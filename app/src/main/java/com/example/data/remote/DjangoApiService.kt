package com.example.data.remote

import com.example.data.model.Product
import com.example.data.model.Store
import com.example.data.model.WalletAccount
import com.example.data.model.WalletTransaction
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Django REST Framework API Service.
 * Matches standard DRF ViewSets / APIViews on the Django backend.
 */
interface DjangoApiService {

    // 1. MStores & Multi-vendor endpoints
    @GET("stores/")
    suspend fun getStores(): Response<List<Store>>

    @GET("products/")
    suspend fun getProducts(
        @Query("store_id") storeId: Int? = null,
        @Query("category") category: String? = null,
        @Query("search") search: String? = null
    ): Response<List<Product>>

    // 2. Authentication with Phone and Password (as requested)
    @POST("auth/login/")
    suspend fun login(
        @Body request: LoginPayload
    ): Response<AuthTokenResponse>

    // 3. Jeeb Wallet endpoints (Balance, Transactions, Pay)
    @GET("wallet/account/")
    suspend fun getWalletAccount(
        @Header("Authorization") token: String
    ): Response<WalletAccount>

    @GET("wallet/transactions/")
    suspend fun getWalletTransactions(
        @Header("Authorization") token: String
    ): Response<List<WalletTransaction>>

    @POST("wallet/transfer/")
    suspend fun transferFunds(
        @Header("Authorization") token: String,
        @Body payload: TransferPayload
    ): Response<WalletTransaction>

    // 4. Order creation & Checkout
    @POST("orders/checkout/")
    suspend fun checkoutOrder(
        @Header("Authorization") token: String,
        @Body payload: CheckoutPayload
    ): Response<CheckoutResponse>
}

data class LoginPayload(
    val phone: String,
    val password: String
)

data class AuthTokenResponse(
    val token: String,
    val user_id: Int,
    val username: String,
    val phone: String,
    val is_active: Boolean
)

data class TransferPayload(
    val recipient_phone_or_account: String,
    val amount: Double,
    val currency: String,
    val note: String? = null
)

data class CheckoutPayload(
    val store_id: Int,
    val items: List<CheckoutItemPayload>,
    val payment_method: String, // "JEEB_WALLET" or "CASH_ON_DELIVERY"
    val delivery_address: String
)

data class CheckoutItemPayload(
    val product_id: Int,
    val quantity: Int
)

data class CheckoutResponse(
    val order_id: String,
    val status: String,
    val total: Double,
    val message: String
)
