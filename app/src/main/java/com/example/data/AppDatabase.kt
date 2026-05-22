package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        DriverEntity::class,
        BookingEntity::class,
        PackageEntity::class,
        AdBannerEntity::class,
        TransactionEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun himDao(): HimDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "himride_database"
                )
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Pre-populate data using a background coroutine context
                        CoroutineScope(Dispatchers.IO).launch {
                            val dao = getDatabase(context).himDao()
                            prepopulateData(dao)
                        }
                    }
                })
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun prepopulateData(dao: HimDao) {
            // 1. Prepopulate default User
            if (dao.getUserSync() == null) {
                dao.insertUser(
                    UserEntity(
                        name = "Piyush",
                        phone = "+91 98765 43210",
                        gender = "Male",
                        emergencyContact = "+91 91111 22222",
                        walletBalance = 650.0
                    )
                )
            }

            // 2. Prepopulate Packages
            val defaultPkgs = listOf(
                PackageEntity(
                    name = "Shimla Daily Pass",
                    ridesCount = 5,
                    validityDays = 7,
                    price = 199.0,
                    discountBadge = "SAVE 30%",
                    city = "Shimla"
                ),
                PackageEntity(
                    name = "Manali Tourist Pass",
                    ridesCount = 10,
                    validityDays = 14,
                    price = 399.0,
                    discountBadge = "BEST SELLER",
                    city = "Manali"
                ),
                PackageEntity(
                    name = "Himachal Unlimited",
                    ridesCount = 999,
                    validityDays = 30,
                    price = 999.0,
                    discountBadge = "MEGA VALUE",
                    city = "All Cities"
                )
            )
            for (pkg in defaultPkgs) {
                dao.insertPackage(pkg)
            }

            // 3. Prepopulate Ad Banners with highly relevant real-world scenic images of Himachal Pradesh (Himachal theme)
            val defaultBanners = listOf(
                AdBannerEntity(
                    title = "Discover Shimla Mall Road",
                    imageUrl = "https://images.unsplash.com/photo-1597074866923-dc0589150358?auto=format&fit=crop&q=80&w=600", // Famous Shimla ridge architecture
                    targetUrl = "https://hp.gov.in",
                    position = 1
                ),
                AdBannerEntity(
                    title = "Ride the Scenic Roads of Manali",
                    imageUrl = "https://images.unsplash.com/photo-1605649487212-47bdab064df7?auto=format&fit=crop&q=80&w=600", // Manali snow mountain roads
                    targetUrl = "test_trip",
                    position = 2
                ),
                AdBannerEntity(
                    title = "Explore Paragliding in Bir Billing",
                    imageUrl = "https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?auto=format&fit=crop&q=80&w=600", // Paragliding over hills
                    targetUrl = "https://tourism.hp.gov.in",
                    position = 3
                )
            )
            for (banner in defaultBanners) {
                dao.insertBanner(banner)
            }

            // 4. Prepopulate initial wallet Transactions
            val defaultTxns = listOf(
                TransactionEntity(
                    title = "Welcome Bonus Added",
                    type = "CREDIT",
                    amount = 500.0,
                    timestamp = System.currentTimeMillis() - 86400000 * 2
                ),
                TransactionEntity(
                    title = "Dharamshala Local Ride",
                    type = "DEBIT",
                    amount = 120.0,
                    timestamp = System.currentTimeMillis() - 86400000
                ),
                TransactionEntity(
                    title = "Wallet Topup (Razorpay)",
                    type = "CREDIT",
                    amount = 270.0,
                    timestamp = System.currentTimeMillis() - 3600000 * 2
                )
            )
            for (txn in defaultTxns) {
                dao.insertTransaction(txn)
            }

            // 5. Prepopulate standard mock drivers in active, approved state for matching simulator
            val defaultDrivers = listOf(
                DriverEntity(
                    name = "Rajesh Thakur",
                    dob = "1992-04-12",
                    address = "The Ridge, Shimla Main Post",
                    aadharNum = "1234 5678 9012",
                    vehicleType = "Cab",
                    vehicleMake = "Maruti Suzuki",
                    vehicleModel = "Swift Dzire",
                    vehicleYear = "2021",
                    vehicleColor = "White",
                    registrationNumber = "HP-03-A-2022",
                    status = "APPROVED",
                    aadharImg = "AadharUploaded",
                    licenseImg = "LicenseUploaded",
                    rcImg = "RcUploaded",
                    selfieImg = "SelfieUploaded",
                    bankName = "State Bank of India",
                    bankAccount = "123456789012",
                    isOnline = true,
                    currentLat = 31.1048,
                    currentLng = 77.1734
                ),
                DriverEntity(
                    name = "Sunil Sharma",
                    dob = "1995-08-25",
                    address = "VPO Old Manali, Distt Kullu",
                    aadharNum = "5678 1234 9012",
                    vehicleType = "Bike",
                    vehicleMake = "Royal Enfield",
                    vehicleModel = "Classic 350",
                    vehicleYear = "2022",
                    vehicleColor = "Black",
                    registrationNumber = "HP-58-B-3200",
                    status = "APPROVED",
                    aadharImg = "AadharUploaded",
                    licenseImg = "LicenseUploaded",
                    rcImg = "RcUploaded",
                    selfieImg = "SelfieUploaded",
                    bankName = "Himachal Pradesh Gramin Bank",
                    bankAccount = "987654321098",
                    isOnline = true,
                    currentLat = 31.1085,
                    currentLng = 77.1700
                ),
                DriverEntity(
                    name = "Amit Kumar",
                    dob = "1994-11-05",
                    address = "Kotwali Bazar, Dharamshala",
                    aadharNum = "9012 1234 5678",
                    vehicleType = "Auto",
                    vehicleMake = "Bajaj",
                    vehicleModel = "RE Maxima",
                    vehicleYear = "2020",
                    vehicleColor = "Yellow",
                    registrationNumber = "HP-48-C-5431",
                    status = "APPROVED",
                    aadharImg = "AadharUploaded",
                    licenseImg = "LicenseUploaded",
                    rcImg = "RcUploaded",
                    selfieImg = "SelfieUploaded",
                    bankName = "Punjab National Bank",
                    bankAccount = "111122223333",
                    isOnline = true,
                    currentLat = 31.1020,
                    currentLng = 77.1780
                )
            )
            for (dr in defaultDrivers) {
                dao.insertDriver(dr)
            }
        }
    }
}
