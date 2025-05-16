package com.example.sample

import android.content.Context
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore

object AppUtil {
    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun addToCart(productId: String, context: Context) {
        val userDoc = Firebase.firestore.collection("users").document(Firebase.auth.currentUser?.uid!!)
        userDoc.get().addOnCompleteListener {
            if (it.isSuccessful) {
                val currentCart = it.result.get("cartItems") as? Map<String, Long> ?: emptyMap()
                val currentQuantity = currentCart[productId] ?: 0
                val updatedQuantity = currentQuantity + 1
                val updatedCart = mapOf("cartItems.$productId" to updatedQuantity)
                userDoc.update(updatedCart).addOnCompleteListener {
                    if (it.isSuccessful) {
                        showToast(context, "Item added to cart")
                    } else {
                        showToast(context, "Failed to add item to cart")
                    }
                }
            }
        }
    }

    fun removeFromCart(productId: String, context: Context, removeAll: Boolean = false) {
        val userDoc = Firebase.firestore.collection("users").document(Firebase.auth.currentUser?.uid!!)
        userDoc.get().addOnCompleteListener {
            if (it.isSuccessful) {
                val currentCart = it.result.get("cartItems") as? Map<String, Long> ?: emptyMap()
                val currentQuantity = currentCart[productId] ?: 0
                val updatedQuantity = currentQuantity - 1
                val updatedCart = if (updatedQuantity <= 0 || removeAll)
                    mapOf("cartItems.$productId" to FieldValue.delete())
                else
                    mapOf("cartItems.$productId" to updatedQuantity)
                userDoc.update(updatedCart).addOnCompleteListener {
                    if (it.isSuccessful) {
                        showToast(context, "Item removed from cart")
                    } else {
                        showToast(context, "Failed to remove item from cart")
                    }
                }
            }
        }
    }

    // New function to add to favorites
    fun addToFavorites(productId: String, context: Context) {
        val userDoc = Firebase.firestore.collection("users").document(Firebase.auth.currentUser?.uid!!)
        val updatedFavorites = mapOf("favoriteItems.$productId" to true)
        userDoc.update(updatedFavorites).addOnCompleteListener {
            if (it.isSuccessful) {
                showToast(context, "Added to favorites")
            } else {
                showToast(context, "Failed to add to favorites")
            }
        }
    }

    // New function to remove from favorites
    fun removeFromFavorites(productId: String, context: Context) {
        val userDoc = Firebase.firestore.collection("users").document(Firebase.auth.currentUser?.uid!!)
        val updatedFavorites = mapOf("favoriteItems.$productId" to FieldValue.delete())
        userDoc.update(updatedFavorites).addOnCompleteListener {
            if (it.isSuccessful) {
                showToast(context, "Removed from favorites")
            } else {
                showToast(context, "Failed to remove from favorites")
            }
        }
    }

    fun getDiscountPercentage(): Float {
        return 10.0f
    }

    fun taxPercentage(): Float {
        return 13.0f
    }
}