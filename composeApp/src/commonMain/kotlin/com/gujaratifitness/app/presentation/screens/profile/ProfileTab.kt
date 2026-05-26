package com.gujaratifitness.app.presentation.screens.profile

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.gujaratifitness.app.core.utils.*
import com.gujaratifitness.app.presentation.components.AppButton

object ProfileTab : Tab {

    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Default.Person)
            return remember {
                TabOptions(
                    index = 4u,
                    title = "Profile",
                    icon = icon
                )
            }
        }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<ProfileScreenModel>()
        val state by screenModel.state.collectAsState()

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Profile & Settings", fontWeight = FontWeight.Bold, color = TextPrimaryColor) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundColor)
                )
            },
            containerColor = BackgroundColor
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(BackgroundColor)
            ) {
                if (state.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = PrimaryColor)
                    }
                } else if (state.profile == null) {
                    Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text("No profile data found", color = TextSecondaryColor, textAlign = TextAlign.Center)
                            AppButton(text = "Try Reload", onClick = { screenModel.loadProfile() })
                        }
                    }
                } else {
                    val profile = state.profile!!
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Profile Banner Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = SurfaceColor),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = profile.full_name ?: "Gujarati Fitness Enthusiast",
                                    color = TextPrimaryColor,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = profile.email,
                                    color = TextSecondaryColor,
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Center
                                )
                                Badge(
                                    containerColor = if (profile.user_type == "influencer") PrimaryColor else if (profile.user_type == "premium") AccentColor else SecondaryColor,
                                    contentColor = Color.White
                                ) {
                                    Text(
                                        text = profile.user_type.uppercase(),
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // App Details / Stats
                        Text("Fitness Profile Details", color = TextPrimaryColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = SurfaceColor)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                    Text("Fitness Level", color = TextSecondaryColor)
                                    Text(profile.fitness_level?.uppercase() ?: "NOT SET", color = TextPrimaryColor, fontWeight = FontWeight.Bold)
                                }
                                HorizontalDivider(color = BackgroundColor)
                                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                    Text("Generation Limit", color = TextSecondaryColor)
                                    Text(
                                        text = if (profile.user_type == "free") "3 generations / mo" else "20 generations / mo",
                                        color = TextPrimaryColor,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                HorizontalDivider(color = BackgroundColor)
                                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                    Text("Exclusive S-Tier Exercises", color = TextSecondaryColor)
                                    Text(
                                        text = if (profile.user_type == "free") "LOCKED" else "UNLOCKED",
                                        color = if (profile.user_type == "free") PrimaryColor else Color.Green,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // Influencer Panel
                        if (profile.user_type == "influencer") {
                            Text("Influencer Administration", color = TextPrimaryColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            
                            if (state.joinRequests.isEmpty()) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = SurfaceColor)
                                ) {
                                    Text(
                                        text = "No pending client join requests.",
                                        color = TextSecondaryColor,
                                        modifier = Modifier.padding(16.dp),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            } else {
                                state.joinRequests.forEach { req ->
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = SurfaceColor)
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                                Column {
                                                    Text(req.user_name ?: "Client ID", color = TextPrimaryColor, fontWeight = FontWeight.Bold)
                                                    Text(req.user_email ?: "email address", color = TextSecondaryColor, fontSize = 12.sp)
                                                }
                                                Badge(containerColor = SecondaryColor, contentColor = PrimaryColor) {
                                                    Text(req.status.uppercase(), modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), fontSize = 10.sp)
                                                }
                                            }
                                            
                                            if (req.status == "pending") {
                                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                                    Button(
                                                        onClick = { screenModel.respondToJoinRequest(req.id, true) },
                                                        modifier = Modifier.weight(1f),
                                                        colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
                                                        shape = RoundedCornerShape(8.dp)
                                                    ) {
                                                        Text("Approve", color = Color.Black, fontWeight = FontWeight.Bold)
                                                    }
                                                    Button(
                                                        onClick = { screenModel.respondToJoinRequest(req.id, false) },
                                                        modifier = Modifier.weight(1f),
                                                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                                        shape = RoundedCornerShape(8.dp)
                                                    ) {
                                                        Text("Reject", color = Color.White, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Premium Mock Toggle Settings
                        Text("Mock Subscription Settings", color = TextPrimaryColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = SurfaceColor)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(
                                    "Toggle premium simulation status below to verify that generation limit gates, warning banners, and exclusive exercise lists switch dynamically.",
                                    color = TextSecondaryColor,
                                    fontSize = 13.sp
                                )
                                OutlinedButton(
                                    onClick = { screenModel.togglePremium(profile) },
                                    modifier = Modifier.fillMaxWidth().height(50.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, PrimaryColor),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryColor)
                                ) {
                                    Text(
                                        text = if (profile.user_type == "free") "Simulate Premium Status" else "Downgrade to Free Status",
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Logout
                        AppButton(
                            text = "Sign Out",
                            onClick = { screenModel.signOut() }
                        )
                    }
                }
            }
        }
    }
}
