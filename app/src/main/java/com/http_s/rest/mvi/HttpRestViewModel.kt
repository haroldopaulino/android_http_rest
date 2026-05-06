package com.http_s.rest.mvi

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.http_s.rest.R
import com.http_s.rest.Utilities
import com.http_s.rest.location.LocationHelper
import com.http_s.rest.repository.HttpRestRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HttpRestViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = HttpRestRepository(application)
    private val locationHelper = LocationHelper(application)
    private val _state = MutableStateFlow(HttpRestState())
    val state: StateFlow<HttpRestState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<HttpRestEffect>()
    val effects: SharedFlow<HttpRestEffect> = _effects.asSharedFlow()

    private var requestJob: Job? = null

    init {
        loadPersistedState()
        refreshLocation()
    }

    fun dispatch(intent: HttpRestIntent) {
        when (intent) {
            is HttpRestIntent.SelectMethod -> _state.update { it.copy(selectedMethod = intent.method) }
            is HttpRestIntent.ChangeUrl -> _state.update { it.copy(url = intent.url) }
            is HttpRestIntent.ToggleHeaders -> {
                repository.saveSetting("CHECKED_HEADERS_CHECKBOX", if (intent.enabled) "YES" else "NO")
                _state.update { it.copy(headersEnabled = intent.enabled) }
            }
            HttpRestIntent.AddHeader -> _state.update { it.copy(headers = it.headers + KeyValueItem()) }
            is HttpRestIntent.UpdateHeader -> updateHeader(intent.id, intent.name, intent.value)
            is HttpRestIntent.DeleteHeader -> {
                _state.update { it.copy(headers = it.headers.filterNot { item -> item.id == intent.id }) }
                persistHeaders()
            }
            is HttpRestIntent.ToggleVariables -> {
                repository.saveSetting("CHECKED_VARIABLES_CHECKBOX", if (intent.enabled) "YES" else "NO")
                _state.update { it.copy(variablesEnabled = intent.enabled) }
            }
            HttpRestIntent.AddVariable -> _state.update { it.copy(variables = it.variables + KeyValueItem()) }
            is HttpRestIntent.UpdateVariable -> updateVariable(intent.id, intent.name, intent.value)
            is HttpRestIntent.DeleteVariable -> {
                _state.update { it.copy(variables = it.variables.filterNot { item -> item.id == intent.id }) }
                persistVariables()
            }
            HttpRestIntent.SendRequest -> sendRequest()
            HttpRestIntent.CancelRequest -> cancelRequest()
            HttpRestIntent.DismissResponse -> _state.update { it.copy(response = null) }
            HttpRestIntent.DismissError -> _state.update { it.copy(errorMessage = null) }
            HttpRestIntent.OpenSettings -> _state.update { it.copy(showSettings = true) }
            HttpRestIntent.CloseSettings -> _state.update { it.copy(showSettings = false) }
            is HttpRestIntent.ChangeSettings -> changeSettings(intent.settings)
            HttpRestIntent.RefreshLocation -> refreshLocation()
            is HttpRestIntent.LocationPermissionResult -> if (intent.granted) refreshLocation() else {
                _state.update { it.copy(locationText = getApplication<Application>().getString(R.string.location_permission_denied)) }
            }
            HttpRestIntent.OpenPrivacyPolicy -> _state.update { it.copy(showPrivacyPolicy = true, privacyPolicy = repository.readAssetText("privacy_policy.txt")) }
            HttpRestIntent.ClosePrivacyPolicy -> _state.update { it.copy(showPrivacyPolicy = false) }
            HttpRestIntent.OpenAbout -> _state.update { it.copy(showAbout = true) }
            HttpRestIntent.CloseAbout -> _state.update { it.copy(showAbout = false) }
        }
    }

    private fun loadPersistedState() {
        val headers = repository.getFields("header").map { KeyValueItem(name = it.first, value = it.second) }
        val variables = repository.getFields("variable").map { KeyValueItem(name = it.first, value = it.second) }
        _state.update {
            it.copy(
                headers = headers,
                variables = variables,
                headersEnabled = repository.getSetting("CHECKED_HEADERS_CHECKBOX").equals("YES", true),
                variablesEnabled = repository.getSetting("CHECKED_VARIABLES_CHECKBOX").equals("YES", true),
                settings = HttpRestSettings(
                    addLocation = repository.getSetting(Utilities.LOCATION_SETTING).equals("YES", true),
                    encodeGetQueryString = repository.getSetting(Utilities.ENCODE_GET_QUERY_STRING).equals("YES", true),
                    encodeGetValues = repository.getSetting(Utilities.ENCODE_GET_VALUES_SETTING).equals("YES", true),
                    encodePostValues = repository.getSetting(Utilities.ENCODE_POST_VALUES_SETTING).equals("YES", true)
                )
            )
        }
    }

    private fun updateHeader(id: Long, name: String?, value: String?) {
        _state.update { current ->
            current.copy(headers = current.headers.map {
                if (it.id == id) it.copy(name = name ?: it.name, value = value ?: it.value) else it
            })
        }
        persistHeaders()
    }

    private fun updateVariable(id: Long, name: String?, value: String?) {
        _state.update { current ->
            current.copy(variables = current.variables.map {
                if (it.id == id) it.copy(name = name ?: it.name, value = value ?: it.value) else it
            })
        }
        persistVariables()
    }

    private fun persistHeaders() {
        repository.replaceFields("header", state.value.headers.map { it.name to it.value })
    }

    private fun persistVariables() {
        repository.replaceFields("variable", state.value.variables.map { it.name to it.value })
    }

    private fun changeSettings(settings: HttpRestSettings) {
        repository.saveSetting(Utilities.LOCATION_SETTING, if (settings.addLocation) "YES" else "NO")
        repository.saveSetting(Utilities.ENCODE_GET_QUERY_STRING, if (settings.encodeGetQueryString) "YES" else "NO")
        repository.saveSetting(Utilities.ENCODE_GET_VALUES_SETTING, if (settings.encodeGetValues) "YES" else "NO")
        repository.saveSetting(Utilities.ENCODE_POST_VALUES_SETTING, if (settings.encodePostValues) "YES" else "NO")
        _state.update { it.copy(settings = settings) }
        if (settings.addLocation) refreshLocation()
    }

    private fun sendRequest() {
        val snapshot = state.value
        if (snapshot.url.isBlank()) {
            _state.update { it.copy(errorMessage = "Enter a URL before sending the request.") }
            return
        }
        requestJob?.cancel()
        requestJob = viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null, response = null) }
            try {
                val response = repository.sendRequest(
                    method = snapshot.selectedMethod,
                    url = snapshot.url.trim(),
                    headers = snapshot.headers.map { it.name to it.value },
                    variables = snapshot.variables.map { it.name to it.value }
                )
                _state.update { it.copy(isLoading = false, response = response) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message ?: "Unable to complete HTTP request.") }
            }
        }
    }

    private fun cancelRequest() {
        requestJob?.cancel()
        _state.update { it.copy(isLoading = false) }
    }

    private fun refreshLocation() {
        val app = getApplication<Application>()
        val hasPermission = ContextCompat.checkSelfPermission(app, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(app, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (!hasPermission) {
            viewModelScope.launch { _effects.emit(HttpRestEffect.RequestLocationPermission) }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(locationText = app.getString(R.string.fetching_location)) }
            val location = locationHelper.getGpsLocation()
            if (location == null) {
                _state.update { it.copy(locationText = app.getString(R.string.location_not_available)) }
            } else {
                repository.saveSetting(Utilities.LATITUDE, location.latitude.toString())
                repository.saveSetting(Utilities.LONGITUDE, location.longitude.toString())
                _state.update {
                    it.copy(locationText = "${app.getString(R.string.my_latitude)}${location.latitude}\n${app.getString(R.string.my_longitude)}${location.longitude}")
                }
            }
        }
    }
}
