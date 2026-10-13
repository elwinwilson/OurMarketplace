package com.learn.ourmarketplace.presentation.products

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.learn.ourmarketplace.domain.model.Product

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsScreen(
    product: Product?,
    onBack: () -> Unit,
    onFavoriteClick: (Product) -> Unit
) {
    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = { Text("Product details") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF00897B),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    product?.let { item ->
                        IconButton(onClick = { onFavoriteClick(item) }) {
                            Icon(
                                imageVector = if (item.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = if (item.isFavorite) "Remove from favorites" else "Add to favorites"
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (product == null) {
            Text(
                "This product is no longer available.",
                modifier = Modifier.padding(padding).padding(16.dp)
            )
        } else {
            Column(
                modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F2F2))
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        AsyncImage(
                            model = product.imageUrl,
                            contentDescription = product.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(320.dp)
                                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = product.title,
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Text(
                                text = formatPrice(product.price),
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                        Text(
                            text = product.description,
                            modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 12.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}
