package com.example.sample.Components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.HorizontalPager
import com.tbuonomo.viewpagerdotsindicator.compose.DotsIndicator
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tbuonomo.viewpagerdotsindicator.compose.model.DotGraphic
import com.tbuonomo.viewpagerdotsindicator.compose.type.ShiftIndicatorType

@Composable
fun BannerView(modifier: Modifier = Modifier) {

    var bannerList by remember { mutableStateOf<List<String>>(emptyList()) }
    LaunchedEffect(Unit) {
        Firebase.firestore.collection("data").document("banners").get().addOnCompleteListener {
            bannerList = it.result.get("urls") as List<String>
        }
    }

    Column(modifier = Modifier) {
        val pagerState = rememberPagerState(0) {
            bannerList.size
        }
        HorizontalPager(state = pagerState,
            pageSpacing = 20.dp)
        {
            AsyncImage(model = bannerList.get(it), contentDescription = "banner",
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp))
            )


        }
        Spacer(modifier = Modifier.height(10.dp))

        DotsIndicator(dotCount =bannerList.size
            ,type = ShiftIndicatorType(DotGraphic(
                color = MaterialTheme.colorScheme.primary, size = 6.dp)) ,
            pagerState = pagerState)

    }


}