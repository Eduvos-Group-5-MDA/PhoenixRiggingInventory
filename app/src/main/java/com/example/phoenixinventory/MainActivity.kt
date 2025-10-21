package com.example.phoenixinventory

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import com.example.phoenixinventory.core.AppNavHost
import com.example.phoenixinventory.ui.theme.PhoenixInventoryTheme
import com.google.firebase.FirebaseApp
import com.example.phoenixinventory.data.AuthState
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        // Note: Firebase App Check is disabled for now to allow password reset to work
        // Password reset emails will be sent without App Check verification
        // To enable App Check later, uncomment the code below and register debug token in Firebase Console

        /*
        // Initialize Firebase App Check for reCAPTCHA and security
        try {
            val firebaseAppCheck = FirebaseAppCheck.getInstance()
            val debugProvider = DebugAppCheckProviderFactory.getInstance()
            firebaseAppCheck.installAppCheckProviderFactory(debugProvider)
            Log.d("MainActivity", "Firebase App Check initialized with Debug provider")
        } catch (e: Exception) {
            Log.e("MainActivity", "Failed to initialize Firebase App Check", e)
        }
        */

        Log.d("MainActivity", "App Check is disabled - password reset will work without extra verification")


        // Initialize auth state
        AuthState.init()

        setContent {
            PhoenixInventoryTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    AppNavHost()
                }
            }
        }
    }
}