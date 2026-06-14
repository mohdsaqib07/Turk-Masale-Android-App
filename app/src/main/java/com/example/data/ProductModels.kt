package com.example.data

import com.example.R
import com.example.ui.theme.ColorCoriander
import com.example.ui.theme.ColorGaramMasala
import com.example.ui.theme.ColorRedChilli
import com.example.ui.theme.ColorTurmeric

data class ProductVariant(
    val size: String,
    val price: Double
)

data class Product(
    val id: String,
    val name: String,
    val category: String, // Powder, Whole, Blend
    val variants: List<ProductVariant>,
    val description: String,
    val ingredients: String,
    val recipe: String,
    val themeColorHex: Long, // to paint the packaging box mock
    val imageUrl: String = "",
    val imageRes: Int? = null // For locally uploaded images
) {
    val startingPrice: Double
        get() = variants.minOfOrNull { it.price } ?: 0.0

    val defaultVariant: ProductVariant
        get() = variants.firstOrNull() ?: ProductVariant("Unknown", 0.0)
}

data class CartItem(
    val product: Product,
    val variant: ProductVariant,
    var quantity: Int
)

data class Order(
    val orderId: String,
    val date: String,
    val totalAmount: Double,
    val status: String // Processing, Shipped, Delivered
)

object FakeData {
    val products = listOf(
        Product(
            id = "p1",
            name = "Turmeric Powder",
            category = "Powdered Spices",
            variants = listOf(
                ProductVariant("50 g", 15.0),
                ProductVariant("100 g", 30.0),
                ProductVariant("250 g", 75.0),
                ProductVariant("500 g", 150.0)
            ),
            description = "100% natural, pure & spicy Turmeric Powder sourced from the best farms. Established in 1978.",
            ingredients = "Dried turmeric roots.",
            recipe = "Golden Milk:\n1. Heat 1 cup milk.\n2. Add 1/2 tsp Turmeric Powder, pinch of black pepper, and honey.\n3. Stir well and enjoy warm.",
            themeColorHex = ColorTurmeric.value.toLong(),
            // imageUrl = "https://images.unsplash.com/photo-1615486171448-4cbab1a8f906?q=80&w=600&auto=format&fit=crop",
            // To use your uploaded image, uncomment the line below and change the name:
            imageRes = R.drawable.turmeric_pack
        ),
        Product(
            id = "p2",
            name = "Coriander Powder",
            category = "Powdered Spices",
            variants = listOf(
                ProductVariant("50 g", 15.0),
                ProductVariant("100 g", 30.0),
                ProductVariant("250 g", 70.0),
                ProductVariant("500 g", 140.0)
            ),
            description = "Finely ground coriander seeds for that fresh, aromatic flavor in your daily cooking.",
            ingredients = "Premium coriander seeds.",
            recipe = "Coriander Chutney:\n1. Blend fresh mint, coriander leaves, green chillies.\n2. Add 1/2 tsp Coriander Powder & salt.\n3. Serve with snacks.",
            themeColorHex = ColorCoriander.value.toLong(),
            // imageUrl = "https://images.unsplash.com/photo-1596040033229-a9821ebd058d?q=80&w=600&auto=format&fit=crop"
            imageRes = R.drawable.coriander_pack
        ),
        Product(
            id = "p3",
            name = "Whole Garam Masala Powder",
            category = "Blended Spices",
            variants = listOf(
                ProductVariant("50 g", 60.0),
                ProductVariant("100 g", 120.0),
                ProductVariant("250 g", 300.0),
                ProductVariant("500 g", 500.0)
            ),
            description = "A perfect blend of whole spices to give your curries an authentic Indian aroma.",
            ingredients = "Cumin, Coriander, Cardamom, Black Pepper, Clove, Cinnamon, Nutmeg.",
            recipe = "Paneer Tikka Masala:\n1. Marinate paneer cubes with yogurt and spices.\n2. Prepare tomato gravy.\n3. Add 1 tsp Garam Masala towards the end. Garnish with cream.",
            themeColorHex = ColorGaramMasala.value.toLong(),
            // imageUrl = "https://images.unsplash.com/photo-1596633605700-1efc9b49e277?q=80&w=600&auto=format&fit=crop"
            imageRes = R.drawable.garammasala_pack
        ),
        Product(
            id = "p4",
            name = "Red Chilli Powder",
            category = "Powdered Spices",
            variants = listOf(
                ProductVariant("50 g", 20.0),
                ProductVariant("100 g", 40.0),
                ProductVariant("250 g", 100.0),
                ProductVariant("500 g", 200.0)
            ),
            description = "Pure and fiery red chilli powder for the perfect color and heat in your dishes.",
            ingredients = "Dried Red Chillies.",
            recipe = "Spicy Chicken Curry:\n1. Sauté onions, garlic, and ginger.\n2. Add tomatoes, Chicken, and 1 tsp Red Chilli Powder.\n3. Simmer until cooked.",
            themeColorHex = ColorRedChilli.value.toLong(),
            // imageUrl = "https://images.unsplash.com/photo-1596040033229-a9821ebd058d?q=80&w=600&auto=format&fit=crop" // fallback image, actual chili powder is red but this is for placeholder
            imageRes = R.drawable.redchilli_pack
        )
    )

    val categories = listOf("All", "Powdered Spices", "Blended Spices", "Whole Spices")
}
