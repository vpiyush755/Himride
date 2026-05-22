package com.example.data

import kotlinx.coroutines.flow.Flow

class HimRepository(private val himDao: HimDao) {
    val userFlow: Flow<UserEntity?> = himDao.getUserFlow()
    val allDriversFlow: Flow<List<DriverEntity>> = himDao.getAllDriversFlow()
    val allBookingsFlow: Flow<List<BookingEntity>> = himDao.getAllBookingsFlow()
    val activeBookingFlow: Flow<BookingEntity?> = himDao.getActiveBookingFlow()
    val activePackagesFlow: Flow<List<PackageEntity>> = himDao.getActivePackagesFlow()
    val activeBannersFlow: Flow<List<AdBannerEntity>> = himDao.getActiveBannersFlow()
    val allTransactionsFlow: Flow<List<TransactionEntity>> = himDao.getAllTransactionsFlow()

    suspend fun getUserSync() = himDao.getUserSync()
    suspend fun insertUser(user: UserEntity) = himDao.insertUser(user)
    suspend fun updateUser(user: UserEntity) = himDao.updateUser(user)

    suspend fun getDriverById(id: Int) = himDao.getDriverById(id)
    suspend fun insertDriver(driver: DriverEntity) = himDao.insertDriver(driver)
    suspend fun updateDriver(driver: DriverEntity) = himDao.updateDriver(driver)
    suspend fun getActiveOnlineDrivers() = himDao.getActiveOnlineDrivers()

    suspend fun getActiveBookingSync() = himDao.getActiveBookingSync()
    suspend fun getBookingById(id: Int) = himDao.getBookingById(id)
    suspend fun insertBooking(booking: BookingEntity): Long = himDao.insertBooking(booking)
    suspend fun updateBooking(booking: BookingEntity) = himDao.updateBooking(booking)

    suspend fun insertPackage(pkg: PackageEntity) = himDao.insertPackage(pkg)
    suspend fun deletePackage(pkg: PackageEntity) = himDao.deletePackage(pkg)

    suspend fun insertBanner(banner: AdBannerEntity) = himDao.insertBanner(banner)
    suspend fun deleteBanner(banner: AdBannerEntity) = himDao.deleteBanner(banner)

    suspend fun insertTransaction(txn: TransactionEntity) = himDao.insertTransaction(txn)
}
