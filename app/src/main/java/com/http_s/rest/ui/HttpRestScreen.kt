package com.http_s.rest.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.http_s.rest.mvi.HttpRestIntent
import com.http_s.rest.mvi.HttpRestSettings
import com.http_s.rest.mvi.HttpRestState
import com.http_s.rest.mvi.KeyValueItem
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HttpRestScreen(
    state: HttpRestState,
    onIntent: (HttpRestIntent) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("HTTP REST Client", maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(
                            "Compose • Material 3 • MVI",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { onIntent(HttpRestIntent.OpenSettings) }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                actions = {
                    IconButton(onClick = { onIntent(HttpRestIntent.OpenSettings) }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            Surface(tonalElevation = 4.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (state.isLoading) {
                        OutlinedButton(
                            onClick = { onIntent(HttpRestIntent.CancelRequest) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = null)
                            Spacer(Modifier.size(8.dp))
                            Text("Cancel")
                        }
                    }
                    Button(
                        onClick = { onIntent(HttpRestIntent.SendRequest) },
                        enabled = !state.isLoading,
                        modifier = Modifier.weight(1f)
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.Send, contentDescription = null)
                        }
                        Spacer(Modifier.size(8.dp))
                        Text(if (state.isLoading) "Sending" else "Send request")
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    RequestCard(state, onIntent)
                }
                item {
                    KeyValueSection(
                        title = "Headers",
                        subtitle = "Optional request headers",
                        enabled = state.headersEnabled,
                        items = state.headers,
                        onToggle = { onIntent(HttpRestIntent.ToggleHeaders(it)) },
                        onAdd = { onIntent(HttpRestIntent.AddHeader) },
                        onUpdate = { id, name, value -> onIntent(HttpRestIntent.UpdateHeader(id, name, value)) },
                        onDelete = { onIntent(HttpRestIntent.DeleteHeader(it)) }
                    )
                }
                item {
                    KeyValueSection(
                        title = "Variables",
                        subtitle = "Query string or request body fields",
                        enabled = state.variablesEnabled,
                        items = state.variables,
                        onToggle = { onIntent(HttpRestIntent.ToggleVariables(it)) },
                        onAdd = { onIntent(HttpRestIntent.AddVariable) },
                        onUpdate = { id, name, value -> onIntent(HttpRestIntent.UpdateVariable(id, name, value)) },
                        onDelete = { onIntent(HttpRestIntent.DeleteVariable(it)) }
                    )
                }
                item {
                    LocationCard(state.locationText, onRefresh = { onIntent(HttpRestIntent.RefreshLocation) })
                }
            }
            if (state.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter))
            }
        }
    }

    if (state.showSettings) SettingsDialog(state.settings, onIntent)
    if (state.response != null) ResponseDialog(state.response, onDismiss = { onIntent(HttpRestIntent.DismissResponse) })
    if (state.errorMessage != null) ErrorDialog(state.errorMessage, onDismiss = { onIntent(HttpRestIntent.DismissError) })
    if (state.showPrivacyPolicy) TextDialog("Privacy Policy", state.privacyPolicy, onDismiss = { onIntent(HttpRestIntent.ClosePrivacyPolicy) })
    if (state.showAbout) TextDialog("About", "HTTP REST Client\n\nRefactored with Jetpack Compose, Material 3, and MVI state management.", onDismiss = { onIntent(HttpRestIntent.CloseAbout) })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RequestCard(state: HttpRestState, onIntent: (HttpRestIntent) -> Unit) {
    ElevatedCard(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text("Request", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                OutlinedTextField(
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    readOnly = true,
                    value = state.selectedMethod,
                    onValueChange = {},
                    label = { Text("Method") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    state.methods.forEach { method ->
                        DropdownMenuItem(
                            text = { Text(method) },
                            onClick = {
                                onIntent(HttpRestIntent.SelectMethod(method))
                                expanded = false
                            }
                        )
                    }
                }
            }
            OutlinedTextField(
                value = state.url,
                onValueChange = { onIntent(HttpRestIntent.ChangeUrl(it)) },
                label = { Text("URL") },
                singleLine = false,
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun KeyValueSection(
    title: String,
    subtitle: String,
    enabled: Boolean,
    items: List<KeyValueItem>,
    onToggle: (Boolean) -> Unit,
    onAdd: () -> Unit,
    onUpdate: (Long, String?, String?) -> Unit,
    onDelete: (Long) -> Unit
) {
    ElevatedCard(shape = RoundedCornerShape(24.dp)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                    Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(checked = enabled, onCheckedChange = onToggle)
            }
            AnimatedVisibility(enabled) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (items.isEmpty()) {
                        Text("No ${title.lowercase()} added yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    items.forEach { item ->
                        KeyValueRow(item, onUpdate, onDelete)
                    }
                    FilledTonalButton(onClick = onAdd, modifier = Modifier.align(Alignment.End)) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(Modifier.size(8.dp))
                        Text("Add ${title.dropLast(1).lowercase()}")
                    }
                }
            }
        }
    }
}

