package com.http_s.rest

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.http_s.rest.mvi.HttpRestEffect
import com.http_s.rest.mvi.HttpRestIntent
import com.http_s.rest.mvi.HttpRestViewModel
import com.http_s.rest.ui.HttpRestScreen
import com.http_s.rest.ui.HttpRestTheme

class MainActivity : ComponentActivity() {
    private val viewModel: HttpRestViewModel by viewModels()

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val granted = result[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            result[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        viewModel.dispatch(HttpRestIntent.LocationPermissionResult(granted))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HttpRestTheme(darkTheme = true) {
                val state by viewModel.state.collectAsStateWithLifecycle()
                LaunchedEffect(Unit) {
                    viewModel.effects.collect { effect ->
                        when (effect) {
                            HttpRestEffect.RequestLocationPermission -> locationPermissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        }
                    }
                }
                HttpRestScreen(
                    state = state,
                    onIntent = viewModel::dispatch
                )
            }
        }
    }
}
