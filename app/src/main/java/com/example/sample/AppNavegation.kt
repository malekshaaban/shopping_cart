package com.example.sample

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sample.pages.CategoryProductPage
import com.example.sample.pages.ProductDetailsPage
import com.example.sample.pages.SearchPage
import com.example.sample.pages.checkoutPage
import com.example.sample.screen.AuthScreen
import com.example.sample.screen.HomeScreen
import com.example.sample.screen.LoginScreen
import com.example.sample.screen.SignupScreen
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun AppNavegation (modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val isLoggedIn = Firebase.auth.currentUser != null

    val firstPage = if (isLoggedIn) "home" else "auth"


    NavHost(navController = navController, startDestination = firstPage) {

        GlobalNavegation.navController = navController
        composable (route = "auth"){

            AuthScreen(modifier,navController)
        }
        composable (route = "login"){

            LoginScreen(modifier,navController)
        }
        composable (route = "signup"){

            SignupScreen(modifier,navController)
        }

        composable (route = "home"){

            HomeScreen(modifier
            ,navController)
        }
        composable (route = "category-product/{categoryId}"){
            var categoryId = it.arguments?.getString("categoryId")
            CategoryProductPage(modifier,categoryId?:"")
        }

        composable (route = "product-details/{productId}"){
            var productId = it.arguments?.getString("productId")
            ProductDetailsPage(modifier,productId?:"")
        }


        composable (route = "checkout"){

            checkoutPage(modifier
                )
        }
        composable(route = "search") {
            SearchPage(modifier)
        }

    }

}

object GlobalNavegation{
    lateinit var navController : NavHostController
}
