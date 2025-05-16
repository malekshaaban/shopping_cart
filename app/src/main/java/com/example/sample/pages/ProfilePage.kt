package com.example.sample.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode.Companion.Screen
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.google.firebase.auth.FirebaseAuth
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.sample.Components.HeaderView
import com.example.sample.GlobalNavegation.navController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth


@Composable
fun ProfilePage(modifier: Modifier) {

    Column(
        modifier = modifier.fillMaxWidth().padding(16.dp)
    ) {

        Text(text = "Profile Page", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.padding(10.dp))

        HeaderView(modifier = Modifier.padding(8.dp))
        Spacer(modifier = Modifier.padding(20.dp))
        Button(onClick = {
            Firebase.auth.signOut()
            navController.navigate("auth"){
                popUpTo("auth"){
                    inclusive = true
                }
            }

        }, modifier = modifier.fillMaxWidth().padding(8.dp)) {
            Text(text = "Logout")


        }
    }
}
