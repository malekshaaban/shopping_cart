package com.example.sample.viewModule

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sample.firebase.FirebaseCommon
import com.example.sample.module.CartProducts
import com.example.sample.module.RatingModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch
import java.util.UUID

class DetailsViewModule : ViewModel() {
    private val auth = Firebase.auth
    private val firestore = Firebase.firestore
    private val _addToCart = mutableListOf<CartProducts>()
    val addToCart = _addToCart
    val firebaseCommon = FirebaseCommon()

    fun addUpdateProductInCart(cartProducts: CartProducts) {
        firestore.collection("users").document(auth.currentUser?.uid!!)
            .collection("cart")
            .whereEqualTo("productModule.id", cartProducts.productModule.id)
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.isEmpty) {
                    val newCartId = UUID.randomUUID().toString()
                    val newCartProduct = cartProducts.copy(cartId = newCartId)
                    addNewProduct(newCartProduct)
                } else {
                    val existingCartProduct = snapshot.documents.first().toObject(CartProducts::class.java)
                    existingCartProduct?.let {
                        val existingCartId = it.cartId
                        val updatedCartProduct = cartProducts.copy(cartId = existingCartId)
                        updateProductInCart(updatedCartProduct)
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Cart", "Error fetching cart data: ", exception)
            }
    }

    private fun updateProductInCart(cartProducts: CartProducts) {
        firebaseCommon.increseQuantity(cartProducts.cartId) { updatedCartId, exception ->
            if (exception == null) {
                Log.d("Cart", "Product quantity updated successfully for cartId: $updatedCartId")
            } else {
                Log.e("Cart", "Error updating product quantity: ${exception.localizedMessage}")
            }
        }
    }

    private fun addNewProduct(cartProducts: CartProducts) {
        firebaseCommon.addProductToCart(cartProducts) { addedProduct, exception ->
            if (addedProduct == null) {
                viewModelScope.launch {
                    _addToCart.add(cartProducts)
                }
            }
        }
    }

    fun clearAllItemsInCart() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection("users")
                .document(userId)
                .collection("cart")
                .get()
                .addOnSuccessListener { snapshot ->
                    for (doc in snapshot.documents) {
                        val documentId = doc.id
                        deleteProductFromCart(documentId)
                    }
                }
        }
    }

    private fun deleteProductFromCart(documentId: String) {
        firestore.collection("users")
            .document(auth.currentUser?.uid!!)
            .collection("cart")
            .document(documentId)
            .delete()
            .addOnSuccessListener {
                Log.d("Cart", "Product deleted successfully")
            }
            .addOnFailureListener { exception ->
                Log.e("Cart", "Error deleting product: ${exception.message}")
            }
    }

    // New function to submit a rating
    fun submitRating(productId: String, rating: Int, comment: String, userName: String, onResult: (Boolean, String?) -> Unit) {
        val userId = auth.currentUser?.uid ?: return onResult(false, "User not logged in")
        val ratingModel = RatingModel(
            userId = userId,
            rating = rating.coerceIn(1, 5),
            comment = comment.trim(),
            userName = userName
        )

        firestore.collection("data").document("stock")
            .collection("products").document(productId)
            .collection("ratings").document(userId)
            .set(ratingModel)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    updateProductRatingStats(productId)
                    onResult(true, null)
                } else {
                    onResult(false, task.exception?.localizedMessage)
                }
            }
    }

    // New function to update average rating and rating count
    private fun updateProductRatingStats(productId: String) {
        val ratingsRef = firestore.collection("data").document("stock")
            .collection("products").document(productId)
            .collection("ratings")

        ratingsRef.get().addOnSuccessListener { snapshot ->
            val ratings = snapshot.documents.mapNotNull { it.toObject(RatingModel::class.java) }
            val ratingCount = ratings.size.toLong()
            val averageRating = if (ratings.isNotEmpty()) {
                ratings.sumOf { it.rating } / ratings.size.toDouble()
            } else 0.0

            firestore.collection("data").document("stock")
                .collection("products").document(productId)
                .update(
                    mapOf(
                        "averageRating" to averageRating,
                        "ratingCount" to ratingCount
                    )
                )
        }
    }

    // New function to fetch ratings for a product
    fun fetchRatings(productId: String, onResult: (List<RatingModel>) -> Unit) {
        firestore.collection("data").document("stock")
            .collection("products").document(productId)
            .collection("ratings")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snapshot ->
                val ratings = snapshot.documents.mapNotNull { it.toObject(RatingModel::class.java) }
                onResult(ratings)
            }
            .addOnFailureListener { exception ->
                Log.e("Ratings", "Error fetching ratings: ${exception.message}")
                onResult(emptyList())
            }
    }
}