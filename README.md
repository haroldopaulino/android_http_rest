# Android HTTP REST Client

Android HTTP REST Client is a modern Android application for manually testing HTTP endpoints from an Android device. The project is a Jetpack Compose application using Material 3, a dark theme, and an MVI-style presentation layer.

The behavior: choose an HTTP method, enter a URL, optionally add headers, optionally add variables, optionally include the current device latitude and longitude, optionally Base64-encode outgoing values, send the request, and review the response details.

<img width="2437" height="1334" alt="http_rest" src="https://github.com/user-attachments/assets/3778ffc6-0bd6-42af-a5f9-866a130b8d10" />

## Technical summary

| Area | Implementation |
| --- | --- |
| UI toolkit | Jetpack Compose |
| Design system | Material 3 |
| Theme | Dark theme by default |
| Architecture | MVI-style state, intent, and effect contracts |
| State holder | AndroidViewModel with StateFlow and SharedFlow |
| Persistence | SQLiteOpenHelper-backed local database |
| HTTP stack | HttpURLConnection wrapped inside a repository |
| Location | Google Play Services Fused Location Provider |
| Gradle | Gradle wrapper 9.4.1 |
| Android Gradle Plugin | 9.2.0 |
| Kotlin / Compose plugin | org.jetbrains.kotlin.plugin.compose 2.3.20 |
| SDK | compileSdk 36, targetSdk 36, minSdk 23 |

## Project structure

```text
.
├── app
│   ├── build.gradle
│   ├── proguard-rules.pro
│   └── src
│       ├── androidTest
│       ├── main
│       │   ├── AndroidManifest.xml
│       │   ├── assets
│       │   ├── java/com/http_s/rest
│       │   │   ├── MainActivity.kt
│       │   │   ├── Utilities.kt
│       │   │   ├── data
│       │   │   │   └── SqliteCore.kt
│       │   │   ├── location
│       │   │   │   └── LocationHelper.kt
│       │   │   ├── mvi
│       │   │   │   ├── HttpRestContract.kt
│       │   │   │   └── HttpRestViewModel.kt
│       │   │   ├── repository
│       │   │   │   └── HttpRestRepository.kt
│       │   │   └── ui
│       │   │       ├── HttpRestScreen.kt
│       │   │       └── Theme.kt
│       │   └── res
│       └── test
├── build.gradle
├── gradle.properties
├── gradle/wrapper/gradle-wrapper.properties
└── settings.gradle
```

Material 3 components used include `Scaffold`, `TopAppBar`, `ElevatedCard`, `OutlinedTextField`, `Button`, `OutlinedButton`, `FilledTonalButton`, `Switch`, `AssistChip`, `AlertDialog`, and progress indicators.

## MVI architecture

The presentation layer is split into state, intents, effects, and a ViewModel.

### Effects

`HttpRestEffect` is used for one-time events that should not be stored as durable screen state. The current effect is `RequestLocationPermission`, which is collected by `MainActivity` and handled by the Activity Result API.

### ViewModel

`HttpRestViewModel` owns the MVI reducer logic. It exposes:

| Flow | Purpose |
| --- | --- |
| `state: StateFlow<HttpRestState>` | The current UI state observed by Compose |
| `effects: SharedFlow<HttpRestEffect>` | One-time side effects observed by `MainActivity` |

The ViewModel is responsible for loading persisted state, updating headers and variables, persisting settings, requesting location refreshes, starting HTTP requests, cancelling active requests, and publishing success/error state.

## Data layer

### Repository

`HttpRestRepository` is the data and networking boundary for the app. It coordinates:

| Responsibility | Details |
| --- | --- |
| Settings access | Reads and writes persisted app settings through `SqliteCore` |
| Header/variable persistence | Loads and replaces saved key/value request fields |
| Asset loading | Reads `privacy_policy.txt` from assets |
| Request execution | Sends HTTP requests using `HttpURLConnection` on `Dispatchers.IO` |
| Request metadata | Stores request/response timestamps and HTTP metadata in SQLite |
| Query/body formatting | Builds URL-encoded request variables and applies optional Base64 encoding |
| Location injection | Adds `LATITUDE` and `LONGITUDE` when the location setting is enabled |

### SQLite storage

`SqliteCore` keeps the original lightweight SQLite approach. It uses two tables:

| Table | Columns | Purpose |
| --- | --- | --- |
| `settings` | `setting_name`, `setting_value` | Stores settings, request metadata, and latest location values |
| `headers_variables` | `field_type`, `field_name`, `field_value` | Stores saved header and variable rows |

The data layer keeps the existing database name, `httprest.db`, and version `2`.

## HTTP behavior

The app supports these methods:

```text
GET, POST, OPTIONS, HEAD, PUT, DELETE, TRACE
```

For `GET` requests, variables are appended to the URL query string. For `POST`, `PUT`, and `DELETE`, variables are written as an `application/x-www-form-urlencoded` request body when variables are present.

Default request headers include:

```text
Content-Type: application/x-www-form-urlencoded; charset=UTF-8
Content-Language: en-US
```

User-entered headers are added after the default headers. Blank header names are ignored.

The response dialog displays a formatted JSON object with:

| JSON field | Meaning |
| --- | --- |
| `response_code` | Numeric HTTP response code |
| `response_code_description` | Legacy descriptive HTTP label |
| `response_code_message` | Response message from the connection |
| `response_message` | Response body text |
| `headers` | Response headers |

## Location behavior

`LocationHelper` uses `FusedLocationProviderClient` and requests high-accuracy location updates. `MainActivity` handles runtime location permission requests through `ActivityResultContracts.RequestMultiplePermissions`.

When location injection is enabled in settings, the repository adds two outgoing variables:

```text
LATITUDE=<last saved latitude>
LONGITUDE=<last saved longitude>
```

The latest location values are persisted in SQLite so they can be reused by the request layer.

The header and variable enabled switches are persisted using:

```text
CHECKED_HEADERS_CHECKBOX
CHECKED_VARIABLES_CHECKBOX
```
