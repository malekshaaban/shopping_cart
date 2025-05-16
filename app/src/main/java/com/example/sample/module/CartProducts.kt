package com.example.sample.module

data class CartProducts(
val cartId : String = "",
val productModule: ProductModule,
    val quantity : Int,



){
    constructor() : this("",ProductModule(),1)
}

