package com.example.sample.Components


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.sample.AppUtil
import com.example.sample.GlobalNavegation
import com.example.sample.module.CartProducts
import com.example.sample.module.ProductModule
import com.example.sample.viewModule.DetailsViewModule


@Composable
fun ProductItemView(modifier: Modifier = Modifier,product : ProductModule,viewModel: DetailsViewModule = viewModel()) {

    var context = LocalContext.current


    Card(
        modifier = modifier
            .padding(8.dp)
            .clickable{
                GlobalNavegation.navController.navigate("product-details/"+product.id)
            },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(contentColor = MaterialTheme.colorScheme.surface,),
        elevation = CardDefaults.cardElevation(10.dp)
    ){
        Column(
            modifier = Modifier.padding(12.dp)
        ){
            AsyncImage(model = product.images.firstOrNull(),
                contentDescription = product.title ,   modifier = Modifier
                    .height(120.dp)
                    .fillMaxWidth())

            Text(text = product.title, fontWeight = FontWeight.Bold, maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(8.dp))


            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text ="$"+ product.price,
                    fontSize = 12.sp,
                    style = TextStyle(textDecoration = TextDecoration.LineThrough)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                Text(text ="$"+ product.actualPrice,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = {
                    AppUtil.addToCart(product.id,context)
                }) {
                    Icon(imageVector = Icons.Default.ShoppingCart ,
                        contentDescription = "Add to cart")


                }


            }


        }


    }

}