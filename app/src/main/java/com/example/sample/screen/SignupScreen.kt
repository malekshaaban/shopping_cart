package com.example.sample.screen

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.sample.AppUtil
import com.example.sample.R
import com.example.sample.viewModule.AuthViewModule

@Composable
fun SignupScreen (modifier: Modifier = Modifier,navController: NavController,AuthViewModule: AuthViewModule=viewModel()) {


    var email by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var context = LocalContext.current

    var isLoading by remember { mutableStateOf(false) }


    Column (modifier = modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center){
        Text(
            text = "Hello there!",
            modifier = Modifier.fillMaxWidth(),
            style = TextStyle(
                fontSize = 30.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            ),
            textAlign = TextAlign.Center // You probably want this if you're centering
        )
        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = " create an account ",
            modifier = Modifier.fillMaxWidth(),
            style = TextStyle(
                fontSize = 20.sp,
                fontFamily = FontFamily.Monospace,

            ),
            textAlign = TextAlign.Center // You probably want this if you're centering
        )
        Spacer(modifier = Modifier.height(5.dp))

        Image(
            painter = painterResource(id = R.drawable.mobfinger),
            contentDescription = "logo",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it }, // ✅ 'it' will NOT be red here
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()

        )
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it }, // ✅ 'it' will NOT be red here
            label = { Text("name") },
            modifier = Modifier
                .fillMaxWidth()

        )
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it }, // ✅ 'it' will NOT be red here
            label = { Text("password") },
            modifier = Modifier
                .fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()

        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = {
            isLoading = true
            AuthViewModule.signup(email, password, name){
                success, errorMessage ->
                if (success) {
                    isLoading = false

                    navController.navigate("home"){
                        popUpTo("auth"){
                            inclusive = true
                        }

                    }
                } else {
                    isLoading = false

                    AppUtil.showToast(context, errorMessage ?: "Unknown error")
                }

            }

        },enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
                .height(60.dp)) {
            Text(text = if (isLoading) "Loading..." else "signup",fontSize = 25.sp)
        }
    }
}