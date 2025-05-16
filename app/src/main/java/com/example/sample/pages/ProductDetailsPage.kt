package com.example.sample.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.sample.AppUtil
import com.example.sample.module.ProductModule
import com.example.sample.module.RatingModel
import com.example.sample.module.UserModule
import com.example.sample.viewModule.DetailsViewModule
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.tbuonomo.viewpagerdotsindicator.compose.DotsIndicator
import com.tbuonomo.viewpagerdotsindicator.compose.model.DotGraphic
import com.tbuonomo.viewpagerdotsindicator.compose.type.ShiftIndicatorType

@Composable
fun ProductDetailsPage(modifier: Modifier = Modifier, productId: String, viewModel: DetailsViewModule = viewModel()) {
    var product by remember { mutableStateOf(ProductModule()) }
    var ratings by remember { mutableStateOf<List<RatingModel>>(emptyList()) }
    var userRating by remember { mutableStateOf(0) }
    var userComment by remember { mutableStateOf("") }
    var userName by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }
    var userModel by remember { mutableStateOf(UserModule()) }

    val context = LocalContext.current

    LaunchedEffect(key1 = Unit) {
        // Fetch product details
        Firebase.firestore.collection("data").document("stock")
            .collection("products")
            .document(productId).get().addOnCompleteListener {
                if (it.isSuccessful) {
                    var result = it.result.toObject(ProductModule::class.java)
                    if (result != null) {
                        product = result
                    }
                }
            }
        // Fetch user name
        Firebase.firestore.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid ?: "")
            .get().addOnSuccessListener {
                userModel = it.toObject(UserModule::class.java) ?: UserModule()

                userName = it.get("name")?.toString()?.split(" ")?.get(0) ?: ""
            }
        // Fetch ratings
        viewModel.fetchRatings(productId) { fetchedRatings ->
            ratings = fetchedRatings
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = product.title,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp),
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.padding(8.dp))

        Column {
            val pagerState = rememberPagerState(0) { product.images.size }
            HorizontalPager(state = pagerState, pageSpacing = 20.dp) {
                AsyncImage(
                    model = product.images.get(it),
                    contentDescription = "product images",
                    modifier = Modifier
                        .height(220.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            DotsIndicator(
                dotCount = product.images.size,
                type = ShiftIndicatorType(DotGraphic(color = MaterialTheme.colorScheme.primary, size = 6.dp)),
                pagerState = pagerState
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$${product.price}",
                fontSize = 16.sp,
                style = TextStyle(textDecoration = TextDecoration.LineThrough)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "$${product.actualPrice}",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = {
                if (userModel.favoriteItems.containsKey(productId)) {
                    AppUtil.removeFromFavorites(productId, context)
                } else {
                    AppUtil.addToFavorites(productId, context)
                } }) {
                Icon( imageVector = if (userModel.favoriteItems.containsKey(productId)) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Toggle favorite",
                    tint = if (userModel.favoriteItems.containsKey(productId)) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                )
            }
        }


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Rating: ${"%.1f".format(product.averageRating)} (${product.ratingCount} reviews)",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Button(
            onClick = { AppUtil.addToCart(productId, context) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(text = "Add to cart", fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.height(10.dp))


        Text(
            text = "Rate this product",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp),
            fontSize = 18.sp
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            (1..5).forEach { star ->
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Star $star",
                    tint = if (star <= userRating) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { userRating = star }
                )
            }
        }
        OutlinedTextField(
            value = userComment,
            onValueChange = { userComment = it },
            label = { Text("Your comment") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            maxLines = 3
        )
        Button(
            onClick = {
                if (userRating == 0) {
                    AppUtil.showToast(context, "Please select a rating")
                    return@Button
                }
                isSubmitting = true
                viewModel.submitRating(productId, userRating, userComment, userName) { success, error ->
                    isSubmitting = false
                    if (success) {
                        AppUtil.showToast(context, "Rating submitted")
                        userRating = 0
                        userComment = ""
                        viewModel.fetchRatings(productId) { fetchedRatings ->
                            ratings = fetchedRatings
                        }
                    } else {
                        AppUtil.showToast(context, error ?: "Failed to submit rating")
                    }
                }
            },
            enabled = !isSubmitting,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .height(50.dp)
        ) {
            Text(text = if (isSubmitting) "Submitting..." else "Submit Rating")
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Description",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp),
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = product.description,
            modifier = Modifier.padding(8.dp),
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(18.dp))
        Text(
            text = "Other details",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp),
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.height(10.dp))
        product.otherDetails.forEach { (key, value) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
            ) {
                Text(text = "$key: ", fontWeight = FontWeight.Bold)
                Text(text = value)
            }
        }


        Spacer(modifier = Modifier.height(18.dp))
        Text(
            text = "User Reviews",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp),
            fontSize = 20.sp
        )
        HorizontalDivider()
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            items(ratings) { rating ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = rating.userName,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        (1..5).forEach { star ->
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = if (star <= rating.rating) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Text(
                        text = rating.comment,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider()
                }
            }
        }
    }
}