@Composable
private fun KeyValueRow(
    item: KeyValueItem,
    onUpdate: (Long, String?, String?) -> Unit,
    onDelete: (Long) -> Unit
) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = item.name,
                    onValueChange = { onUpdate(item.id, it, null) },
                    label = { Text("Key") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                IconButton(onClick = { onDelete(item.id) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
            OutlinedTextField(
                value = item.value,
                onValueChange = { onUpdate(item.id, null, it) },
                label = { Text("Value") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = false,
                minLines = 1
            )
        }
    }
}

@Composable
private fun LocationCard(locationText: String, onRefresh: () -> Unit) {
    ElevatedCard(shape = RoundedCornerShape(24.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(Icons.Default.LocationOn, contentDescription = null)
            Text(locationText, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
            AssistChip(onClick = onRefresh, label = { Text("Refresh") })
        }
    }
}

@Composable
private fun SettingsDialog(settings: HttpRestSettings, onIntent: (HttpRestIntent) -> Unit) {
    AlertDialog(
        onDismissRequest = { onIntent(HttpRestIntent.CloseSettings) },
        icon = { Icon(Icons.Default.Settings, contentDescription = null) },
        title = { Text("Settings") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SettingSwitch("Add current GPS location as LATITUDE and LONGITUDE", settings.addLocation) {
                    onIntent(HttpRestIntent.ChangeSettings(settings.copy(addLocation = it)))
                }
                SettingSwitch("Encode GET query string with Base64", settings.encodeGetQueryString) {
                    onIntent(HttpRestIntent.ChangeSettings(settings.copy(encodeGetQueryString = it)))
                }
                SettingSwitch("Encode GET values with Base64", settings.encodeGetValues) {
                    onIntent(HttpRestIntent.ChangeSettings(settings.copy(encodeGetValues = it)))
                }
                SettingSwitch("Encode POST values with Base64", settings.encodePostValues) {
                    onIntent(HttpRestIntent.ChangeSettings(settings.copy(encodePostValues = it)))
                }
                HorizontalDivider()
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = { onIntent(HttpRestIntent.OpenPrivacyPolicy) }) {
                        Icon(Icons.Default.Visibility, contentDescription = null)
                        Spacer(Modifier.size(8.dp))
                        Text("Privacy")
                    }
                    TextButton(onClick = { onIntent(HttpRestIntent.OpenAbout) }) {
                        Icon(Icons.Default.Info, contentDescription = null)
                        Spacer(Modifier.size(8.dp))
                        Text("About")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onIntent(HttpRestIntent.CloseSettings) }) { Text("Close") }
        }
    )
}

@Composable
private fun SettingSwitch(text: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(text, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun ResponseDialog(response: JSONObject, onDismiss: () -> Unit) {
    TextDialog("Response", response.toString(2), onDismiss)
}

@Composable
private fun ErrorDialog(message: String, onDismiss: () -> Unit) {
    TextDialog("Error", message, onDismiss)
}

@Composable
private fun TextDialog(title: String, message: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            LazyColumn(modifier = Modifier.height(360.dp)) {
                item {
                    Text(
                        text = message.ifBlank { "No content available." },
                        style = MaterialTheme.typography.bodyMedium,
                        fontFamily = if (title == "Response") FontFamily.Monospace else FontFamily.Default
                    )
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("OK") } }
    )
}
