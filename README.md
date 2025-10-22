# Phoenix Rigging Inventory

A comprehensive Android inventory management application built with Kotlin and Jetpack Compose for tracking rigging equipment and supplies. This application provides real-time inventory tracking, user management, check-in/check-out functionality, and detailed reporting capabilities.

## Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [User Roles](#user-roles)
- [Core Features](#core-features)
- [Firebase Integration](#firebase-integration)
- [Building & Running](#building--running)
- [Testing](#testing)
- [Version](#version)
- [License](#license)

## Features

### Authentication & User Management
- User registration with email verification
- Secure login with Firebase Authentication
- Password reset functionality
- Role-based access control (Admin, Manager, Employee)
- User profile management

### Inventory Management
- Add, edit, and delete inventory items
- Comprehensive item details (name, serial ID, description, category, condition, value)
- Item status tracking (Available, Checked Out, Lost, Stolen, Damaged)
- Soft delete with restoration capability
- Real-time inventory updates
- Search and filter functionality

### Check-In/Check-Out System
- Quick check-out process for employees
- Check-in tracking with condition verification
- Permanent checkout option for assigned equipment
- Permission requirements for restricted items
- Driver's license requirement for applicable items
- Checkout history and audit trail

### Dashboard & Analytics
- Real-time inventory overview
- Total items count
- Checked out items tracking
- Total inventory value (Admin/Manager)
- Items checked out for 30+ days alert
- Lost/damaged/stolen items tracking with value totals

### Advanced Statistics (Admin/Manager Only)
- **Items Out 30+ Days**: Detailed view of overdue items with user information
- **Total Value Report**: Complete inventory valuation with item-by-item breakdown
- **Lost/Damaged/Deleted Report**: Comprehensive report of problem items with total lost value

### Reporting System
- User-submitted issue reports
- Report management for admins/managers
- Report status tracking
- Detailed report viewing

### User Interface
- Modern Material Design 3 UI
- Dark/Light mode support
- Responsive layouts
- Intuitive navigation
- Custom theming with consistent color palette

## Tech Stack

### Core Technologies
- **Language**: Kotlin 1.9+
- **UI Framework**: Jetpack Compose
- **Design System**: Material Design 3
- **Navigation**: Jetpack Navigation Compose
- **Backend**: Firebase (Firestore, Authentication, App Check)

### Android Specifications
- **Minimum SDK**: 28 (Android 9.0 Pie)
- **Target SDK**: 36
- **Compile SDK**: 36
- **Build System**: Gradle with Kotlin DSL
- **JVM Target**: Java 11

### Key Dependencies
- AndroidX Core KTX
- Lifecycle Runtime KTX
- Activity Compose
- Compose BOM (Bill of Materials)
- Material 3 Components
- Material Icons Extended
- Navigation Compose
- Firebase BOM 34.4.0
  - Firebase Analytics
  - Firebase Firestore
  - Firebase Authentication
  - Firebase App Check (Play Integrity & Debug)
- Kotlinx Coroutines
- JUnit & Espresso for testing

## Architecture

The application follows a modern Android architecture pattern:

- **UI Layer**: Jetpack Compose with Material 3 components
- **Data Layer**: Firebase Firestore for real-time data synchronization
- **Authentication**: Firebase Authentication for secure user management
- **Navigation**: Single-activity architecture with Jetpack Navigation
- **State Management**: Compose state management with coroutines
- **Security**: Firebase App Check for backend protection

## Project Structure

```
PhoenixRigginInventory/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/phoenixinventory/
│   │   │   │   ├── core/                    # UI Screens
│   │   │   │   │   ├── AddItemScreen.kt
│   │   │   │   │   ├── AppNav.kt           # Navigation graph
│   │   │   │   │   ├── CheckInItemsListScreen.kt
│   │   │   │   │   ├── CheckInOutScreen.kt
│   │   │   │   │   ├── CheckedInItemsScreen.kt
│   │   │   │   │   ├── CheckedOutItemsScreen.kt
│   │   │   │   │   ├── CheckoutItemsListScreen.kt
│   │   │   │   │   ├── DashboardScreen.kt
│   │   │   │   │   ├── ForgotPasswordScreen.kt
│   │   │   │   │   ├── HomeScreen.kt
│   │   │   │   │   ├── ItemCheckoutScreen.kt
│   │   │   │   │   ├── ItemDeleteScreen.kt
│   │   │   │   │   ├── ItemDetailScreen.kt
│   │   │   │   │   ├── ItemEditScreen.kt
│   │   │   │   │   ├── LoginScreen.kt
│   │   │   │   │   ├── ManageScreen.kt
│   │   │   │   │   ├── ManageUsersScreen.kt
│   │   │   │   │   ├── MyCheckedOutItemsScreen.kt
│   │   │   │   │   ├── RegisterScreen.kt
│   │   │   │   │   ├── ReportDetailScreen.kt
│   │   │   │   │   ├── StatsScreen.kt
│   │   │   │   │   ├── SubmitReportScreen.kt
│   │   │   │   │   ├── TermsPrivacyScreen.kt
│   │   │   │   │   ├── UserEditScreen.kt
│   │   │   │   │   ├── ViewAllItemsScreen.kt
│   │   │   │   │   ├── ViewDeletedItemsScreen.kt
│   │   │   │   │   ├── ViewItemsOut30DaysScreen.kt
│   │   │   │   │   ├── ViewLostDamagedDeletedScreen.kt
│   │   │   │   │   ├── ViewReportsScreen.kt
│   │   │   │   │   └── ViewTotalValueScreen.kt
│   │   │   │   ├── data/                    # Data layer
│   │   │   │   │   ├── DataRepository.kt
│   │   │   │   │   ├── FirebaseRepository.kt
│   │   │   │   │   └── Models.kt
│   │   │   │   ├── ui/                      # UI components & theme
│   │   │   │   │   └── theme/
│   │   │   │   │       ├── AppColors.kt
│   │   │   │   │       ├── Color.kt
│   │   │   │   │       ├── Theme.kt
│   │   │   │   │       └── Type.kt
│   │   │   │   └── MainActivity.kt
│   │   │   ├── res/                         # Resources
│   │   │   └── AndroidManifest.xml
│   │   └── test/                            # Unit tests
│   │   └── androidTest/                     # Instrumented tests
│   ├── build.gradle.kts
│   └── google-services.json                 # Firebase configuration
├── backend/                                 # Optional backend services
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

## Getting Started

### Prerequisites

- **Android Studio**: Hedgehog (2023.1.1) or later
- **JDK**: 11 or higher
- **Android SDK**: Level 36
- **Firebase Project**: Set up a Firebase project with Firestore and Authentication enabled
- **Git**: For version control

### Initial Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd PhoenixRigginInventory
   ```

2. **Firebase Configuration**
   - Create a Firebase project at [Firebase Console](https://console.firebase.google.com)
   - Enable Firebase Authentication (Email/Password)
   - Enable Cloud Firestore
   - Download `google-services.json` and place it in the `app/` directory
   - Enable Firebase App Check for production security

3. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an Existing Project"
   - Navigate to the cloned directory
   - Wait for Gradle sync to complete

4. **Build the project**
   ```bash
   ./gradlew build
   ```

### Firebase Firestore Setup

Create the following collections in Firestore:

1. **users** collection
   - Fields: name, email, role, createdAt

2. **items** collection
   - Fields: name, serialId, description, category, condition, status, value, deleted, etc.

3. **checkouts** collection
   - Fields: itemId, userId, checkedOutAt, checkedInAt, etc.

4. **reports** collection
   - Fields: title, description, userId, userName, userEmail, status, createdAt

## User Roles

### Employee
- View all items
- Check out/check in items
- View personal checked-out items
- Submit reports

### Manager
- All Employee permissions
- View all checked-out/checked-in items
- Access statistics and reports
- View user reports
- Manage item status

### Admin
- All Manager permissions
- Add, edit, delete items
- Manage users (edit roles)
- View deleted items
- Restore deleted items
- Full system access

## Core Features

### Item Management
Items have the following properties:
- **Name**: Item identification
- **Serial ID**: Unique identifier
- **Description**: Detailed information
- **Category**: Classification (Tools, Safety Equipment, Hardware, Electronics, Vehicles, Other)
- **Condition**: Current state (Excellent, Good, Fair, Poor, Damaged)
- **Status**: Availability (Available, Checked Out, Lost, Stolen, Damaged)
- **Value**: Monetary worth (in Rands - R)
- **Permanent Checkout**: Flag for assigned equipment
- **Permission Needed**: Requires approval
- **Driver's License Needed**: For vehicle/heavy equipment

### Check-Out Process
1. Employee selects an item
2. Optional notes and photos
3. System records checkout with timestamp
4. Item status updated to "Checked Out"
5. Checkout record created with user information

### Check-In Process
1. Employee selects their checked-out item
2. Verifies condition
3. Optional notes and photos
4. System records check-in with timestamp
5. Item status updated to "Available"

### Statistics Dashboard

**For All Users:**
- Total items count
- Personal checked-out items count

**For Admin/Manager:**
- Total inventory value
- Items out 30+ days with alerts
- Lost/stolen/damaged items with value totals
- Comprehensive statistics reports

## Firebase Integration

### Authentication
- Email/password authentication
- Password reset via email
- User session management
- Role-based access control

### Firestore Database
- Real-time data synchronization
- Offline persistence
- Structured data collections
- Efficient querying with indexes

### App Check
- Play Integrity verification
- Debug provider for development
- Backend API protection

## Building & Running

### Debug Build

```bash
./gradlew assembleDebug
```

### Release Build

```bash
./gradlew assembleRelease
```

### Install on Device

```bash
./gradlew installDebug
```

### Run from Android Studio

1. Connect an Android device (USB debugging enabled) or start an emulator
2. Click the "Run" button (green play icon)
3. Select your device
4. Wait for installation and launch

## Testing

### Run Unit Tests

```bash
./gradlew test
```

### Run Instrumented Tests

```bash
./gradlew connectedAndroidTest
```

### Lint Checks

```bash
./gradlew lint
```

## Version

- **Version Code**: 1
- **Version Name**: 1.0.30
- **Last Updated**: 2025

## Security Considerations

- Firebase App Check enabled for production
- Role-based access control enforced
- Secure password authentication
- Input validation on all forms
- Firestore security rules configured
- No sensitive data stored locally

## Future Enhancements

- Barcode/QR code scanning for items
- Export reports to PDF/Excel
- Push notifications for overdue items
- Bulk operations for items
- Advanced analytics and charts
- Item maintenance scheduling
- Photo attachments for items
- Location tracking for items

## Troubleshooting

### Common Issues

1. **Build fails**:
   - Ensure `google-services.json` is in the `app/` directory
   - Sync Gradle files
   - Clean and rebuild project

2. **Firebase errors**:
   - Verify Firebase configuration
   - Check internet connectivity
   - Ensure Firebase services are enabled

3. **Authentication issues**:
   - Verify email/password is enabled in Firebase Console
   - Check App Check configuration

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

Copyright (c) 2025 Phoenix Rigging Inventory

All rights reserved.

## Support

For issues, questions, or contributions, please contact the development team or open an issue in the repository.

---

**Built with Kotlin and Jetpack Compose**
