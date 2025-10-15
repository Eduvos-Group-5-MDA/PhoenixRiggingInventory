package com.example.myapp.controller

import android.content.Context
import java.security.MessageDigest //hashing
import com.example.myapp.model.ResultCallback
import com.example.myapp.model.SimpleCallback

import com.example.myapp.model.User
import com.example.myapp.model.UserModel
import com.example.myapp.view //.file


class UserController(context: Context) {
    private val model = UserModel(context)

    fun hashText(input: String): String {
	val bytes = input.toByteArray(Charsets.UTF_8)
	val md = MessageDigest.getInstance("SHA-256") //the hash
	val digest = md.digest(bytes)
    return digest.joinToString("") { "%02x".format(it) }
    }

    fun validateID(userId: String): Boolean {

	val id = userId.toIntOrNull()
	val isValid = ((id != null) && (id > -1))

	if (!isValid)
		view.invalidUserID() //placeholder
	return isValid
    }

    fun translateRole(role: String): Int {
	if (role == "Guest") return 0
	if (role == "Employee") return 1
	if (role == "Admin") return 2

	view.invalidRole()
	return null
    } 

    fun validateAll(
	userId: String,
	name: String,
	surname: String,
	email: String,
	phoneNum: String,
	ZAID: String,
	password: String
	): Boolean {
	
	if (!validateID(userId)){
		return false
	}
	if ( (name.isBlank()) || (name.length > 50) ){
		view.invalidName()
		return false
	}
	if ( (surname.isBlank()) || (surname.length > 50) ){
		view.invalidSurname()
		return false
	}
	if ( (email.isBlank()) || (email.length < 6) || (email.length > 50) || (!email.contains("@")) ){ //regex lmao
		view.invalidEmail()
		return false
	}
	if ( (phoneNum.toLongOrNull() == null) || (phoneNum.length != 10)){ //Long refers to bigger ints (>10 digits)
		view.invalidPhoneNum()
		return false
	}
	if ( (ZAID.toLongOrNull() == null) || (ZAID.length != 13) ){
		view.invalidZAID()
		return false
	}
	if ( (password.isBlank()) || (password.length > 50) ){ //probably needs a minimum
		view.invalidPassword()
		return false
	}

	return true

	//driversLicense should be autoselected as False; thus cannot be null
	//role is read in as str and translated elsewhere, cannot be invalid
    }

    suspend fun fetchAllUsers(callback: ResultCallback<List<User>>) { //√
        // No coroutine usage inside model (Volley), so call directly.*
        model.getAllUsers(callback) //no validation
    }

    fun fetchUserById(userId: String, callback: ResultCallback<User>) { //√
	if (validateID(userId)) {
		user = model.getUserById(userId.toInt(), callback)
		if user.name == null { //ID not in DB
			view.UserDNE()
		} 
		return user //so that front-end can use user.name etc. to populate
/*front-end:

userId = label.text
user = controller.fetchUserById(userId)

label.text = user.name

:)*/
	}
    }

    fun createUser(
	userId: String,
	name: String,
	surname: String,
	email: String,
	phoneNum: String,
	ZAID: String,
	role: String, //'Guest','Employee','Admin'
	driversLicense: Boolean,
	password: String,
	callback: ResultCallback<User>
	) {
        if (validateAll(userId, name, surname, email, phoneNum, ZAID, password)) { //if true ?:)
	        val user = User(
			userId = userId.toInt(),
			name = name,
			surname = surname,
			email = email,
			phoneNum = phoneNum,
			ZAID = ZAID,
			role = translateRole(role),
			driversLicense = driversLicense,
			password = hashText(password)
			)
        	model.createUser(user, callback)
	}
    }

    fun updateUser(
	userId: String,
	name: String,
	surname: String,
	email: String,
	phoneNum: String,
	ZAID: String,
	role: String,
	driversLicense: Boolean,
	password: String,
	callback: ResultCallback<User>
	) {
        if (validateAll(userId, name, surname, email, phoneNum, ZAID, password)) { if true
	        val user = User(
			userId = userId.toInt(),
			name = name,
			surname = surname,
			email = email,
			phoneNum = phoneNum,
			ZAID = ZAID,
			role = translateRole(role),
			driversLicense = driversLicense,
			password = hashText(password)
			)
        	model.updateUser(user, callback)
	}
    }

spot any errors/issues/googidy-gag/gibbidy-gook?

    fun deleteUser(userId: String, callback: SimpleCallback) { //√
	if (validateID(userId)) {
		model.deleteUser(userId.toInt(), callback)
	}
    }
}
