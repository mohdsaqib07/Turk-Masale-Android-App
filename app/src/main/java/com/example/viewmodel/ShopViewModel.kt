package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.data.CartItem
import com.example.data.FakeData
import com.example.data.Order
import com.example.data.Product
import com.example.data.ProductVariant
import com.example.data.local.AddressEntity
import com.example.data.local.AppDatabase
import com.example.data.local.ReviewEntity
import com.example.data.local.UserEntity
import com.example.data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class ShopViewModel(application: Application) : AndroidViewModel(application) {
    private val db = Room.databaseBuilder(
        application,
        AppDatabase::class.java, "turk_masale_db"
    ).build()
    private val repository = AppRepository(db.appDao())

    val currentUser = repository.currentUser.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), null
    )
    
    val wishlist = repository.wishlist.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    
    fun getReviews(productId: String) = repository.getReviews(productId).stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    
    fun getUserAddresses() = repository.getUserAddresses(currentUser.value?.id ?: "").stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    fun loginUser(name: String, email: String) {
        viewModelScope.launch {
            repository.insertUser(UserEntity(id = UUID.randomUUID().toString(), name = name, email = email, phone = ""))
        }
    }
    
    fun logout() {
        viewModelScope.launch { repository.logoutUser() }
    }
    
    fun addAddress(address: String) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            repository.insertAddress(AddressEntity(userId = user.id, fullAddress = address, isDefault = false))
        }
    }

    fun toggleWishlist(productId: String) {
        viewModelScope.launch {
            val isContained = wishlist.value.any { it.productId == productId }
            repository.toggleWishlist(productId, isContained)
        }
    }
    
    fun submitReview(productId: String, rating: Int, comment: String) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            repository.insertReview(
                ReviewEntity(productId = productId, userName = user.name, rating = rating, comment = comment)
            )
        }
    }
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    val products = FakeData.products

    fun addToCart(product: Product, variant: ProductVariant, quantity: Int = 1) {
        val currentCart = _cartItems.value.toMutableList()
        val existingItemIndex = currentCart.indexOfFirst { it.product.id == product.id && it.variant.size == variant.size }
        if (existingItemIndex >= 0) {
            val existingItem = currentCart[existingItemIndex]
            currentCart[existingItemIndex] = existingItem.copy(quantity = existingItem.quantity + quantity)
        } else {
            currentCart.add(CartItem(product, variant, quantity))
        }
        _cartItems.value = currentCart
    }

    fun removeFromCart(productId: String, size: String) {
        _cartItems.value = _cartItems.value.filter { !(it.product.id == productId && it.variant.size == size) }
    }

    fun updateQuantity(productId: String, size: String, newQuantity: Int) {
        if (newQuantity <= 0) {
            removeFromCart(productId, size)
            return
        }
        _cartItems.value = _cartItems.value.map {
            if (it.product.id == productId && it.variant.size == size) it.copy(quantity = newQuantity) else it
        }
    }

    val cartTotal: Double
        get() = _cartItems.value.sumOf { it.variant.price * it.quantity }

    fun checkout() {
        if (_cartItems.value.isEmpty()) return
        
        val newOrder = Order(
            orderId = "ORD-${UUID.randomUUID().toString().substring(0, 6).uppercase()}",
            date = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date()),
            totalAmount = cartTotal,
            status = "Processing"
        )
        
        _orders.value = listOf(newOrder) + _orders.value
        _cartItems.value = emptyList() // Clear cart
    }
}
