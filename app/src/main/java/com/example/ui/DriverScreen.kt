package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.theme.*
import com.example.viewmodel.HimViewModel
import kotlinx.coroutines.delay

@Composable
fun DriverScreen(viewModel: HimViewModel) {
    val drivers by viewModel.allDriversFlow.collectAsState()
    val bookings by viewModel.allBookingsFlow.collectAsState()

    // Let's check if the driver is registered. 
    // To make this super usable, we treat the first non-mock driver or a driver with name matching formName as ourselves,
    // or we can allow creating multiple drivers and matching. 
    // Let's grab the driver we created or are working with.
    val myDriver = drivers.find { it.name == viewModel.driverFormName || it.id > 3 } 
        ?: drivers.find { it.id == 99 } // Fallback to a default
        ?: drivers.firstOrNull()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DriverNavy)
    ) {
        if (myDriver == null || viewModel.driverOnboardingSuccessCode == null) {
            // Document onboarding flow
            DriverOnboardingModule(viewModel)
        } else {
            when (myDriver.status) {
                "PENDING" -> DriverPendingKYCScreen(viewModel, myDriver)
                "REJECTED" -> DriverRejectedKYCScreen(viewModel, myDriver)
                "APPROVED" -> DriverDashboardScreen(viewModel, myDriver, bookings)
            }
        }
    }
}

