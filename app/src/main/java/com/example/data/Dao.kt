package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface HimDao {
    // Users
    @Query("SELECT * FROM users LIMIT 1")
    fun getUserFlow(): Flow<UserEntity?>

    @Query("SELECT * FROM users LIMIT 1")
    suspend fun getUserSync(): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    // Drivers
    @Query("SELECT * FROM drivers")
    fun getAllDriversFlow(): Flow<List<DriverEntity>>

    @Query("SELECT * FROM drivers WHERE id = :id")
    suspend fun getDriverById(id: Int): DriverEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDriver(driver: DriverEntity)

    @Update
    suspend fun updateDriver(driver: DriverEntity)

    @Query("SELECT * FROM drivers WHERE status = 'APPROVED' AND isOnline = 1")
    suspend fun getActiveOnlineDrivers(): List<DriverEntity>

    // Bookings
    @Query("SELECT * FROM bookings ORDER BY timestamp DESC")
    fun getAllBookingsFlow(): Flow<List<BookingEntity>>

    @Query("SELECT * FROM bookings WHERE status = 'SEARCHING' OR status = 'MATCHED' OR status = 'EN_ROUTE' OR status = 'ARRIVED' OR status = 'STARTED' LIMIT 1")
    fun getActiveBookingFlow(): Flow<BookingEntity?>

    @Query("SELECT * FROM bookings WHERE status = 'SEARCHING' OR status = 'MATCHED' OR status = 'EN_ROUTE' OR status = 'ARRIVED' OR status = 'STARTED' LIMIT 1")
    suspend fun getActiveBookingSync(): BookingEntity?

    @Query("SELECT * FROM bookings WHERE id = :id")
    suspend fun getBookingById(id: Int): BookingEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: BookingEntity): Long

    @Update
    suspend fun updateBooking(booking: BookingEntity)

    // Packages
    @Query("SELECT * FROM subscription_packages WHERE isActive = 1")
    fun getActivePackagesFlow(): Flow<List<PackageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPackage(pkg: PackageEntity)

    @Delete
    suspend fun deletePackage(pkg: PackageEntity)

    // Ad Banners
    @Query("SELECT * FROM ad_banners WHERE isActive = 1 ORDER BY position ASC")
    fun getActiveBannersFlow(): Flow<List<AdBannerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBanner(banner: AdBannerEntity)

    @Delete
    suspend fun deleteBanner(banner: AdBannerEntity)

    // Transactions
    @Query("SELECT * FROM wallet_transactions ORDER BY timestamp DESC")
    fun getAllTransactionsFlow(): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)
}
