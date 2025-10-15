# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Phoenix Rigging Inventory is an Android application for tracking rigging equipment and supplies. Built with Kotlin and Jetpack Compose using Material Design 3.

**Package**: `com.example.phoenixinventory`
**Min SDK**: 28 (Android 9.0 Pie)
**Target SDK**: 36
**Java Version**: 11

## Build Commands

```bash
# Build the project
./gradlew build

# Install debug build on connected device/emulator
./gradlew installDebug

# Run unit tests
./gradlew test

# Run instrumented tests (requires connected device/emulator)
./gradlew connectedAndroidTest

# Clean build
./gradlew clean
```

## Architecture

### Navigation Structure

The app uses Jetpack Navigation Compose with a centralized navigation graph in `AppNav.kt`. All screen destinations are defined in the `Dest` object as string constants:

- Authentication flow: HOME → LOGIN/REGISTER → DASHBOARD
- Item management: ADD_ITEM, VIEW_ALL_ITEMS, ITEM_DETAIL
- Check-in/out: CHECKED_OUT_ITEMS, CHECK_IN_OUT
- Admin: MANAGE_USERS, MANAGE_ITEMS

Navigation is handled through a single `NavHost` in `AppNavHost()` composable, with the NavController passed to screens that need navigation capabilities.

### Data Layer

**In-Memory Repository Pattern**: `DataRepository` (singleton object) manages all app data without persistence. Data resets on app restart.

**Core Models** (`data/Models.kt`):
- `InventoryItem`: Tracks equipment with status (Available, Checked Out, Under Maintenance, Retired, Stolen, Lost, Damaged), condition, value, and permission flags
- `User`: Stores user info with roles (Admin, Manager, Employee)
- `CheckoutRecord`: Links items to users with checkout/checkin timestamps
- `CheckedOutItemDetail`: Composite view combining item, user, checkout record, and days-out calculation

**Repository Operations**:
- Item CRUD: `getAllItems()`, `getItemById()`, `addItem()`, `updateItem()`, `removeItem()`
- User operations: `getAllUsers()`, `getCurrentUser()` (returns first user for demo)
- Checkout flow: `checkOutItem()`, `checkInItem()`, `getCheckedOutItems()`, `getItemsOutLongerThan(days)`
- Dashboard stats: `getTotalValue()`, `getCheckedOutCount()`, `getStolenLostDamagedValue()`, `getStolenLostDamagedCount()`

### Screen Organization

All UI screens are in `core/` directory as standalone composables. Each screen accepts navigation callbacks (`onBack`, `onNavigateTo`, etc.) as parameters rather than directly accessing NavController, except `DashboardScreen` which receives the NavController directly.

**Entry Point**: `MainActivity` sets up theme and renders `AppNavHost()`

## Development Notes

### Dependencies

Core libraries are managed via Gradle version catalog (`libs.versions.toml`):
- Jetpack Compose BOM for compose dependencies
- Navigation Compose 2.8.0
- Material Icons Extended for icon support

### State Management

Currently uses in-memory mutable lists in `DataRepository`. No database or persistence layer implemented. Data is initialized with sample rigging equipment on app launch.

### Navigation Patterns

When adding new screens:
1. Add destination constant to `Dest` object in `AppNav.kt`
2. Add `composable()` entry in `AppNavHost` NavHost
3. Pass navigation callbacks as lambda parameters to screen composables
4. For parameterized routes (e.g., item details), use pattern `"${Dest.SCREEN}/{paramName}"` and extract via `backStackEntry.arguments`
