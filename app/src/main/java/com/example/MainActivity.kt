package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AdminScreen
import com.example.ui.DriverScreen
import com.example.ui.PassengerScreen
import com.example.ui.theme.*
import com.example.viewmodel.HimViewModel
import com.example.viewmodel.UserRole

class MainActivity : ComponentActivity() {
    private val viewModel: HimViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        RoleSelectionTopBar(viewModel)
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (viewModel.currentRole) {
                            UserRole.PASSENGER -> PassengerScreen(viewModel)
                            UserRole.DRIVER -> DriverScreen(viewModel)
                            UserRole.ADMIN -> AdminScreen(viewModel)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RoleSelectionTopBar(viewModel: HimViewModel) {
    // Elegant M3 floating role switcher to demo Passenger, Driver, and Admin flows with synchronized Room DB state
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = when (viewModel.currentRole) {
            UserRole.PASSENGER -> PassOrangeDark
            UserRole.DRIVER -> DriverNavyLight
            UserRole.ADMIN -> AdminAccent
        },
        tonalElevation = 6.dp
    ) {
        Column(
            modifier = Modifier
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "HimRide Hub Simulator",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )

                // Simple simulated network chip
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(Color(0xFF00E676), RoundedCornerShape(50))
                    )
                    Text(
                        text = "ROOM DB SYNCED",
                        fontWeight = FontWeight.Bold,
                        fontSize = 8.sp,
                        color = Color.White
                    )
                }
            }

            // Elegant, accessible segmented tabs
            TabRow(
                selectedTabIndex = viewModel.currentRole.ordinal,
                containerColor = Color.Transparent,
                contentColor = Color.White,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[viewModel.currentRole.ordinal]),
                        color = when (viewModel.currentRole) {
                            UserRole.PASSENGER -> Color.White
                            UserRole.DRIVER -> DriverMint
                            UserRole.ADMIN -> Color.White
                        }
                    )
                },
                divider = {}
            ) {
                Tab(
                    selected = viewModel.currentRole == UserRole.PASSENGER,
                    onClick = { viewModel.currentRole = UserRole.PASSENGER },
                    modifier = Modifier.testTag("passenger_role_tab")
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.Person, "Passenger", modifier = Modifier.size(16.dp))
                        Text("Rider Client", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Tab(
                    selected = viewModel.currentRole == UserRole.DRIVER,
                    onClick = { viewModel.currentRole = UserRole.DRIVER },
                    modifier = Modifier.testTag("driver_role_tab")
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.DirectionsCar, "Driver", modifier = Modifier.size(16.dp))
                        Text("Driver KYC", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Tab(
                    selected = viewModel.currentRole == UserRole.ADMIN,
                    onClick = { viewModel.currentRole = UserRole.ADMIN },
                    modifier = Modifier.testTag("admin_role_tab")
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.AdminPanelSettings, "Admin", modifier = Modifier.size(16.dp))
                        Text("SaaS Admin", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
