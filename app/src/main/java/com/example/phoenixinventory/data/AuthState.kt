package com.example.phoenixinventory.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.firebase.auth.FirebaseAuth

/**
 * Singleton object to manage authentication state across the app.
 * Tracks whether a user is logged in and stores current user data.
 */
object AuthState {
    var isLoggedIn by mutableStateOf(false)
        private set

    private var _currentUser by mutableStateOf<User?>(null)
    val currentUser: User?
        get() = _currentUser

    /**
     * Initialize the AuthState by checking current Firebase Auth status
     * and setting up a listener for auth state changes.
     */
    fun init() {
        val auth = FirebaseAuth.getInstance()
        isLoggedIn = auth.currentUser != null

        // Listen to auth state changes
        auth.addAuthStateListener { firebaseAuth ->
            isLoggedIn = firebaseAuth.currentUser != null
            if (!isLoggedIn) {
                _currentUser = null
            }
        }
    }

    /**
     * Set the current user data after successful login/registration
     */
    fun setCurrentUser(user: User?) {
        _currentUser = user
    }

    /**
     * Clear the current user data (called on logout)
     */
    fun clearUser() {
        _currentUser = null
        isLoggedIn = false
    }
}
