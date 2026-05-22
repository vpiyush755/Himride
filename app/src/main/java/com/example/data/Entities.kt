package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val phone: String,
    val gender: String,
    val emergencyContact: String,
    val walletBalance: Double = 500.0,
    val activeSubscriptionName: String? = null
)

@Entity(tableName = "drivers")
data class DriverEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val dob: String,
    val address: String,
    val aadharNum: String,
    val vehicleType: String, // Bike, Auto, Cab, Bus
    val vehicleMake: String,
    val vehicleModel: String,
    val vehicleYear: String,
    val vehicleColor: String,
    val registrationNumber: String,
    val status: String = "PENDING", // PENDING, APPROVED, REJECTED
    val rejectionReason: String? = null,
    val aadharImg: String,
    val licenseImg: String,
    val rcImg: String,
    val selfieImg: String,
    val bankName: String,
    val bankAccount: String,
    val isOnline: Boolean = false,
    val currentLat: Double = 31.1048, // Default Shimla coords
    val currentLng: Double = 77.1734
)

@Entity(tableName = "bookings")
data class BookingEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val pickupName: String,
    val pickupLat: Double,
    val pickupLng: Double,
    val dropName: String,
    val dropLat: Double,
    val dropLng: Double,
    val category: String, // Bike, Auto, Cab, Bus
    val fare: Double,
    val coupon: String?,
    val status: String, // SEARCHING, MATCHED, EN_ROUTE, ARRIVED, STARTED, COMPLETED, CANCELLED
    val driverId: Int? = null,
    val driverName: String? = null,
    val driverPhone: String? = null,
    val vehiclePlate: String? = null,
    val otp: String = "1234",
    val rating: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "subscription_packages")
data class PackageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val ridesCount: Int,
    val validityDays: Int,
    val price: Double,
    val discountBadge: String,
    val city: String = "Shimla",
    val isActive: Boolean = true
)

@Entity(tableName = "ad_banners")
data class AdBannerEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val imageUrl: String,
    val targetUrl: String,
    val position: Int,
    val isActive: Boolean = true
)

@Entity(tableName = "wallet_transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val type: String, // CREDIT, DEBIT
    val amount: Double,
    val timestamp: Long = System.currentTimeMillis()
)
