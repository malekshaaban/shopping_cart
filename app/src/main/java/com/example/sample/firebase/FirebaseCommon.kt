package com.example.sample.firebase

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.example.sample.module.CartProducts

class FirebaseCommon {
    private val auth = Firebase.auth
    private val firestore = Firebase.firestore



    private val cartCollection =
        firestore.collection("users").document(auth.currentUser?.uid!!).collection("cart")

    fun addProductToCart(CartProduct: CartProducts, onResult: (CartProducts?, Exception?) -> Unit) {
        cartCollection.document().set(CartProduct).addOnSuccessListener {
            onResult(CartProduct, null)

        }.addOnFailureListener {
            onResult(null, it)

        }

    }


    fun increseQuantity(documentId: String, onResult: (String?, Exception?) -> Unit) {

        firestore.runTransaction { transaction ->
            val docRef = cartCollection.document(documentId)
            val document = transaction.get(docRef)
            val productObject = document.toObject(CartProducts::class.java)
            productObject?.let { cartProduct ->
                val newQuantity = cartProduct.quantity + 1
                val newProductObject = cartProduct.copy(quantity = newQuantity)
                transaction.set(docRef, newProductObject)
                onResult(documentId, null)
            }
        }.addOnSuccessListener {
            onResult(documentId, null)
        }.addOnFailureListener {
            onResult(null, it)

        }
    }
}