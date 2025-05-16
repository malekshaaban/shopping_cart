package com.example.sample.module

data class UserModule(

    val name: String ="",
    val email: String = "",
    val userId: String ="",
    val cartItems : Map<String,Long> = emptyMap(),
    val address : String = "",
    val favoriteItems: Map<String, Boolean> = emptyMap()


)
