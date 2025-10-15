package com.example.myapp.model

data class User(
    val userId: Int,
    val name: String,
    val surname: String,
    val email: String,
    val phoneNum: String,
    val ZAID: String,
    val role: Int, // 0, 1, 2 | Guest, Employee, Admin
  //val company: String ???
    val driversLicense: Boolean,
    val password: String, //Hashed
)

//DATABASE VARIABLES:
//User_ID: int(20)
//Name: Text
//Surname: Text
//Email: varchar(255)
//Phone_number: varchar(10)
//ID_number: varchar(13)
//Role: enum('Guest','Employee','Admin')
//Company: varchar(50) ???
//Drivers_license: True/False
//Password: varchar(255)
