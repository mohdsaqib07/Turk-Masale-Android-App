package com.example.data.repository

import com.example.data.local.AddressEntity
import com.example.data.local.AppDao
import com.example.data.local.ReviewEntity
import com.example.data.local.UserEntity
import com.example.data.local.WishlistEntity

class AppRepository(private val appDao: AppDao) {
    val currentUser = appDao.getCurrentUser()
    val wishlist = appDao.getWishlist()
    
    fun getReviews(productId: String) = appDao.getReviews(productId)
    fun getUserAddresses(userId: String) = appDao.getUserAddresses(userId)
    
    suspend fun insertUser(user: UserEntity) = appDao.insertUser(user)
    suspend fun logoutUser() = appDao.logoutUser()
    suspend fun insertAddress(address: AddressEntity) = appDao.insertAddress(address)
    
    suspend fun toggleWishlist(productId: String, isContained: Boolean) {
        if (isContained) {
            appDao.removeFromWishlist(productId)
        } else {
            appDao.addToWishlist(WishlistEntity(productId = productId))
        }
    }
    
    suspend fun insertReview(review: ReviewEntity) = appDao.insertReview(review)
}
