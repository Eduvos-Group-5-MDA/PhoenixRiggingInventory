//VALIDATION MISSING

package com.example.myapp.controller

import android.content.Context
import com.example.myapp.model.ResultCallback
import com.example.myapp.model.SimpleCallback
import com.example.myapp.model.User
import com.example.myapp.model.UserModel

class UserController(context: Context) {
    private val model = UserModel(context)

    suspend fun fetchAllUsers(callback: ResultCallback<List<User>>) {
        // No coroutine usage inside model (Volley), so call directly.
        model.getAllUsers(callback)
    }

    fun fetchUserById(callback: ResultCallback<User>) {
	//validation
	model.getUserById(callback)
    }

    fun createUser(name: String, email: String, role: String?, callback: ResultCallback<User>) {
        // validation
        val user = User(name = name, email = email, role = role)
        model.createUser(user, callback)
    }

    fun updateUser(name: String, email: String, role: String?, callback: SimpleCallback) {
	// validation
        val user = User(name = name, email = email, role = role)
        model.updateUser(user, callback)
    }

    fun deleteUser(userId: Int, callback: SimpleCallback) {
        // validation
        model.deleteUser(userId, callback)
    }
}
