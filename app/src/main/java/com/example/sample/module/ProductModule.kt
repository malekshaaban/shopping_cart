package com.example.sample.module

data class ProductModule(

    val id : String = "",
    val title : String = "",
    val description : String = "",
    val price : String = "",
    val actualPrice : String = "",
    val category : String = "",
    val images :  List<String> = emptyList(),
    val otherDetails : Map<String,String> = mapOf(),
    val averageRating: Double = 0.0,
    val ratingCount: Long = 0
)



