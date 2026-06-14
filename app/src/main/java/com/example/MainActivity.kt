package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.ui.screens.*
import com.example.ui.theme.Red40
import com.example.ui.theme.TurkMasaleTheme
import com.example.viewmodel.ShopViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TurkMasaleTheme {
                val navController = rememberNavController()
                val viewModel: ShopViewModel = viewModel()
                
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentRoute = navBackStackEntry?.destination?.route
                        
                        // Show bottom bar only on major screens
                        val showBottomBar = currentRoute in listOf("home", "cart", "orders", "profile")
                        if (showBottomBar) {
                            NavigationBar(
                                containerColor = MaterialTheme.colorScheme.surface
                            ) {
                                val items = listOf(
                                    BottomNavItem("Home", "home", Icons.Default.Home),
                                    BottomNavItem("Cart", "cart", Icons.Default.ShoppingCart),
                                    BottomNavItem("Orders", "orders", Icons.Default.List),
                                    BottomNavItem("Profile", "profile", Icons.Default.Person)
                                )
                                items.forEach { item ->
                                    NavigationBarItem(
                                        icon = { Icon(item.icon, contentDescription = item.title) },
                                        label = { Text(item.title) },
                                        selected = currentRoute == item.route,
                                        onClick = {
                                            navController.navigate(item.route) {
                                                popUpTo("home") { saveState = true }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = Red40,
                                            selectedTextColor = Red40,
                                            indicatorColor = MaterialTheme.colorScheme.secondaryContainer
                                        )
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("home") {
                            HomeScreen(
                                viewModel = viewModel,
                                onProductClick = { productId ->
                                    navController.navigate("product/$productId")
                                },
                                onCartClick = {
                                    navController.navigate("cart") {
                                        popUpTo("home") { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                        
                        composable(
                            route = "product/{productId}",
                            arguments = listOf(navArgument("productId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val productId = backStackEntry.arguments?.getString("productId") ?: ""
                            ProductDetailScreen(
                                productId = productId,
                                viewModel = viewModel,
                                onBack = { navController.popBackStack() },
                                onCartNavigate = {
                                    navController.navigate("cart") {
                                        popUpTo("home") { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                        
                        composable("cart") {
                            CartScreen(
                                viewModel = viewModel,
                                onBack = { navController.popBackStack() },
                                onCheckout = { navController.navigate("checkout") }
                            )
                        }
                        
                        composable("checkout") {
                            CheckoutScreen(
                                viewModel = viewModel,
                                onBack = { navController.popBackStack() },
                                onPaymentSuccess = {
                                    navController.navigate("orders") {
                                        popUpTo("home") { inclusive = false }
                                    }
                                }
                            )
                        }
                        
                        composable("orders") {
                            OrdersScreen(
                                viewModel = viewModel,
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable("profile") {
                            ProfileScreen(
                                viewModel = viewModel,
                                onNavigate = { route -> navController.navigate(route) }
                            )
                        }

                        composable("wishlist") {
                            WishlistScreen(
                                viewModel = viewModel,
                                onBack = { navController.popBackStack() },
                                onProductClick = { productId -> navController.navigate("product/$productId") }
                            )
                        }
                    }
                }
            }
        }
    }
}

data class BottomNavItem(val title: String, val route: String, val icon: ImageVector)
