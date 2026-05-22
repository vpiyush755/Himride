package com.example.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.Delay
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class UserRole {
    PASSENGER, DRIVER, ADMIN
}

enum class PassengerTab {
    HOME, RIDES, PACKAGES, WALLET, PROFILE
}

enum class AdminTab {
    KYC, MONITORING, PACKAGES, CAMPAIGNS, ANALYTICS
}

class HimViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = HimRepository(database.himDao())

    // UI Routing & Navigation State
    var currentRole by mutableStateOf(UserRole.PASSENGER)
    var passengerTab by mutableStateOf(PassengerTab.HOME)
    var adminTab by mutableStateOf(AdminTab.KYC)

    // DB flows
    val userFlow = repository.userFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    val allDriversFlow = repository.allDriversFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList<DriverEntity>())
    val allBookingsFlow = repository.allBookingsFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList<BookingEntity>())
    val activeBookingFlow = repository.activeBookingFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    val packagesFlow = repository.activePackagesFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList<PackageEntity>())
    val adBannersFlow = repository.activeBannersFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList<AdBannerEntity>())
    val transactionsFlow = repository.allTransactionsFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList<TransactionEntity>())

    // Form states - Booking
    var fromLocation by mutableStateOf("Shimla")
    var toLocation by mutableStateOf("Manali")
    var selectedCategory by mutableStateOf("Cab") // Bike, Auto, Cab, Bus
    var promoCodeInput by mutableStateOf("")
    var currentPromoDiscount by mutableStateOf(0.0)
    var bookingPromoMessage by mutableStateOf("")

    // Form states - Driver Register
    var driverFormName by mutableStateOf("")
    var driverFormDob by mutableStateOf("1996-05-15")
    var driverFormAddress by mutableStateOf("")
    var driverFormAadhar by mutableStateOf("")
    var driverFormType by mutableStateOf("Cab")
    var driverFormMake by mutableStateOf("Hyundai")
    var driverFormModel by mutableStateOf("i20")
    var driverFormYear by mutableStateOf("2022")
    var driverFormColor by mutableStateOf("Silver")
    var driverFormPlate by mutableStateOf("")
    var driverFormBankName by mutableStateOf("")
    var driverFormBankAccount by mutableStateOf("")
    
    // Status tracking helper
    var driverOnboardingSuccessCode by mutableStateOf<String?>(null)

    // Simulation states
    var driverPopupBooking by mutableStateOf<BookingEntity?>(null)
    var driverPopupTimer by mutableStateOf(15)
    private var driverPopupJob: Job? = null

    var rideProgress by mutableStateOf(0f)
    var simLatitude by mutableStateOf(31.1048)
    var simLongitude by mutableStateOf(77.1734)

    // Active Simulation Job
    private var simulationJob: Job? = null

    // Helper to bypass standard type checking
    private fun HimRepository.activePackagesFlowFlow() = this.activePackagesFlow

    init {
        // Preload standard inputs if empty
        checkRegisterState()
    }

    private fun checkRegisterState() {
        viewModelScope.launch {
            val user = repository.getUserSync()
            if (user == null) {
                repository.insertUser(UserEntity(
                    name = "Piyush",
                    phone = "+91 98765 43210",
                    gender = "Male",
                    emergencyContact = "+91 91111 22222",
                    walletBalance = 650.0
                ))
            }
        }
    }

    // Booking Calculations
    fun getBaseFare(from: String, to: String, category: String): Double {
        val distanceFactor = when {
            from.lowercase() == to.lowercase() -> 1.0
            (from == "Shimla" && to == "Manali") || (from == "Manali" && to == "Shimla") -> 12.0
            (from == "Shimla" && to == "Chandigarh") || (from == "Chandigarh" && to == "Shimla") -> 15.0
            (from == "Manali" && to == "Delhi") || (from == "Delhi" && to == "Manali") -> 25.0
            (from == "Dharamshala" && to == "Shimla") || (from == "Shimla" && to == "Dharamshala") -> 10.0
            else -> 8.0
        }

        val multiplier = when (category) {
            "Bike" -> 15.0
            "Auto" -> 25.0
            "Cab" -> 45.0
            "Bus" -> 12.0
            else -> 30.0
        }
        return distanceFactor * multiplier
    }

    fun applyPromoCode() {
        if (promoCodeInput.trim().uppercase() == "HIMCOUPON") {
            currentPromoDiscount = 100.0
            bookingPromoMessage = "₹100 discount applied successfully! 🎉"
        } else if (promoCodeInput.isNotBlank()) {
            currentPromoDiscount = 0.0
            bookingPromoMessage = "Invalid promo code"
        } else {
            currentPromoDiscount = 0.0
            bookingPromoMessage = ""
        }
    }

    // Create a new Booking - Starts client-side SEARCHING status
    fun requestBooking() {
        viewModelScope.launch {
            val user = repository.getUserSync() ?: return@launch
            val baseFare = getBaseFare(fromLocation, toLocation, selectedCategory)
            val finalFare = (baseFare - currentPromoDiscount).coerceAtLeast(15.0)

            val booking = BookingEntity(
                userId = user.id,
                pickupName = fromLocation,
                pickupLat = 31.1048,
                pickupLng = 77.1734,
                dropName = toLocation,
                dropLat = 32.2396,
                dropLng = 77.1887,
                category = selectedCategory,
                fare = finalFare,
                coupon = if (currentPromoDiscount > 0) promoCodeInput else null,
                status = "SEARCHING"
            )

            val bookingId = repository.insertBooking(booking).toInt()
            
            // Check if there is an online approved driver of this category
            startDriverMatchingSim(bookingId)
        }
    }

    // Handle simulation inside Passenger flow
    private fun startDriverMatchingSim(bookingId: Int) {
        simulationJob?.cancel()
        simulationJob = viewModelScope.launch {
            delay(4000) // 4 seconds of matching animations

            val activeBooking = repository.getBookingById(bookingId) ?: return@launch
            if (activeBooking.status != "SEARCHING") return@launch

            // Fetch approved online drivers matching category if available, otherwise fallback to standard approved driver
            val candidates = allDriversFlow.value.filter {
                it.status == "APPROVED" && it.isOnline && (it.vehicleType == activeBooking.category)
            }

            val matchingDriver = if (candidates.isNotEmpty()) {
                candidates.random()
            } else {
                // Fallback to auto-approving a mock driver in case none are online
                DriverEntity(
                    id = 99,
                    name = "Manoj Kumar",
                    dob = "1990-01-01",
                    address = "Shimla",
                    aadharNum = "1234",
                    vehicleType = activeBooking.category,
                    vehicleMake = "Suzuki",
                    vehicleModel = "Dzire",
                    vehicleYear = "2021",
                    vehicleColor = "White",
                    registrationNumber = "HP-03-9999",
                    status = "APPROVED",
                    aadharImg = "", licenseImg = "", rcImg = "", selfieImg = "",
                    bankName = "", bankAccount = "",
                    isOnline = true
                )
            }

            // Sync booking with matched driver and trigger EN_ROUTE
            val matchedBooking = activeBooking.copy(
                status = "MATCHED",
                driverId = matchingDriver.id,
                driverName = matchingDriver.name,
                driverPhone = "+91 94180 ${Random.nextInt(10000, 99999)}",
                vehiclePlate = matchingDriver.registrationNumber,
                otp = Random.nextInt(1000, 9999).toString()
            )
            repository.updateBooking(matchedBooking)

            // Trigger "EN_ROUTE" simulation (Driver heading to pickup)
            delay(3000)
            repository.updateBooking(matchedBooking.copy(status = "EN_ROUTE"))

            // Trigger "ARRIVED" status
            delay(3000)
            repository.updateBooking(matchedBooking.copy(status = "ARRIVED"))
        }
    }

    // Passenger confirms OTP to start the ride
    fun verifyOtpAndStartRide(otpInput: String): Boolean {
        var success = false
        val job = viewModelScope.launch {
            val booking = repository.getActiveBookingSync() ?: return@launch
            if (booking.otp == otpInput) {
                success = true
                val updated = booking.copy(status = "STARTED")
                repository.updateBooking(updated)
                
                // Start movement coordinates simulation
                simulateRideMovement(updated)
            }
        }
        // block briefly to resolve
        runBlockingLike(job)
        return success
    }

    // Simulates live ride tracking progress coordinate updates
    private fun simulateRideMovement(booking: BookingEntity) {
        simulationJob?.cancel()
        simulationJob = viewModelScope.launch {
            val steps = 10
            val pLat = booking.pickupLat
            val pLng = booking.pickupLng
            val dLat = booking.dropLat
            val dLng = booking.dropLng

            for (i in 1..steps) {
                delay(1200)
                rideProgress = i.toFloat() / steps.toFloat()
                simLatitude = pLat + (dLat - pLat) * rideProgress
                simLongitude = pLng + (dLng - pLng) * rideProgress
            }

            // Ride Complete! Deduct balance and update room state
            val completedBooking = booking.copy(status = "COMPLETED")
            repository.updateBooking(completedBooking)

            val user = repository.getUserSync()
            if (user != null) {
                val newBal = (user.walletBalance - booking.fare).coerceAtLeast(0.0)
                repository.updateUser(user.copy(walletBalance = newBal))
                repository.insertTransaction(
                    TransactionEntity(
                        title = "Ride completed to ${booking.dropName}",
                        type = "DEBIT",
                        amount = booking.fare
                    )
                )
            }

            // Settle Driver account if matched driver exists
            booking.driverId?.let { drId ->
                val dr = repository.getDriverById(drId)
                if (dr != null) {
                    val driverShare = booking.fare * 0.85 // 15% commission to HimRide Admin
                    // Log transfer or simulated earnings
                }
            }

            rideProgress = 0f
        }
    }

    private fun runBlockingLike(job: Job) {
        // Simple polling wait for ViewModelScope
        val limit = 50
        var counter = 0
        while (!job.isCompleted && counter < limit) {
            Thread.sleep(10)
            counter++
        }
    }

    // Cancel Active Booking
    fun cancelActiveBooking() {
        simulationJob?.cancel()
        viewModelScope.launch {
            val booking = repository.getActiveBookingSync() ?: return@launch
            repository.updateBooking(booking.copy(status = "CANCELLED"))
            repository.insertTransaction(
                TransactionEntity(
                    title = "Ride Cancelled",
                    type = "CREDIT",
                    amount = 0.0
                )
            )
        }
    }

    // Submit Passenger Rating
    fun submitRideRating(bookingId: Int, score: Int) {
        viewModelScope.launch {
            val b = repository.getBookingById(bookingId) ?: return@launch
            repository.updateBooking(b.copy(rating = score))
        }
    }

    // Driver Onboarding registration flow
    fun registerDriverProfile(selfieFilePath: String = "sim_selfie.jpg") {
        viewModelScope.launch {
            val newDriver = DriverEntity(
                name = driverFormName.ifBlank { "Prem Sen" },
                dob = driverFormDob,
                address = driverFormAddress.ifBlank { "Lower Mall Road, Kasol" },
                aadharNum = driverFormAadhar.ifBlank { "4321 8765 9123" },
                vehicleType = driverFormType,
                vehicleMake = driverFormMake,
                vehicleModel = driverFormModel,
                vehicleYear = driverFormYear,
                vehicleColor = driverFormColor,
                registrationNumber = driverFormPlate.ifBlank { "HP-34-D-8080" },
                status = "PENDING",
                aadharImg = "Aadhar Doc Uploaded (Simulated)",
                licenseImg = "License Doc Uploaded (Simulated)",
                rcImg = "Registration RC Card (Simulated)",
                selfieImg = selfieFilePath,
                bankName = driverFormBankName.ifBlank { "HDFC Bank" },
                bankAccount = driverFormBankAccount.ifBlank { "98765432123" }
            )
            repository.insertDriver(newDriver)
            driverOnboardingSuccessCode = "REGISTERED"
        }
    }

    // Switch driver online status
    fun toggleDriverOnlineState(driverId: Int, online: Boolean) {
        viewModelScope.launch {
            val dr = repository.getDriverById(driverId) ?: return@launch
            repository.updateDriver(dr.copy(isOnline = online))
            
            if (online) {
                // Periodically simulate an incoming customer request!
                startDriverIncomingJobSimulation(driverId)
            } else {
                driverPopupJob?.cancel()
                driverPopupBooking = null
            }
        }
    }

    private fun startDriverIncomingJobSimulation(driverId: Int) {
        driverPopupJob?.cancel()
        driverPopupJob = viewModelScope.launch {
            while (true) {
                delay(12000) // every 12 seconds check for incoming jobs
                val active = repository.getActiveBookingSync()
                if (active != null && active.status == "SEARCHING" && active.driverId == null) {
                    // Match directly!
                    driverPopupBooking = active
                    for (timer in 15 downTo 1) {
                        driverPopupTimer = timer
                        delay(1000)
                    }
                    // Expired
                    driverPopupBooking = null
                } else if (Random.nextBoolean()) {
                    // Create totally random passenger simulated trip request!
                    val cities = listOf("Shimla", "Manali", "Dharamshala", "Kasauli", "Kullu")
                    val pickup = cities.random()
                    val drop = cities.filter { it != pickup }.random()
                    val category = listOf("Bike", "Auto", "Cab").random()
                    val fare = getBaseFare(pickup, drop, category)

                    val newBooking = BookingEntity(
                        id = Random.nextInt(1000, 9999), // Mock ID
                        userId = 1,
                        pickupName = pickup,
                        pickupLat = 31.1048,
                        pickupLng = 77.1734,
                        dropName = drop,
                        dropLat = 32.2396,
                        dropLng = 77.1887,
                        category = category,
                        fare = fare,
                        coupon = null,
                        status = "SEARCHING"
                    )
                    driverPopupBooking = newBooking
                    
                    for (timer in 15 downTo 1) {
                        driverPopupTimer = timer
                        delay(1000)
                        if (driverPopupBooking == null) break // Accepted
                    }
                    driverPopupBooking = null
                }
            }
        }
    }

    // Driver accepts popup trip
    fun driverAcceptTrip(bookingId: Int, driverId: Int) {
        viewModelScope.launch {
            val dbBooking = repository.getBookingById(bookingId)
            val driver = repository.getDriverById(driverId) ?: return@launch
            
            if (dbBooking != null) {
                val updated = dbBooking.copy(
                    status = "MATCHED",
                    driverId = driver.id,
                    driverName = driver.name,
                    driverPhone = "+91 98160 55021",
                    vehiclePlate = driver.registrationNumber,
                    otp = "5858"
                )
                repository.updateBooking(updated)
            } else {
                // If it was purely a simulated trip not yet in Room, insert it
                driverPopupBooking?.let { b ->
                    val bToInsert = b.copy(
                        id = 0, // auto gen
                        status = "MATCHED",
                        driverId = driver.id,
                        driverName = driver.name,
                        driverPhone = "+91 98160 55021",
                        vehiclePlate = driver.registrationNumber,
                        otp = "5858"
                    )
                    repository.insertBooking(bToInsert)
                }
            }
            driverPopupBooking = null
            driverPopupJob?.cancel()
        }
    }

    fun driverAdvanceTripStatus(bookingId: Int) {
        viewModelScope.launch {
            val b = repository.getBookingById(bookingId) ?: return@launch
            val nextStatus = when (b.status) {
                "MATCHED" -> "EN_ROUTE"
                "EN_ROUTE" -> "ARRIVED"
                "ARRIVED" -> "STARTED"
                "STARTED" -> "COMPLETED"
                else -> b.status
            }
            
            val updated = b.copy(status = nextStatus)
            repository.updateBooking(updated)

            if (nextStatus == "COMPLETED") {
                val user = repository.getUserSync()
                if (user != null) {
                    val bal = (user.walletBalance - b.fare).coerceAtLeast(0.0)
                    repository.updateUser(user.copy(walletBalance = bal))
                }
                repository.insertTransaction(
                    TransactionEntity(
                        title = "Completed ride for Passenger ${user?.name ?: "Guest"}",
                        type = "CREDIT",
                        amount = b.fare * 0.85
                    )
                )
            }
        }
    }

    // Admin KYC Controls
    fun adminApproveDriver(driverId: Int) {
        viewModelScope.launch {
            val dr = repository.getDriverById(driverId) ?: return@launch
            repository.updateDriver(dr.copy(status = "APPROVED", rejectionReason = null))
        }
    }

    fun adminRejectDriver(driverId: Int, reason: String) {
        viewModelScope.launch {
            val dr = repository.getDriverById(driverId) ?: return@launch
            repository.updateDriver(dr.copy(status = "REJECTED", rejectionReason = reason))
        }
    }

    // Admin Ad campaignsCRUD
    fun adminAddCampaign(title: String, imgUrl: String, redirect: String, pos: Int = 1) {
        viewModelScope.launch {
            val ad = AdBannerEntity(
                title = title,
                imageUrl = imgUrl,
                targetUrl = redirect,
                position = pos
            )
            repository.insertBanner(ad)
        }
    }

    fun adminDeleteCampaign(banner: AdBannerEntity) {
        viewModelScope.launch {
            repository.deleteBanner(banner)
        }
    }

    // Admin PackagesCRUD
    fun adminAddPackage(name: String, rides: Int, days: Int, price: Double, badge: String, city: String) {
        viewModelScope.launch {
            val pkg = PackageEntity(
                name = name,
                ridesCount = rides,
                validityDays = days,
                price = price,
                discountBadge = badge,
                city = city
            )
            repository.insertPackage(pkg)
        }
    }

    fun adminDeletePackage(pkg: PackageEntity) {
        viewModelScope.launch {
            repository.deletePackage(pkg)
        }
    }

    // Wallet deposit
    fun addWalletMoney(amount: Double) {
        viewModelScope.launch {
            val user = repository.getUserSync() ?: return@launch
            repository.updateUser(user.copy(walletBalance = user.walletBalance + amount))
            repository.insertTransaction(
                TransactionEntity(
                    title = "Wallet Topup (Simulated Razorpay Invoice)",
                    type = "CREDIT",
                    amount = amount
                )
            )
        }
    }

    // Purchase active package subscription
    fun purchasePackage(pkg: PackageEntity) {
        viewModelScope.launch {
            val user = repository.getUserSync() ?: return@launch
            if (user.walletBalance >= pkg.price) {
                repository.updateUser(user.copy(
                    walletBalance = user.walletBalance - pkg.price,
                    activeSubscriptionName = pkg.name
                ))
                repository.insertTransaction(
                    TransactionEntity(
                        title = "Purchased Sub: ${pkg.name}",
                        type = "DEBIT",
                        amount = pkg.price
                    )
                )
            }
        }
    }

    fun saveUserProfile(name: String, phone: String, emergency: String) {
        viewModelScope.launch {
            val user = repository.getUserSync()
            val currentU = user ?: UserEntity(name = name, phone = phone, gender = "Male", emergencyContact = emergency)
            repository.insertUser(currentU.copy(name = name, phone = phone, emergencyContact = emergency))
        }
    }
}
