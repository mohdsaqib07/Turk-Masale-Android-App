package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.Product
import com.example.ui.theme.Red40
import com.example.viewmodel.ShopViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: String,
    viewModel: ShopViewModel,
    onBack: () -> Unit,
    onCartNavigate: () -> Unit
) {
    val product = viewModel.products.find { it.id == productId }
    if (product == null) {
        onBack()
        return
    }

    var quantity by remember { mutableStateOf(1) }
    var selectedVariant by remember { mutableStateOf(product.defaultVariant) }
    
    val wishlist by viewModel.wishlist.collectAsState()
    val isWishlisted = wishlist.any { it.productId == productId }
    
    val reviews by viewModel.getReviews(productId).collectAsState()
    val averageRating = if (reviews.isEmpty()) 0.0 else reviews.map { it.rating }.average()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(product.name, maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleWishlist(productId) }) {
                        Icon(
                            if (isWishlisted) Icons.Default.Favorite else Icons.Default.FavoriteBorder, 
                            contentDescription = "Wishlist", 
                            tint = if (isWishlisted) Red40 else MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Total Price", style = MaterialTheme.typography.labelMedium)
                        Text(
                            text = "₹${selectedVariant.price * quantity}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Red40
                        )
                    }
                    Button(
                        onClick = {
                            viewModel.addToCart(product, selectedVariant, quantity)
                            onCartNavigate()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Red40),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Add to Cart")
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Product Image Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(Color(product.themeColorHex).copy(alpha = 0.1f))
            ) {
                AsyncImage(
                    model = product.imageRes ?: product.imageUrl,
                    contentDescription = product.name,
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            androidx.compose.ui.graphics.Brush.verticalGradient(
                                listOf(Color.Transparent, MaterialTheme.colorScheme.background),
                                startY = 300f
                            )
                        )
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    // Quantity Selector
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { if (quantity > 1) quantity-- }) {
                            Text("-", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        }
                        Text(quantity.toString(), style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(horizontal = 8.dp))
                        IconButton(onClick = { quantity++ }) {
                            Text("+", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(String.format("%.1f", averageRating), fontWeight = FontWeight.Bold)
                    Text(" (${reviews.size} reviews)", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "${selectedVariant.size} • ${product.category}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Dropdown for sizes
                Text("Select Size", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                
                var expanded by remember { mutableStateOf(false) }
                
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = "${selectedVariant.size} - ₹${selectedVariant.price}",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        product.variants.forEach { variant ->
                            DropdownMenuItem(
                                text = { Text("${variant.size} - ₹${variant.price}") },
                                onClick = {
                                    selectedVariant = variant
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("Description", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(product.description, style = MaterialTheme.typography.bodyMedium)

                Spacer(modifier = Modifier.height(16.dp))
                
                Text("Ingredients", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(product.ingredients, style = MaterialTheme.typography.bodyMedium)

                Spacer(modifier = Modifier.height(16.dp))
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Recipe Idea", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = Red40)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(product.recipe, style = MaterialTheme.typography.bodyMedium)
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Reviews Section
                Text("Customer Reviews", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))
                
                var newReviewText by remember { mutableStateOf("") }
                var newReviewRating by remember { mutableStateOf(5) }
                
                val user by viewModel.currentUser.collectAsState()
                if (user != null) {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Write a Review", fontWeight = FontWeight.Bold)
                            Row {
                                (1..5).forEach { i ->
                                    IconButton(onClick = { newReviewRating = i }) {
                                        Icon(if (i <= newReviewRating) Icons.Default.Star else Icons.Outlined.Star, contentDescription = null, tint = Color(0xFFFFC107))
                                    }
                                }
                            }
                            OutlinedTextField(
                                value = newReviewText,
                                onValueChange = { newReviewText = it },
                                label = { Text("Share your experience") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = {
                                viewModel.submitReview(productId, newReviewRating, newReviewText)
                                newReviewText = ""
                                newReviewRating = 5
                            }, colors = ButtonDefaults.buttonColors(containerColor = Red40)) {
                                Text("Submit Review")
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                } else {
                    Text("Please login from Profile to write a review.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                if (reviews.isEmpty()) {
                    Text("No reviews yet. Be the first to review!", style = MaterialTheme.typography.bodyMedium)
                } else {
                    reviews.forEach { r ->
                        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(r.userName, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(8.dp))
                                Row {
                                    (1..5).forEach { i ->
                                        Icon(if (i <= r.rating) Icons.Default.Star else Icons.Outlined.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                            Text(r.comment, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 4.dp))
                            Divider(modifier = Modifier.padding(top = 8.dp))
                        }
                    }
                }
            }
        }
    }
}
