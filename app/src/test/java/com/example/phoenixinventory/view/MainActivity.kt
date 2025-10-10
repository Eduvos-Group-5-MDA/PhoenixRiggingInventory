package com.example.myapp.view

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.myapp.controller.AssetController
import com.example.myapp.controller.UserController
import com.example.myapp.model.Asset
import com.example.myapp.model.Callbacks
import com.example.myapp.model.User

class MainActivity : AppCompatActivity() {

    private lateinit var userController: UserController
    private lateinit var assetController: AssetController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        userController = UserController(this)
        assetController = AssetController(this)

        // Fetch assets (read)
        assetController.fetchAllAssets(object : ResultCallback<List<Asset>> {
            override fun onSuccess(result: List<Asset>) {
                for (a in result) {
                    Log.d("MainActivity", "Asset: ${a.name} - ${a.status} @ ${a.location}")
                }
            }
            override fun onError(error: String) {
                Log.e("MainActivity", "Asset fetch error: $error")
            }
        })

        // Create user (example)
        userController.createUser("Test User", "test@example.com", "staff", object : ResultCallback<User> {
            override fun onSuccess(result: User) {
                Log.d("MainActivity", "Created user id=${result.userId}")
            }
            override fun onError(error: String) {
                Log.e("MainActivity", "Create user error: $error")
            }
        })
    }
}
