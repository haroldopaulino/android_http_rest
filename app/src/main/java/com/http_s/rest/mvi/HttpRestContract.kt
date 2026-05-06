package com.http_s.rest.mvi

import org.json.JSONObject

data class KeyValueItem(
    val id: Long = System.nanoTime(),
    val name: String = "",
    val value: String = ""
)

data class HttpRestSettings(
    val addLocation: Boolean = false,
    val encodeGetQueryString: Boolean = false,
    val encodeGetValues: Boolean = false,
    val encodePostValues: Boolean = false
)

data class HttpRestState(
    val methods: List<String> = listOf("GET", "POST", "OPTIONS", "HEAD", "PUT", "DELETE", "TRACE"),
    val selectedMethod: String = "GET",
    val url: String = "https://sparqm.com/web/http_rest/gateway.php",
    val headersEnabled: Boolean = false,
    val variablesEnabled: Boolean = false,
    val headers: List<KeyValueItem> = emptyList(),
    val variables: List<KeyValueItem> = emptyList(),
    val settings: HttpRestSettings = HttpRestSettings(),
    val locationText: String = "My Latitude: ...\nMy Longitude: ...",
    val isLoading: Boolean = false,
    val response: JSONObject? = null,
    val errorMessage: String? = null,
    val showSettings: Boolean = false,
    val privacyPolicy: String = "",
    val showPrivacyPolicy: Boolean = false,
    val showAbout: Boolean = false
)

sealed interface HttpRestIntent {
    data class SelectMethod(val method: String) : HttpRestIntent
    data class ChangeUrl(val url: String) : HttpRestIntent
    data class ToggleHeaders(val enabled: Boolean) : HttpRestIntent
    data object AddHeader : HttpRestIntent
    data class UpdateHeader(val id: Long, val name: String? = null, val value: String? = null) : HttpRestIntent
    data class DeleteHeader(val id: Long) : HttpRestIntent
    data class ToggleVariables(val enabled: Boolean) : HttpRestIntent
    data object AddVariable : HttpRestIntent
    data class UpdateVariable(val id: Long, val name: String? = null, val value: String? = null) : HttpRestIntent
    data class DeleteVariable(val id: Long) : HttpRestIntent
    data object SendRequest : HttpRestIntent
    data object CancelRequest : HttpRestIntent
    data object DismissResponse : HttpRestIntent
    data object DismissError : HttpRestIntent
    data object OpenSettings : HttpRestIntent
    data object CloseSettings : HttpRestIntent
    data class ChangeSettings(val settings: HttpRestSettings) : HttpRestIntent
    data object RefreshLocation : HttpRestIntent
    data class LocationPermissionResult(val granted: Boolean) : HttpRestIntent
    data object OpenPrivacyPolicy : HttpRestIntent
    data object ClosePrivacyPolicy : HttpRestIntent
    data object OpenAbout : HttpRestIntent
    data object CloseAbout : HttpRestIntent
}

sealed interface HttpRestEffect {
    data object RequestLocationPermission : HttpRestEffect
}
