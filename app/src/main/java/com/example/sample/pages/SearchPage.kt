package com.example.sample.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sample.Components.ProductItemView
import com.example.sample.module.ProductModule
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

@Composable
fun SearchPage(modifier: Modifier = Modifier) {
    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<ProductModule>>(emptyList()) }

    // Perform search when query changes
    LaunchedEffect(searchQuery) {
        if (searchQuery.isBlank()) {
            searchResults = emptyList()
        } else {
            Firebase.firestore.collection("data").document("stock")
                .collection("products")
                .whereGreaterThanOrEqualTo("title", searchQuery)
                .whereLessThanOrEqualTo("title", searchQuery + "\uf8ff")
                .get()
                .addOnSuccessListener { snapshot ->
                    val results = snapshot.documents.mapNotNull { it.toObject(ProductModule::class.java) }
                    searchResults = results
                }
                .addOnFailureListener { e ->
                    // Handle error (e.g., show toast)
                    println("Search error: ${e.message}")
                }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top bar with back button and search field
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                com.example.sample.GlobalNavegation.navController.popBackStack()
            }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search products") },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                singleLine = true
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Search results or empty state
        if (searchQuery.isBlank()) {
            Text(
                text = "Enter a search term to find products",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else if (searchResults.isEmpty()) {
            Text(
                text = "No products found for \"$searchQuery\"",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(searchResults, key = { it.id }) { product ->
                    ProductItemView(
                        product = product,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}