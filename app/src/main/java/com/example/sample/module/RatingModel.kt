package com.example.sample.module


import com.google.firebase.Timestamp


data class RatingModel(
    val userId: String = "",
    val rating: Int = 0, // 1 to 5 stars
    val comment: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val userName: String = "" // Optional: Store user's name for display

)
