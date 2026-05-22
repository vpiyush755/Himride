package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.*
import com.example.ui.theme.*
import com.example.viewmodel.HimViewModel
import com.example.viewmodel.PassengerTab
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.geometry.Offset
import kotlinx.coroutines.delay

@Composable
fun PassengerScreen(viewModel: HimViewModel) {
    val activeBooking by viewModel.activeBookingFlow.collectAsState()
    val userSession by viewModel.userFlow.collectAsState()

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = viewModel.passengerTab == PassengerTab.HOME,
                    onClick = { viewModel.passengerTab = PassengerTab.HOME },
                    icon = { Icon(Icons.Default.Home, "Home") },
                    label = { Text("Home", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PassOrange,
                        selectedTextColor = PassOrange,
                        indicatorColor = PassOrangeLight
                    )
                )
                NavigationBarItem(
                    selected = viewModel.passengerTab == PassengerTab.RIDES,
                    onClick = { viewModel.passengerTab = PassengerTab.RIDES },
                    icon = { Icon(Icons.Default.DirectionsCar, "Rides") },
                    label = { Text("My Rides", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PassOrange,
                        selectedTextColor = PassOrange,
                        indicatorColor = PassOrangeLight
                    )
                )
                NavigationBarItem(
                    selected = viewModel.passengerTab == PassengerTab.PACKAGES,
                    onClick = { viewModel.passengerTab = PassengerTab.PACKAGES },
                    icon = { Icon(Icons.Default.CardMembership, "Packages") },
                    label = { Text("Packages", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PassOrange,
                        selectedTextColor = PassOrange,
                        indicatorColor = PassOrangeLight
                    )
                )
                NavigationBarItem(
                    selected = viewModel.passengerTab == PassengerTab.WALLET,
                    onClick = { viewModel.passengerTab = PassengerTab.WALLET },
                    icon = { Icon(Icons.Default.AccountBalanceWallet, "Wallet") },
                    label = { Text("Wallet", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PassOrange,
                        selectedTextColor = PassOrange,
                        indicatorColor = PassOrangeLight
                    )
                )
                NavigationBarItem(
                    selected = viewModel.passengerTab == PassengerTab.PROFILE,
                    onClick = { viewModel.passengerTab = PassengerTab.PROFILE },
                    icon = { Icon(Icons.Default.Person, "Profile") },
                    label = { Text("Profile", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PassOrange,
                        selectedTextColor = PassOrange,
                        indicatorColor = PassOrangeLight
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (viewModel.passengerTab) {
                PassengerTab.HOME -> PassengerHomeTab(viewModel, activeBooking)
                PassengerTab.RIDES -> PassengerRidesTab(viewModel)
                PassengerTab.PACKAGES -> PassengerPackagesTab(viewModel, userSession)
                PassengerTab.WALLET -> PassengerWalletTab(viewModel, userSession)
                PassengerTab.PROFILE -> PassengerProfileTab(viewModel, userSession)
            }

            // Booking overlay for live simulation during matching / ongoing rides
            activeBooking?.let { booking ->
                if (booking.status != "COMPLETED" && booking.status != "CANCELLED") {
                    PassengerBookingSimulationOverlay(viewModel, booking)
                }
            }
        }
    }
}

@Composable
fun PassengerHomeTab(viewModel: HimViewModel, activeBooking: BookingEntity?) {
    val banners by viewModel.adBannersFlow.collectAsState()
    val packages by viewModel.packagesFlow.collectAsState()
    val scrollState = rememberScrollState()

    // Auto-scroll ad banners trick
    var currentAdIndex by remember { mutableStateOf(0) }
    LaunchedEffect(banners) {
        if (banners.isNotEmpty()) {
            while (true) {
                delay(4000)
                currentAdIndex = (currentAdIndex + 1) % banners.size
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(
                Brush.verticalGradient(
                    colors = listOf(PassBgGradStart, PassBgGradEnd)
                )
            )
    ) {
        // Hero Header Segment with gorgeous Unsplash scenery & glass overlay
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("https://images.unsplash.com/photo-1626621341517-bbf3d9990a23?auto=format&fit=crop&q=80&w=600")
                    .crossfade(true)
                    .build(),
                contentDescription = "Scenic Himachal Mountains",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Dynamic gradient dimming layer (matching linear-gradient in design)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Black.copy(alpha = 0.2f), Color.Black.copy(alpha = 0.65f))
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "HimRide",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            letterSpacing = (-1).sp
                        )
                        Text(
                            text = "HIMACHAL KI APNI SAWARI 🏔️",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.9f),
                            letterSpacing = 1.5.sp
                        )
                    }

                    SocialSosTriggerButton()
                }

                // Frosted Glass "Current Location" card matching the design exactly!
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White.copy(alpha = 0.20f), RoundedCornerShape(16.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(Color(0xFF4ADE80), CircleShape) // Vibrant green-400
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "CURRENT LOCATION",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.75f),
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Mall Road, Shimla",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Quick Booking Panel Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .offset(y = (-30).dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(modifier = Modifier.padding(22.dp)) {
                Text(
                    text = "Request a Sawari",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Pickup input
                OutlinedTextField(
                    value = viewModel.fromLocation,
                    onValueChange = { viewModel.fromLocation = it },
                    label = { Text("From Pickup Location") },
                    leadingIcon = { Icon(Icons.Default.LocationOn, "From", tint = PassOrange) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pickup_field"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PassOrange)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Drop input
                OutlinedTextField(
                    value = viewModel.toLocation,
                    onValueChange = { viewModel.toLocation = it },
                    label = { Text("To Destination") },
                    leadingIcon = { Icon(Icons.Default.LocationOn, "To", tint = Color.Red) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("destination_field"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PassOrange)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Categories (Bike, Auto, Cab, Bus)
                Text(
                    text = "Service Type",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val categories = listOf(
                        Triple("Bike", Icons.AutoMirrored.Filled.DirectionsBike, Color(0xFFFFF2EC) to Color(0xFFFFDFD0)),
                        Triple("Auto", Icons.Default.Directions, Color(0xFFEFF6FF) to Color(0xFFDBEAFE)),
                        Triple("Cab", Icons.Default.DirectionsCar, Color(0xFFEEF2FF) to Color(0xFFE0E7FF)),
                        Triple("Bus", Icons.Default.DirectionsCar, Color(0xFFECFDF5) to Color(0xFFD1FAE5))
                    )
                    categories.forEach { (cat, icon, colorsPair) ->
                        val selected = viewModel.selectedCategory == cat
                        val bg = if (selected) PassOrange else colorsPair.first
                        val borderCol = if (selected) PassOrangeDark else colorsPair.second
                        val contentCol = if (selected) Color.White else Color(0xFF334155) // Slate-700
                        
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(58.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(bg)
                                .border(1.dp, borderCol, RoundedCornerShape(16.dp))
                                .clickable { viewModel.selectedCategory = cat }
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(icon, contentDescription = cat, tint = contentCol, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(cat, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = contentCol)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Coupon field
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = viewModel.promoCodeInput,
                        onValueChange = { viewModel.promoCodeInput = it },
                        label = { Text("Have a coupon?") },
                        placeholder = { Text("e.g. HIMCOUPON") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PassOrange)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { viewModel.applyPromoCode() },
                        colors = ButtonDefaults.buttonColors(containerColor = PassOrangeDark)
                    ) {
                        Text("Apply")
                    }
                }

                if (viewModel.bookingPromoMessage.isNotEmpty()) {
                    Text(
                        text = viewModel.bookingPromoMessage,
                        fontSize = 12.sp,
                        color = if (viewModel.currentPromoDiscount > 0) Color(0xFF2E7D32) else Color.Red,
                        modifier = Modifier.padding(top = 4.dp),
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                val base = viewModel.getBaseFare(viewModel.fromLocation, viewModel.toLocation, viewModel.selectedCategory)
                val finalFare = (base - viewModel.currentPromoDiscount).coerceAtLeast(15.0)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Estimated Fare", fontSize = 12.sp, color = Color.Gray)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (viewModel.currentPromoDiscount > 0) {
                                Text(
                                    text = "₹${base.toInt()} ",
                                    fontSize = 14.sp,
                                    color = Color.LightGray,
                                    textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                                )
                            }
                            Text("₹${finalFare.toInt()}", fontSize = 24.sp, fontWeight = FontWeight.Black, color = Color.Black)
                        }
                    }

                    Button(
                        onClick = { viewModel.requestBooking() },
                        modifier = Modifier
                            .height(50.dp)
                            .testTag("book_now_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = PassOrange),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.CheckCircle, "Book")
                            Text("Book Sawari Now", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
        }

        // Horizontal Auto-Scrolling Ad Banners Carousel
        if (banners.isNotEmpty()) {
            Text(
                text = "Partner Deals & Tourism",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .padding(horizontal = 16.dp)
            ) {
                val activeAd = banners[currentAdIndex]
                Card(
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxSize(),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)), // Deep Slate 900
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(activeAd.imageUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = activeAd.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            alpha = 0.45f
                        )
                        
                        // Right-bottom glowing neon orange bubble behind the card content for that modern atmospheric glow
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .offset(x = 25.dp, y = 25.dp)
                                .size(90.dp)
                                .background(PassOrange.copy(alpha = 0.3f), CircleShape)
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1.3f),
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "OFFER",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White,
                                    modifier = Modifier
                                        .background(PassOrange, RoundedCornerShape(100.dp))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = activeAd.title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    lineHeight = 18.sp
                                )
                            }

                            // Clean glass button on the right matching HTML
                            Box(
                                modifier = Modifier
                                    .background(Color.White.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                                    .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "APPLY CODE",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Popular Himachal Routes Fast Fill Section
        Text(
            text = "Popular Himachal Scenic Routes",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        )
        Text(
            text = "Click any route to instantly fill coordinates and pricing",
            fontSize = 11.sp,
            color = Color.Gray,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 10.dp)
        )

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val routes = listOf(
                "Shimla" to "Manali",
                "Shimla" to "Chandigarh",
                "Manali" to "Delhi",
                "Dharamshala" to "Shimla"
            )
            items(routes) { (from, to) ->
                Card(
                    modifier = Modifier
                        .width(180.dp)
                        .clickable {
                            viewModel.fromLocation = from
                            viewModel.toLocation = to
                        },
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Explore, "Route", tint = PassOrange, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Fast Booking Route", fontSize = 11.sp, color = PassOrange, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("$from ➔", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text(to, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Est ₹${viewModel.getBaseFare(from, to, "Cab").toInt()} (Cab)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Gray
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Become a Driver CTA banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DriverNavy),
            onClick = { viewModel.currentRole = com.example.viewmodel.UserRole.DRIVER }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Want to Earn Partner Income? 💰",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Become a certified HimRide driver with your Bike, Auto, or Cab. Quick digital KYC.",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
                Icon(
                    Icons.Default.ArrowForward,
                    "Register",
                    tint = DriverMint,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

// SOS Trigger Modal Overlay action
@Composable
fun SocialSosTriggerButton() {
    var timerActive by remember { mutableStateOf(false) }

    Box {
        IconButton(
            onClick = { timerActive = true },
            modifier = Modifier
                .background(Color.Red, CircleShape)
                .size(44.dp)
        ) {
            Icon(Icons.Default.Warning, "SOS Alert", tint = Color.White)
        }

        if (timerActive) {
            AlertDialog(
                onDismissRequest = { timerActive = false },
                title = { Text("🚨 SOS Emergency Alert Called") },
                text = { Text("Your exact GPS location is being broadcasted to saved local emergency contacts and the Shimla Police helpline department.") },
                confirmButton = {
                    TextButton(onClick = { timerActive = false }) {
                        Text("Dismiss Safe", color = Color.Red, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    }
}

@Composable
fun PassengerRidesTab(viewModel: HimViewModel) {
    val bookings by viewModel.allBookingsFlow.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(16.dp)
    ) {
        Text("Your Booking Journeys", fontSize = 22.sp, fontWeight = FontWeight.Black)
        Text("History of rides, cancellations, and packages purchased", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 16.dp))

        if (bookings.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.DirectionsCar, "None", tint = Color.LightGray, modifier = Modifier.size(64.dp))
                    Text("No bookings made yet", fontWeight = FontWeight.Bold, color = Color.Gray)
                    Text("Request your first ride from the Home screen", fontSize = 12.sp, color = Color.LightGray)
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(bookings) { booking ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    val icon = when (booking.category) {
                                        "Bike" -> Icons.Default.TwoWheeler
                                        "Auto" -> Icons.Default.ElectricRickshaw
                                        "Bus" -> Icons.Default.DirectionsBus
                                        else -> Icons.Default.LocalTaxi
                                    }
                                    Icon(icon, booking.category, tint = PassOrange, modifier = Modifier.size(20.dp))
                                    Text(booking.category, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }

                                Text(
                                    text = booking.status,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = when (booking.status) {
                                        "COMPLETED" -> Color(0xFF2E7D32)
                                        "CANCELLED" -> Color.Red
                                        "SEARCHING" -> PassOrange
                                        else -> Color(0xFF1976D2)
                                    },
                                    modifier = Modifier
                                        .background(
                                            when (booking.status) {
                                                "COMPLETED" -> Color(0xFFE8F5E9)
                                                "CANCELLED" -> Color(0xFFFFEBEE)
                                                "SEARCHING" -> PassOrangeLight
                                                else -> Color(0xFFE3F2FD)
                                            },
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(modifier = Modifier.size(8.dp).background(Color.Green, CircleShape))
                                    Box(modifier = Modifier.width(1.dp).height(24.dp).background(Color.Gray))
                                    Box(modifier = Modifier.size(8.dp).background(Color.Red, CircleShape))
                                }

                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(booking.pickupName, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                    Text(booking.dropName, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            SimpleHorizontalDivider()
                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Fare Paid", fontSize = 11.sp, color = Color.Gray)
                                    Text("₹${booking.fare.toInt()}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                }

                                if (booking.driverName != null) {
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("Driver: ${booking.driverName}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text("Vehicle: ${booking.vehiclePlate}", fontSize = 11.sp, color = Color.Gray)
                                    }
                                }
                            }

                            // Rating if complete
                            if (booking.status == "COMPLETED") {
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("My Rating", fontSize = 11.sp, color = Color.Gray)
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        for (i in 1..5) {
                                            val active = booking.rating >= i
                                            Icon(
                                                Icons.Default.Star,
                                                "Star",
                                                tint = if (active) Color(0xFFFFB300) else Color.LightGray,
                                                modifier = Modifier
                                                    .size(18.dp)
                                                    .clickable {
                                                        viewModel.submitRideRating(booking.id, i)
                                                    }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PassengerPackagesTab(viewModel: HimViewModel, user: UserEntity?) {
    val packages by viewModel.packagesFlow.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFBFBFD))
            .padding(16.dp)
    ) {
        Text("Scenic Subscription Packages", fontSize = 22.sp, fontWeight = FontWeight.Black)
        Text("Save up to 45% on regular tourist trails and routes in Himachal", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 16.dp))

        user?.activeSubscriptionName?.let { activeSubName ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = PassOrangeLight),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(Icons.Default.CardMembership, "Subscribed", tint = PassOrangeDark)
                    Column {
                        Text("Active Package Saved", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PassOrangeDark)
                        Text(activeSubName, fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color.Black)
                        Text("All rides during active package are free. Charges will not be deducted.", fontSize = 11.sp, color = Color.DarkGray)
                    }
                }
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(items = packages) { pkg ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = pkg.discountBadge.uppercase(),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                modifier = Modifier
                                    .background(PassOrange, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )

                            Text(
                                text = pkg.city,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(pkg.name, fontSize = 18.sp, fontWeight = FontWeight.Black)
                        Text("Includes: ${pkg.ridesCount} Rides | Validity: ${pkg.validityDays} Days", fontSize = 12.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Price Tag", fontSize = 11.sp, color = Color.Gray)
                                Text("₹${pkg.price.toInt()}", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            }

                            Button(
                                onClick = { viewModel.purchasePackage(pkg) },
                                colors = ButtonDefaults.buttonColors(containerColor = PassOrange),
                                shape = RoundedCornerShape(10.dp),
                                enabled = (user != null && user.walletBalance >= pkg.price)
                            ) {
                                Text("Purchase Pass", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PassengerWalletTab(viewModel: HimViewModel, user: UserEntity?) {
    val txns by viewModel.transactionsFlow.collectAsState()
    var inputAmount by remember { mutableStateOf("500") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(14.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Available Balance", fontSize = 13.sp, color = Color.Gray)
                Text(
                    text = "₹${"%.2f".format(user?.walletBalance ?: 0.0)}",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(14.dp))
                SimpleHorizontalDivider()
                Spacer(modifier = Modifier.height(14.dp))

                Text("Add Money via UPI / Cards (Razorpay Simulated Flow)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = inputAmount,
                        onValueChange = { inputAmount = it },
                        label = { Text("Amount (₹)") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                        )
                    )

                    Button(
                        onClick = {
                            val amt = inputAmount.toDoubleOrNull() ?: 100.0
                            viewModel.addWalletMoney(amt)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PassOrange),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.height(56.dp)
                    ) {
                        Text("Add", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text("Statement Ledger", fontSize = 15.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 10.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
            items(txns) { txn ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(txn.title, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = if (txn.type == "CREDIT") "Added to wallet" else "Charged",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }

                    Text(
                        text = "${if (txn.type == "CREDIT") "+" else "-"} ₹${txn.amount.toInt()}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = if (txn.type == "CREDIT") Color(0xFF2E7D32) else Color.Red
                    )
                }
            }
        }
    }
}

@Composable
fun PassengerProfileTab(viewModel: HimViewModel, user: UserEntity?) {
    var editName by remember { mutableStateOf(user?.name ?: "Piyush") }
    var editPhone by remember { mutableStateOf(user?.phone ?: "+91 98765 43210") }
    var editEmergency by remember { mutableStateOf(user?.emergencyContact ?: "+91 91111 22222") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(PassOrangeLight, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, "Avatar", tint = PassOrange, modifier = Modifier.size(44.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(editName, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("Passenger Member Status", fontSize = 12.sp, color = Color.Gray)
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Edit Rider Profile Information", fontWeight = FontWeight.Bold, fontSize = 15.sp)

                OutlinedTextField(
                    value = editName,
                    onValueChange = { editName = it },
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = editPhone,
                    onValueChange = { editPhone = it },
                    label = { Text("Phone Number") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = editEmergency,
                    onValueChange = { editEmergency = it },
                    label = { Text("Emergency Contact (SOS Alert)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        viewModel.saveUserProfile(editName, editPhone, editEmergency)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PassOrange),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Altered Credentials", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// Full matching booking loop interactive overlay
@Composable
fun PassengerBookingSimulationOverlay(viewModel: HimViewModel, booking: BookingEntity) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Your Ride Booking Progress",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp
                    )

                    Button(
                        onClick = { viewModel.cancelActiveBooking() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.1f), contentColor = Color.Red),
                        modifier = Modifier.height(36.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        Text("Cancel Ride", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Custom Step indicator UI matches Rapido and is detailed
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val states = listOf("SEARCHING", "MATCHED", "EN_ROUTE", "ARRIVED", "STARTED")
                    val currentStepIdx = states.indexOf(booking.status).coerceAtLeast(0)

                    states.forEachIndexed { idx, state ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .background(
                                        if (currentStepIdx >= idx) PassOrange else Color.LightGray.copy(alpha = 0.5f),
                                        CircleShape
                                    )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = state.replace("_", "\n"),
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (currentStepIdx >= idx) PassOrange else Color.Gray,
                                textAlign = TextAlign.Center,
                                lineHeight = 10.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Status descriptive UI and custom graphics
                when (booking.status) {
                    "SEARCHING" -> {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            CircularProgressIndicator(color = PassOrange)
                            Text("Broadcasting trip coordinates safely...", fontWeight = FontWeight.Bold)
                            Text("Awaiting response from nearest certified drivers around ${booking.pickupName}", fontSize = 12.sp, color = Color.Gray, textAlign = TextAlign.Center)
                        }
                    }

                    "MATCHED", "EN_ROUTE", "ARRIVED" -> {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF3F4F6), RoundedCornerShape(12.dp))
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .background(Color.White, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.AccountCircle, "Driver", tint = PassOrange, modifier = Modifier.size(40.dp))
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(booking.driverName ?: "Manoj Sen", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("Vehicle No: ${booking.vehiclePlate ?: "HP-03-B-5012"}", fontSize = 12.sp, color = Color.DarkGray)
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(Icons.Default.Star, "Rating", tint = Color(0xFFFFB300), modifier = Modifier.size(14.dp))
                                    Text("4.9 ⭐ Standard Driver", fontSize = 11.sp, color = Color.Gray)
                                }
                            }

                            // Show SMS verification OTP
                            Column(
                                horizontalAlignment = Alignment.End,
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text("SECURE OTP", fontSize = 10.sp, color = Color.Gray)
                                Text(
                                    text = booking.otp,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = PassOrange
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (booking.status == "ARRIVED") {
                            Text(
                                "Your ride is waiting at ${booking.pickupName}. Please tell the driver your Secure OTP to start.",
                                fontSize = 12.sp,
                                color = Color.DarkGray,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .background(PassBgGradStart, RoundedCornerShape(8.dp))
                                    .padding(10.dp)
                            )
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            var otpInputState by remember { mutableStateOf("") }
                            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                OutlinedTextField(
                                    value = otpInputState,
                                    onValueChange = { otpInputState = it },
                                    label = { Text("Enter OTP to match") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = { viewModel.verifyOtpAndStartRide(otpInputState) },
                                    colors = ButtonDefaults.buttonColors(containerColor = PassOrange)
                                ) {
                                    Text("Verify & Start")
                                }
                            }
                        } else {
                            Text(
                                text = "Driver is en route to pick you up in approx 3 minutes.",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    "STARTED" -> {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text("Simulated Live Map Journey Tracker", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(8.dp))

                            // Interactive route progression using canvas drawings! AMAZING!
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp)
                                    .background(Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
                            ) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val w = size.width
                                    val h = size.height

                                    // Draw simulated route curves
                                    val path = Path().apply {
                                        moveTo(50f, h / 2)
                                        quadraticTo(w * 0.35f, h * 0.2f, w * 0.5f, h / 2)
                                        quadraticTo(w * 0.7f, h * 0.8f, w - 50f, h / 2)
                                    }
                                    drawPath(
                                        path = path,
                                        color = Color.LightGray,
                                        style = Stroke(width = 8f, cap = StrokeCap.Round)
                                    )

                                    // Draw completed path curve
                                    drawPath(
                                        path = path,
                                        color = PassOrange,
                                        style = Stroke(width = 8f, cap = StrokeCap.Round)
                                    )

                                    // Draw starting city bubble
                                    drawCircle(color = Color.Green, radius = 10f, center = Offset(50f, h / 2))
                                    // Draw destination city bubble
                                    drawCircle(color = Color.Red, radius = 10f, center = Offset(w - 50f, h / 2))

                                    // Draw vehicle moving bubble
                                    val t = viewModel.rideProgress
                                    val valStartX = 50f
                                    val valEndX = w - 50f
                                    val currentX = valStartX + (valEndX - valStartX) * t
                                    // Simulated simple wave offset height
                                    val currentY = if (t < 0.5f) {
                                        h / 2 - (h * 0.3f) * (t / 0.5f)
                                    } else {
                                        h / 2 + (h * 0.3f) * ((t - 0.5f) / 0.5f)
                                    }
                                    drawCircle(color = PassOrange, radius = 14f, center = Offset(currentX, currentY))
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            LinearProgressIndicator(
                                progress = { viewModel.rideProgress },
                                modifier = Modifier.fillMaxWidth(),
                                color = PassOrange
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Going to ${booking.dropName}. Progress: ${(viewModel.rideProgress * 100).toInt()}%",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = PassOrangeDark
                            )
                        }
                    }
                }
            }
        }
    }
}
