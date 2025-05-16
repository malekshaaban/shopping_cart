package com.example.sample.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sample.AppUtil
import com.example.sample.module.ProductModule
import com.example.sample.module.UserModule
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.combine

@Composable
fun checkoutPage (modifier: Modifier = Modifier) {

    val userModel = remember {
        mutableStateOf(UserModule())
    }

    val productList = remember {
        mutableStateListOf(ProductModule())
    }

    val subTotal = remember {
        mutableStateOf(0f)
    }
    val discount = remember {
        mutableStateOf(0f)
    }
    val tax = remember {
        mutableStateOf(0f)
    }
    val total = remember {
        mutableStateOf(0f)
    }


    fun calculateAndAssign(){
productList.forEach {
    if (it.actualPrice.isNotEmpty()){
        val qnt = userModel.value.cartItems[it.id] ?: 0
        subTotal.value += it.actualPrice.toFloat() * qnt
    }

}
        discount.value = subTotal.value * AppUtil.getDiscountPercentage() / 100
        tax.value = subTotal.value * AppUtil.taxPercentage() / 100
        total.value  = "%.2f".format( subTotal.value - discount.value + tax.value).toFloat()
    }

    LaunchedEffect(key1 = Unit) {
        Firebase.firestore.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid!!)
            .get().addOnCompleteListener {
                if (it.isSuccessful) {
                    val result = it.result.toObject(UserModule::class.java)
                    if (result != null) {
                        userModel.value = result


                        Firebase.firestore.collection("data").document("stock")
                            .collection("products")
                            .whereIn("id", userModel.value.cartItems.keys.toList())
                            .get().addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val resultProduct =
                                        task.result.toObjects(ProductModule::class.java)
                                    productList.addAll(resultProduct)
                                    calculateAndAssign()
                                }
                            }

                    }
                }
            }

    }

    Column(modifier=modifier
        .fillMaxSize()
        .padding(16.dp)) {


        Text(text = "checkout",fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Delivery to : ",fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
        Text(text = userModel.value.address,fontSize = 15.sp)
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))

        RowCheckoutItems(title = "SubTotal", value = subTotal.value.toString())
        Spacer(modifier = Modifier.height(16.dp))
        RowCheckoutItems(title = "Discount (-)", value = discount.value.toString())
        Spacer(modifier = Modifier.height(16.dp))
        RowCheckoutItems(title = "Tax (+)", value = tax.value.toString())
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))
        RowCheckoutItems(title = "Total", value = total.value.toString())
        Spacer(modifier = Modifier.height(16.dp))

    }




}

@Composable
fun RowCheckoutItems(title: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = title, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
        Text(text = "$ "+value,fontSize = 15.sp)
    }

}