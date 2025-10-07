# Phoenix Rigging Inventory

An Android inventory management application built with Kotlin and Jetpack Compose for tracking rigging equipment and supplies.

## Features

- User authentication (Login/Register)
- Dashboard for inventory overview
- Add and manage inventory items
- Modern Material Design 3 UI
- Navigation with Jetpack Navigation Compose

## Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: Material Design 3
- **Minimum SDK**: 28 (Android 9.0 Pie)
- **Target SDK**: 36
- **Build System**: Gradle with Kotlin DSL

## Dependencies

- AndroidX Core KTX
- Lifecycle Runtime KTX
- Activity Compose
- Compose BOM
- Material 3 Components
- Material Icons Extended
- Navigation Compose
- JUnit & Espresso for testing

## Project Structure

```
app/src/main/java/com/example/phoenixinventory/
├── core/
│   ├── AddItemScreen.kt
│   ├── AppNav.kt
│   ├── DashboardScreen.kt
│   ├── HomeScreen.kt
│   ├── LoginScreen.kt
│   └── RegisterScreen.kt
├── ui/
└── MainActivity.kt
```

## Getting Started

### Prerequisites

- Android Studio Hedgehog or later
- JDK 11 or higher
- Android SDK 36

### Building the Project

1. Clone the repository
2. Open the project in Android Studio
3. Sync Gradle files
4. Run the app on an emulator or physical device

```bash
./gradlew build
```

### Running the App

Connect an Android device or start an emulator, then:

```bash
./gradlew installDebug
```

Or use Android Studio's Run button.

## Development

### Build Variants

- **Debug**: Development build with debugging enabled
- **Release**: Production build with ProGuard optimization

### Testing

Run unit tests:
```bash
./gradlew test
```

Run instrumented tests:
```bash
./gradlew connectedAndroidTest
```

## License

Copyright (c) 2025 Phoenix Rigging Inventory

## Version

- Version Code: 1
- Version Name: 1.0