@Composable
fun DriverOnboardingModule(viewModel: HimViewModel) {
    var step by remember { mutableStateOf(1) }
    val maxSteps = 4

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App title
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "HimRide Partner Onboarding",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                "Step $step of $maxSteps",
                fontSize = 12.sp,
                color = DriverMint,
                fontWeight = FontWeight.Bold
            )
        }

        LinearProgressIndicator(
            progress = { step.toFloat() / maxSteps.toFloat() },
            modifier = Modifier.fillMaxWidth(),
            color = DriverMint,
            trackColor = DriverNavyLight
        )

        when (step) {
            1 -> {
                // Personal details
                Text("Personal Information Details", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text("Enter basic identity details required by Shimla Licensing Office.", fontSize = 11.sp, color = Color.LightGray)

                OutlinedTextField(
                    value = viewModel.driverFormName,
                    onValueChange = { viewModel.driverFormName = it },
                    label = { Text("Driver Full Name") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = DriverMint,
                        focusedBorderColor = DriverMint
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = viewModel.driverFormDob,
                    onValueChange = { viewModel.driverFormDob = it },
                    label = { Text("Date of Birth (YYYY-MM-DD)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = DriverMint,
                        focusedBorderColor = DriverMint
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = viewModel.driverFormAddress,
                    onValueChange = { viewModel.driverFormAddress = it },
                    label = { Text("Full Home Address (H.P.)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = DriverMint,
                        focusedBorderColor = DriverMint
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = viewModel.driverFormAadhar,
                    onValueChange = { viewModel.driverFormAadhar = it },
                    label = { Text("UIDAI Aadhar Number") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = DriverMint,
                        focusedBorderColor = DriverMint
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            2 -> {
                // Vehicle details
                Text("Vehicle Fleet Registry", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text("Specify details of the carrier you are driving.", fontSize = 11.sp, color = Color.LightGray)

                Text("Vehicle Category type", fontSize = 12.sp, color = Color.LightGray)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Bike", "Auto", "Cab").forEach { type ->
                        val selected = viewModel.driverFormType == type
                        Button(
                            onClick = { viewModel.driverFormType = type },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selected) DriverMint else DriverNavyLight,
                                contentColor = if (selected) Color.White else Color.LightGray
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(type)
                        }
                    }
                }

                OutlinedTextField(
                    value = viewModel.driverFormMake,
                    onValueChange = { viewModel.driverFormMake = it },
                    label = { Text("Vehicle Brand Make (e.g. Maruti)") },
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, focusedBorderColor = DriverMint),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = viewModel.driverFormModel,
                    onValueChange = { viewModel.driverFormModel = it },
                    label = { Text("Vehicle Model (e.g. Swift)") },
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, focusedBorderColor = DriverMint),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = viewModel.driverFormPlate,
                    onValueChange = { viewModel.driverFormPlate = it },
                    label = { Text("Registration Plate (e.g. HP-03-A-1234)") },
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, focusedBorderColor = DriverMint),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            3 -> {
                // Documents upload
                Text("Attach Digital KYC Photocopies", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text("Documents must be clearly read under sunlight.", fontSize = 11.sp, color = Color.LightGray)

                val items = listOf("Upload Aadhar Card Copy", "Upload Driving Licence Desk", "Upload RC Smartcard photo", "Click Selfie with Vehicle")
                items.forEach { doc ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = DriverNavyLight)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(doc, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Default.CheckCircle, "Done", tint = DriverMint, modifier = Modifier.size(16.dp))
                                Text("READY", color = DriverMint, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            4 -> {
                // Bank and Finish
                Text("Bank Settlements Account", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text("Payouts are settled directly to this account via Cashfree API automatically daily.", fontSize = 11.sp, color = Color.LightGray)

                OutlinedTextField(
                    value = viewModel.driverFormBankName,
                    onValueChange = { viewModel.driverFormBankName = it },
                    label = { Text("Bank Holder Name") },
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, focusedBorderColor = DriverMint),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = viewModel.driverFormBankAccount,
                    onValueChange = { viewModel.driverFormBankAccount = it },
                    label = { Text("Bank Account / IFSC branch Code") },
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, focusedBorderColor = DriverMint),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { viewModel.registerDriverProfile() },
                    colors = ButtonDefaults.buttonColors(containerColor = DriverMint),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                ) {
                    Text("Submit KYC File for Review", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (step > 1) {
                TextButton(onClick = { step-- }) {
                    Text("Previous", color = Color.LightGray)
                }
            } else {
                Spacer(modifier = Modifier.width(1.dp))
            }

            if (step < maxSteps) {
                Button(
                    onClick = { step++ },
                    colors = ButtonDefaults.buttonColors(containerColor = DriverMint)
                ) {
                    Text("Continue")
                }
            }
        }
    }
}

@Composable
fun DriverPendingKYCScreen(viewModel: HimViewModel, driver: DriverEntity) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(Color.Gray.copy(alpha = 0.2f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.HourglassEmpty, "Hourglass", tint = Color.LightGray, modifier = Modifier.size(54.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Your Documents are Under Review", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            "Account: ${driver.name}\nVehicle: ${driver.vehicleMake} ${driver.vehicleModel}\n\nOur Himachal operators verify driver registry licenses manually. This takes approximately 24-48 hours. We will notify you inside the app.",
            fontSize = 12.sp,
            color = Color.LightGray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(30.dp))

        // Guide how to test
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DriverNavyLight)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("💡 Demo Testing Guide", fontWeight = FontWeight.Bold, color = DriverMint, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text("Select the 'Admin Panel' role from the switcher bar above, navigate to the 'KYC Approvals' tab, and click Approve on your registration card to immediately unlock this driver dashboard!", fontSize = 11.sp, color = Color.LightGray)
            }
        }
    }
}

@Composable
fun DriverRejectedKYCScreen(viewModel: HimViewModel, driver: DriverEntity) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.Cancel, "Rejected", tint = Color.Red, modifier = Modifier.size(64.dp))
        Spacer(modifier = Modifier.height(20.dp))

        Text("KYC Verification Rejected", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Reason: ${driver.rejectionReason ?: "Aadhar documents are blurry or invalid"}",
            color = Color.Red,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = { viewModel.driverOnboardingSuccessCode = null },
            colors = ButtonDefaults.buttonColors(containerColor = DriverMint)
        ) {
            Text("Edit Onboarding Documents & Resubmit", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun DriverDashboardScreen(viewModel: HimViewModel, driver: DriverEntity, bookings: List<BookingEntity>) {
    var payoutSuccessMsg by remember { mutableStateOf<String?>(null) }

    // Check if there is an active booking matched to this driver
    val activeMyTrip = bookings.find {
        it.driverId == driver.id && it.status != "COMPLETED" && it.status != "CANCELLED"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Driver Header Block
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(DriverNavyLight)
                .padding(24.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(driver.name, fontWeight = FontWeight.Black, fontSize = 24.sp, color = Color.White)
                        Text("${driver.vehicleColor} ${driver.vehicleMake} • ${driver.vehicleType}", fontSize = 12.sp, color = Color.LightGray)
                    }

                    // Online Toggler
                    Column(horizontalAlignment = Alignment.End) {
                        Text(if (driver.isOnline) "ONLINE" else "OFFLINE", fontSize = 10.sp, color = if (driver.isOnline) DriverMint else Color.Gray, fontWeight = FontWeight.Black)
                        Spacer(modifier = Modifier.height(4.dp))
                        Switch(
                            checked = driver.isOnline,
                            onCheckedChange = { viewModel.toggleDriverOnlineState(driver.id, it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = DriverMint, checkedTrackColor = DriverMintDark)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Stats Dashboard Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Total Earnings Card
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.08f)),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("EARNINGS TODAY", fontSize = 10.sp, color = Color.LightGray.copy(alpha = 0.7f), fontWeight = FontWeight.Bold)
                            Text("₹${"%.1f".format(bookings.filter { it.driverId == driver.id && it.status == "COMPLETED" }.sumOf { it.fare * 0.85 })}", fontSize = 20.sp, color = Color.White, fontWeight = FontWeight.Black)
                        }
                    }

                    // Total rides count
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.08f)),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("TRIPS DONE", fontSize = 10.sp, color = Color.LightGray.copy(alpha = 0.7f), fontWeight = FontWeight.Bold)
                            Text("${bookings.count { it.driverId == driver.id && it.status == "COMPLETED" }} Rides", fontSize = 20.sp, color = Color.White, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }

        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // Simulated trip progress steps if driver is on active assignment
            activeMyTrip?.let { trip ->
                DriverActiveSimulationJobCard(viewModel, trip)
            }

            // Incentive tracker meter
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DriverNavyLight)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("🎯 Daily Extra Incentives", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("✨ Bonus: ₹200", color = DriverMint, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Complete 5 trips to unlock ₹200 bonus. 1 trip remaining!", fontSize = 11.sp, color = Color.LightGray)
                    Spacer(modifier = Modifier.height(10.dp))
                    LinearProgressIndicator(
                        progress = { 0.8f },
                        modifier = Modifier.fillMaxWidth(),
                        color = DriverMint,
                        trackColor = DriverNavy
                    )
                }
            }

            // Payout control panel
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DriverNavyLight)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Instant Bank Cashout Settlement", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("Verified Bank account: ${driver.bankName} (A/C ****${driver.bankAccount.takeLast(4)})", fontSize = 11.sp, color = Color.LightGray)
                    
                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { payoutSuccessMsg = "Cash transfer of payout settled to bank branch successfully! 💸" },
                        colors = ButtonDefaults.buttonColors(containerColor = DriverMint),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Transfer Earnings to Bank Account", fontWeight = FontWeight.Bold, color = DriverNavy)
                    }

                    payoutSuccessMsg?.let {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(it, color = DriverMint, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Historical assignments
            Text("Driver Historical Logs", color = Color.White, fontWeight = FontWeight.Bold)

            val myHistory = bookings.filter { it.driverId == driver.id }
            if (myHistory.isEmpty()) {
                Text("No rides done yet. Switch online and await request matching logs.", color = Color.LightGray, fontSize = 12.sp)
            } else {
                myHistory.forEach { trip ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = DriverNavyLight)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("${trip.pickupName} ➔ ${trip.dropName}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Passenger ID: #${trip.userId} • Code: ${trip.otp}", color = Color.LightGray, fontSize = 11.sp)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("₹${(trip.fare * 0.85).toInt()}", color = DriverMint, fontWeight = FontWeight.Black, fontSize = 15.sp)
                                Text(trip.status, color = Color.LightGray, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    // Direct Live Booking request Alert Modal Dialog simulation! INCREDIBLE UX!
    viewModel.driverPopupBooking?.let { popupTrip ->
        AlertDialog(
            onDismissRequest = { viewModel.driverPopupBooking = null },
            containerColor = DriverNavyLight,
            tonalElevation = 16.dp,
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🚖 NEW SAWARI REQUEST RECEIVED!", color = Color.White, fontWeight = FontWeight.Black, fontSize = 15.sp)
                    Text("⏰ ${viewModel.driverPopupTimer}s", color = Color.Red, fontWeight = FontWeight.Black, fontSize = 14.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("SERVICE TYPE: ${popupTrip.category.uppercase()}", color = DriverMint, fontWeight = FontWeight.Black, fontSize = 13.sp)
                        Text("EST FARE: ₹${popupTrip.fare.toInt()}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    SimpleHorizontalDivider()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("PICKUP: ${popupTrip.pickupName}", color = Color.White, fontWeight = FontWeight.Medium, fontSize = 12.sp)
                            Text("DROP: ${popupTrip.dropName}", color = Color.LightGray, fontSize = 12.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.driverAcceptTrip(popupTrip.id, driver.id) },
                    colors = ButtonDefaults.buttonColors(containerColor = DriverMint)
                ) {
                    Text("ACCEPT SAWARI", fontWeight = FontWeight.Black, color = DriverNavy)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.driverPopupBooking = null }) {
                    Text("DECLINE", color = Color.LightGray)
                }
            }
        )
    }
}

// Active assignment controller state machine card inside driver panel
@Composable
fun DriverActiveSimulationJobCard(viewModel: HimViewModel, trip: BookingEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DriverNavyLight),
        border = BorderStroke(1.dp, DriverMint)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("⭐ Active Job Route", color = DriverMint, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text(trip.status, color = Color.White, fontWeight = FontWeight.Black, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text("Client Location: From ${trip.pickupName} to ${trip.dropName}", color = Color.White, fontWeight = FontWeight.Medium)
            Text("Passenger Code: #${trip.userId} | Safe OTP needed: ${trip.otp}", fontSize = 12.sp, color = Color.LightGray)

            Spacer(modifier = Modifier.height(16.dp))

            when (trip.status) {
                "MATCHED" -> {
                    Button(
                        onClick = { viewModel.driverAdvanceTripStatus(trip.id) },
                        colors = ButtonDefaults.buttonColors(containerColor = DriverMint),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("START DRIVING TO PICKUP", fontWeight = FontWeight.Bold, color = DriverNavy)
                    }
                }

                "EN_ROUTE" -> {
                    Button(
                        onClick = { viewModel.driverAdvanceTripStatus(trip.id) },
                        colors = ButtonDefaults.buttonColors(containerColor = DriverMint),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("CONFIRM ARRIVAL AT PICKUP POINT", fontWeight = FontWeight.Bold, color = DriverNavy)
                    }
                }

                "ARRIVED" -> {
                    var inputOtpState by remember { mutableStateOf("") }
                    Text("Provide Passengers 4 digit OTP and enter below to begin trip:", fontSize = 11.sp, color = Color.LightGray)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = inputOtpState,
                            onValueChange = { inputOtpState = it },
                            label = { Text("Trip OTP") },
                            modifier = Modifier.weight(1f).testTag("otp_input"),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, focusedLabelColor = DriverMint, focusedBorderColor = DriverMint)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (inputOtpState == trip.otp) {
                                    viewModel.driverAdvanceTripStatus(trip.id)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DriverMint)
                        ) {
                            Text("Verify", color = DriverNavy)
                        }
                    }
                }

                "STARTED" -> {
                    Text("Ride is Active and in progress...", color = Color.White, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))
                    LinearProgressIndicator(
                        progress = { viewModel.rideProgress },
                        modifier = Modifier.fillMaxWidth(),
                        color = DriverMint
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { viewModel.driverAdvanceTripStatus(trip.id) },
                        colors = ButtonDefaults.buttonColors(containerColor = DriverMint),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("ARRIVED AT DESTINATION - COMPLETE!", fontWeight = FontWeight.Bold, color = DriverNavy)
                    }
                }
            }
        }
    }
}
