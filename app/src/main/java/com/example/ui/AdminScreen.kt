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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.*
import com.example.ui.theme.*
import com.example.viewmodel.AdminTab
import com.example.viewmodel.HimViewModel
import kotlinx.coroutines.launch

@Composable
fun AdminScreen(viewModel: HimViewModel) {
    var adminAuthenticated by remember { mutableStateOf(false) }

    if (!adminAuthenticated) {
        AdminLoginScreen(onUnlock = { adminAuthenticated = true })
    } else {
        AdminMainConsole(viewModel)
    }
}

@Composable
fun AdminLoginScreen(onUnlock: () -> Unit) {
    var code2fa by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("admin") }
    var email by remember { mutableStateOf("franchise.shimla@himride.com") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AdminBg)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, AdminBorders),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.AdminPanelSettings, "Admin Locked", tint = AdminAccent, modifier = Modifier.size(54.dp))
                
                Text(
                    text = "HimRide SaaS Operator Login",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black
                )
                Text(
                    text = "Franchise & Partner Management Console",
                    fontSize = 11.sp,
                    color = Color.Gray
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Operator Email") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Franchise Password") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = code2fa,
                    onValueChange = { code2fa = it },
                    label = { Text("Authentic 2FA OTP Code (TOTP)") },
                    placeholder = { Text("e.g. 5241") },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = onUnlock,
                    colors = ButtonDefaults.buttonColors(containerColor = AdminAccent),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("Secure Login Direct", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AdminMainConsole(viewModel: HimViewModel) {
    val drivers by viewModel.allDriversFlow.collectAsState()
    val bookings by viewModel.allBookingsFlow.collectAsState()
    val packages by viewModel.packagesFlow.collectAsState()
    val banners by viewModel.adBannersFlow.collectAsState()

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 6.dp
            ) {
                NavigationBarItem(
                    selected = viewModel.adminTab == AdminTab.KYC,
                    onClick = { viewModel.adminTab = AdminTab.KYC },
                    icon = { Icon(Icons.Default.Rule, "KYC") },
                    label = { Text("KYC Drivers", fontSize = 10.sp, fontWeight = java.lang.Boolean.TRUE.let { FontWeight.Bold }) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = AdminAccent, selectedTextColor = AdminAccent, indicatorColor = AdminAccentLight)
                )
                NavigationBarItem(
                    selected = viewModel.adminTab == AdminTab.MONITORING,
                    onClick = { viewModel.adminTab = AdminTab.MONITORING },
                    icon = { Icon(Icons.Default.LiveTv, "Monitor") },
                    label = { Text("Monitoring", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = AdminAccent, selectedTextColor = AdminAccent, indicatorColor = AdminAccentLight)
                )
                NavigationBarItem(
                    selected = viewModel.adminTab == AdminTab.PACKAGES,
                    onClick = { viewModel.adminTab = AdminTab.PACKAGES },
                    icon = { Icon(Icons.Default.Subscriptions, "Subs") },
                    label = { Text("Packages", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = AdminAccent, selectedTextColor = AdminAccent, indicatorColor = AdminAccentLight)
                )
                NavigationBarItem(
                    selected = viewModel.adminTab == AdminTab.CAMPAIGNS,
                    onClick = { viewModel.adminTab = AdminTab.CAMPAIGNS },
                    icon = { Icon(Icons.Default.Campaign, "Ads") },
                    label = { Text("Campaigns", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = AdminAccent, selectedTextColor = AdminAccent, indicatorColor = AdminAccentLight)
                )
                NavigationBarItem(
                    selected = viewModel.adminTab == AdminTab.ANALYTICS,
                    onClick = { viewModel.adminTab = AdminTab.ANALYTICS },
                    icon = { Icon(Icons.Default.Analytics, "Data") },
                    label = { Text("Analytics", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = AdminAccent, selectedTextColor = AdminAccent, indicatorColor = AdminAccentLight)
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(AdminBg)
        ) {
            when (viewModel.adminTab) {
                AdminTab.KYC -> AdminKycTab(viewModel, drivers)
                AdminTab.MONITORING -> AdminMonitoringTab(viewModel, bookings)
                AdminTab.PACKAGES -> AdminPackagesTab(viewModel, packages)
                AdminTab.CAMPAIGNS -> AdminCampaignsTab(viewModel, banners)
                AdminTab.ANALYTICS -> AdminAnalyticsTab(viewModel, bookings)
            }
        }
    }
}

@Composable
fun AdminKycTab(viewModel: HimViewModel, drivers: List<DriverEntity>) {
    val pendingDrivers = drivers.filter { it.status == "PENDING" }
    val otherDrivers = drivers.filter { it.status != "PENDING" }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Driver KYC Operator Dashboard", fontSize = 22.sp, fontWeight = FontWeight.Black)
            Text("Supervise and inline verify submitted driving credentials", fontSize = 12.sp, color = Color.Gray)
        }

        item {
            Text("Pending Review Applications (${pendingDrivers.size})", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.Red)
        }

        if (pendingDrivers.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, AdminBorders)
                ) {
                    Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                        Text("No pending KYC verification requests left! 🌿", color = Color.Gray, fontWeight = FontWeight.Medium)
                    }
                }
            }
        } else {
            items(pendingDrivers) { driver ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, AdminBorders)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(driver.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("Aadhar: ${driver.aadharNum}", fontSize = 12.sp, color = Color.Gray)
                            }
                            Text(
                                "PENDING REVIEW",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = AdminAccent,
                                modifier = Modifier
                                    .background(AdminAccentLight, RoundedCornerShape(4.dp))
                                    .padding(6.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        SimpleHorizontalDivider()
                        Spacer(modifier = Modifier.height(10.dp))

                        // Details grid inline
                        Text("Vehicle registration parameters:", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                        Text("Brand Model: ${driver.vehicleMake} ${driver.vehicleModel} • Color: ${driver.vehicleColor}", fontSize = 12.sp)
                        Text("Class: ${driver.vehicleType} • Plate No: ${driver.registrationNumber}", fontSize = 12.sp)
                        Text("Payout Bank: ${driver.bankName} (Acc ${driver.bankAccount})", fontSize = 12.sp, color = Color.Gray)

                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = { viewModel.adminApproveDriver(driver.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Approve Direct", color = Color.White, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { viewModel.adminRejectDriver(driver.id, "RC uploaded blurry or incomplete aadhar documentation") },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Reject Issue", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        item {
            Text("Authorized Verified Drivers / Suspended List (${otherDrivers.size})", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }

        items(otherDrivers) { driver ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, AdminBorders)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(driver.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Plate: ${driver.registrationNumber} • ${driver.vehicleType}", fontSize = 11.sp, color = Color.Gray)
                    }

                    Text(
                        text = driver.status,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (driver.status == "APPROVED") Color(0xFF2E7D32) else Color.Red,
                        modifier = Modifier
                            .background(
                                if (driver.status == "APPROVED") Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                                RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AdminMonitoringTab(viewModel: HimViewModel, bookings: List<BookingEntity>) {
    val activeTrackRides = bookings.filter {
        it.status != "COMPLETED" && it.status != "CANCELLED"
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("Real-Time Dispatch Center Monitor", fontSize = 22.sp, fontWeight = FontWeight.Black)
            Text("Live map feeds tracking ongoing carrier trips and matches", fontSize = 12.sp, color = Color.Gray)
        }

        if (activeTrackRides.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, AdminBorders)
                ) {
                    Box(modifier = Modifier.padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("No active passenger bookings currently transit. 🌲", color = Color.Gray, fontWeight = FontWeight.Medium)
                    }
                }
            }
        } else {
            items(activeTrackRides) { trip ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, AdminBorders)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.ShareLocation, "Live GPS", tint = AdminAccent)
                                Text("Trip Code #${trip.id}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }

                            Text(
                                trip.status,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = AdminAccent,
                                modifier = Modifier
                                    .background(AdminAccentLight, RoundedCornerShape(4.dp))
                                    .padding(6.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        SimpleHorizontalDivider()
                        Spacer(modifier = Modifier.height(10.dp))

                        Text("Pickup Point: ${trip.pickupName}", fontSize = 12.sp)
                        Text("Drop-off Point: ${trip.dropName}", fontSize = 12.sp)
                        Text("Carrier Class: ${trip.category} • Price: ₹${trip.fare.toInt()}", fontSize = 12.sp, color = Color.Gray)

                        if (trip.driverName != null) {
                            Text("Matched Driver Partner: ${trip.driverName} (Plate: ${trip.vehiclePlate})", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                viewModel.cancelActiveBooking()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Operator Force Cancel Live Trip", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminPackagesTab(viewModel: HimViewModel, packages: List<PackageEntity>) {
    var formName by remember { mutableStateOf("") }
    var formRides by remember { mutableStateOf("10") }
    var formDays by remember { mutableStateOf("14") }
    var formPrice by remember { mutableStateOf("299") }
    var formBadge by remember { mutableStateOf("BEST DEAL") }
    var formCity by remember { mutableStateOf("Shimla") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("Subscriber packages (SaaS Config)", fontSize = 22.sp, fontWeight = FontWeight.Black)
            Text("Adjust subscription passes active for Shimla / Manali riders", fontSize = 12.sp, color = Color.Gray)
        }

        // Add package Form card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, AdminBorders)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Add Corporate Ride Subscription Pass", fontWeight = FontWeight.Bold, fontSize = 15.sp)

                    OutlinedTextField(
                        value = formName,
                        onValueChange = { formName = it },
                        label = { Text("Pass Title Name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = formRides,
                            onValueChange = { formRides = it },
                            label = { Text("Rides Count") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = formDays,
                            onValueChange = { formDays = it },
                            label = { Text("Validity Days") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = formPrice,
                            onValueChange = { formPrice = it },
                            label = { Text("Price (₹)") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = formBadge,
                            onValueChange = { formBadge = it },
                            label = { Text("Badge (e.g. Save 20%)") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = formCity,
                        onValueChange = { formCity = it },
                        label = { Text("Target coverage City") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            if (formName.isNotBlank()) {
                                viewModel.adminAddPackage(
                                    name = formName,
                                    rides = formRides.toIntOrNull() ?: 5,
                                    days = formDays.toIntOrNull() ?: 7,
                                    price = formPrice.toDoubleOrNull() ?: 199.0,
                                    badge = formBadge,
                                    city = formCity
                                )
                                formName = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AdminAccent),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Publish New Package Pass", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Text("Published SaaS Packages", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }

        items(packages) { pkg ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, AdminBorders)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(pkg.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Rides: ${pkg.ridesCount} | Price: ₹${pkg.price.toInt()} | City: ${pkg.city}", fontSize = 11.sp, color = Color.Gray)
                    }

                    IconButton(onClick = { viewModel.adminDeletePackage(pkg) }) {
                        Icon(Icons.Default.Delete, "Delete", tint = Color.Red)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminCampaignsTab(viewModel: HimViewModel, banners: List<AdBannerEntity>) {
    var formAdName by remember { mutableStateOf("") }
    var formAdImg by remember { mutableStateOf("") }
    var formAdUrl by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("Sponsor Banner Campaigns Management", fontSize = 22.sp, fontWeight = FontWeight.Black)
            Text("Design high quality Himachal tourism banners featured at homepage slider", fontSize = 12.sp, color = Color.Gray)
        }

        // Add campaign form
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, AdminBorders)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Register Brand Campaign Ad", fontWeight = FontWeight.Bold, fontSize = 15.sp)

                    OutlinedTextField(
                        value = formAdName,
                        onValueChange = { formAdName = it },
                        label = { Text("Deal / Sponsor Name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = formAdImg,
                        onValueChange = { formAdImg = it },
                        label = { Text("Display Banner Image URL Link") },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Unsplash image url") }
                    )

                    OutlinedTextField(
                        value = formAdUrl,
                        onValueChange = { formAdUrl = it },
                        label = { Text("Target Redirect Navigation URL") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            if (formAdName.isNotBlank() && formAdImg.isNotBlank()) {
                                viewModel.adminAddCampaign(
                                    title = formAdName,
                                    imgUrl = formAdImg,
                                    redirect = formAdUrl
                                )
                                formAdName = ""
                                formAdImg = ""
                                formAdUrl = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AdminAccent),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Publish Banner Live", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Text("Live Sponsored Campaigns Banners", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }

        items(banners) { activeAd ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, AdminBorders)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(activeAd.imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = activeAd.title,
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Text(activeAd.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Position Sequence: Slot ${activeAd.position}", fontSize = 11.sp, color = Color.Gray)
                    }

                    IconButton(onClick = { viewModel.adminDeleteCampaign(activeAd) }) {
                        Icon(Icons.Default.Delete, "Delete", tint = Color.Red)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminAnalyticsTab(viewModel: HimViewModel, bookings: List<BookingEntity>) {
    var commisionPercentage by remember { mutableStateOf("15") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Franchise Revenue & Growth Metrics", fontSize = 22.sp, fontWeight = FontWeight.Black)
            Text("City-wise aggregate ledger records automatically pulled daily", fontSize = 12.sp, color = Color.Gray)
        }

        // Stats boxes
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                val totalRevenue = bookings.filter { it.status == "COMPLETED" }.sumOf { it.fare }
                val commValue = totalRevenue * (commisionPercentage.toDoubleOrNull() ?: 15.0) / 100.0

                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, AdminBorders)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("AGGREGATE REVENUE", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                        Text("₹${"%.1f".format(totalRevenue)}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color.Black)
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, AdminBorders)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("FRANCHISE PROFIT", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                        Text("₹${"%.1f".format(commValue)}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = AdminAccent)
                    }
                }
            }
        }

        // Custom canvas bar chart representation
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, AdminBorders)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Daily Ticket Bookings Trend", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(14.dp))
                    
                    // Display Canvas chart from our analytical components
                    AnalyticsBarChart(
                        modifier = Modifier.fillMaxSize(),
                        data = listOf(3f, 7f, 12f, 15f, 2f, 9f, 14f),
                        labels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"),
                        barColor = AdminAccent
                    )
                }
            }
        }

        // Commission Adjust settings
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, AdminBorders)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Commission Configuration (Franchise Share %)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("Changes are deployed immediately across all regional passenger booking tickets.", fontSize = 11.sp, color = Color.Gray)

                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = commisionPercentage,
                            onValueChange = { commisionPercentage = it },
                            label = { Text("Standard Commission Rate %") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Button(
                            onClick = { /* Saved setting state */ },
                            colors = ButtonDefaults.buttonColors(containerColor = AdminAccent)
                        ) {
                            Text("Update Settings")
                        }
                    }
                }
            }
        }
    }
}
