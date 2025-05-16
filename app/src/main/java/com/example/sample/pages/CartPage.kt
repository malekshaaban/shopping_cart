package com.example.sample.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.Disposable
import com.example.sample.AppUtil
import com.example.sample.Components.CartItemView
import com.example.sample.GlobalNavegation
import com.example.sample.module.CartProducts
import com.example.sample.module.UserModule
import com.example.sample.viewModule.DetailsViewModule
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlin.toString

@Composable
fun CartPage (modifier: Modifier = Modifier) {

    val userModel = remember {
        mutableStateOf(UserModule())
    }
    var cartProducts by remember { mutableStateOf<List<CartProducts>>(emptyList()) }
    val context = androidx.compose.ui.platform.LocalContext.current
    DisposableEffect (key1 = Unit) {
       var listener = Firebase.firestore.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid!!)
            .addSnapshotListener{it,_ ->
                if (it!= null) {
                    val result = it.toObject(UserModule::class.java)
                    if (result != null) {
                        userModel.value = result
                    }
                }


            };


        onDispose {
            listener.remove()
        }



    }


    Column(modifier=modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Your cart", style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold))
        Spacer(modifier = Modifier.height(16.dp))


        LazyColumn(modifier = Modifier.weight(1f)) {
            items(userModel.value.cartItems.toList(),key = {it.first}) { (productId,qty) ->
                CartItemView(productId = productId, qty = qty)



            }
        }

        Button(onClick = {
            if (userModel.value.cartItems.isNotEmpty()) {
                GlobalNavegation.navController.navigate("checkout")
            } else {
                AppUtil.showToast(context, "Your cart is empty")
            }
        },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)) {

            Text(text = "checkout")
        }


    }


}
