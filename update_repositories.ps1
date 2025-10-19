# PowerShell script to update DataRepository to ApiRepository in all screen files
# This script handles the conversion from synchronous to asynchronous API calls

$coreDir = "app\src\main\java\com\example\phoenixinventory\core"

Write-Host "Updating repository references in screen files..." -ForegroundColor Green

# List of files to update
$files = @(
    "CheckedInItemsScreen.kt",
    "CheckedOutItemsScreen.kt",
    "CheckInItemsListScreen.kt",
    "CheckInOutScreen.kt",
    "CheckoutItemsListScreen.kt",
    "ItemCheckoutScreen.kt",
    "ItemDeleteScreen.kt",
    "ItemDetailScreen.kt",
    "ItemEditScreen.kt",
    "ManageUsersScreen.kt",
    "MyCheckedOutItemsScreen.kt",
    "UserEditScreen.kt"
)

foreach ($file in $files) {
    $filePath = Join-Path $coreDir $file

    if (Test-Path $filePath) {
        Write-Host "Processing $file..." -ForegroundColor Yellow

        $content = Get-Content $filePath -Raw

        # Replace import statement
        $content = $content -replace 'import com\.example\.phoenixinventory\.data\.DataRepository', 'import com.example.phoenixinventory.data.ApiRepository'

        # Add coroutines import if not present
        if ($content -notmatch 'import kotlinx\.coroutines\.launch') {
            $content = $content -replace '(import com\.example\.phoenixinventory\.data\.ApiRepository)', "`$1`nimport kotlinx.coroutines.launch"
        }

        # Replace DataRepository with ApiRepository
        $content = $content -replace 'DataRepository\.', 'ApiRepository.'

        Set-Content $filePath -Value $content -NoNewline
        Write-Host "  ✓ Updated $file" -ForegroundColor Green
    } else {
        Write-Host "  ✗ File not found: $file" -ForegroundColor Red
    }
}

Write-Host "`nRepository update complete!" -ForegroundColor Green
Write-Host "`nNOTE: You will need to manually update these files to use coroutines:" -ForegroundColor Cyan
Write-Host "  - Wrap API calls in rememberCoroutineScope().launch { }" -ForegroundColor Cyan
Write-Host "  - Convert 'remember' blocks to use LaunchedEffect for data loading" -ForegroundColor Cyan
Write-Host "  - Add mutableStateOf for API responses" -ForegroundColor Cyan
