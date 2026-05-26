package com.gujaratifitness.app.presentation.screens.imbalance

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
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
import com.gujaratifitness.app.presentation.components.AppTextField

object ImbalanceTab : Tab {

    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Default.Warning)
            return remember {
                TabOptions(
                    index = 1u,
                    title = "Imbalance",
                    icon = icon
                )
            }
        }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<ImbalanceScreenModel>()
        val state by screenModel.state.collectAsState()

        var showForm by remember { mutableStateOf(false) }

        // Form fields
        var benchPress by remember { mutableStateOf("") }
        var squat by remember { mutableStateOf("") }
        var deadlift by remember { mutableStateOf("") }
        var ohp by remember { mutableStateOf("") }
        var pullups by remember { mutableStateOf("") }
        var experienceMonths by remember { mutableStateOf("") }

        // Training days per week per muscle group
        var pushDays by remember { mutableStateOf(1.0f) }
        var pullDays by remember { mutableStateOf(1.0f) }
        var legsDays by remember { mutableStateOf(1.0f) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Muscle Imbalance Detector", fontWeight = FontWeight.Bold, color = TextPrimaryColor) },
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
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = PrimaryColor)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Analyzing lift mechanics...", color = TextSecondaryColor, fontSize = 14.sp)
                        }
                    }
                } else if (state.report == null || showForm) {
                    // Show Input Questionnaire Form
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Strength Profile (1RM in kg)",
                            color = TextPrimaryColor,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            AppTextField(
                                value = benchPress,
                                onValueChange = { benchPress = it },
                                label = "Bench Press",
                                placeholder = "e.g., 80",
                                modifier = Modifier.weight(1f)
                            )
                            AppTextField(
                                value = squat,
                                onValueChange = { squat = it },
                                label = "Squat",
                                placeholder = "e.g., 100",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            AppTextField(
                                value = deadlift,
                                onValueChange = { deadlift = it },
                                label = "Deadlift",
                                placeholder = "e.g., 120",
                                modifier = Modifier.weight(1f)
                            )
                            AppTextField(
                                value = ohp,
                                onValueChange = { ohp = it },
                                label = "Overhead Press",
                                placeholder = "e.g., 50",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            AppTextField(
                                value = pullups,
                                onValueChange = { pullups = it },
                                label = "Pull-ups / Rows Max Reps",
                                placeholder = "e.g., 10",
                                modifier = Modifier.weight(1f)
                            )
                            AppTextField(
                                value = experienceMonths,
                                onValueChange = { experienceMonths = it },
                                label = "Training Months",
                                placeholder = "e.g., 12",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        HorizontalDivider(color = SecondaryColor)

                        Text(
                            text = "Weekly Frequency per Pattern",
                            color = TextPrimaryColor,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        // Push slider
                        Column {
                            Text("Push Volume (Chest/Triceps): ${pushDays.toInt()} Days/Week", color = TextSecondaryColor, fontSize = 14.sp)
                            Slider(
                                value = pushDays,
                                onValueChange = { pushDays = it },
                                valueRange = 0f..4f,
                                steps = 3,
                                colors = SliderDefaults.colors(thumbColor = PrimaryColor, activeTrackColor = PrimaryColor)
                            )
                        }

                        // Pull slider
                        Column {
                            Text("Pull Volume (Back/Biceps): ${pullDays.toInt()} Days/Week", color = TextSecondaryColor, fontSize = 14.sp)
                            Slider(
                                value = pullDays,
                                onValueChange = { pullDays = it },
                                valueRange = 0f..4f,
                                steps = 3,
                                colors = SliderDefaults.colors(thumbColor = PrimaryColor, activeTrackColor = PrimaryColor)
                            )
                        }

                        // Legs slider
                        Column {
                            Text("Legs/Core Volume: ${legsDays.toInt()} Days/Week", color = TextSecondaryColor, fontSize = 14.sp)
                            Slider(
                                value = legsDays,
                                onValueChange = { legsDays = it },
                                valueRange = 0f..4f,
                                steps = 3,
                                colors = SliderDefaults.colors(thumbColor = PrimaryColor, activeTrackColor = PrimaryColor)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        AppButton(
                            text = "Analyze Imbalance",
                            onClick = {
                                screenModel.detectImbalance(
                                    benchPress = benchPress.toDoubleOrNull() ?: 0.0,
                                    squat = squat.toDoubleOrNull() ?: 0.0,
                                    deadlift = deadlift.toDoubleOrNull() ?: 0.0,
                                    ohp = ohp.toDoubleOrNull() ?: 0.0,
                                    pullups = pullups.toIntOrNull() ?: 0,
                                    trainingDays = mapOf(
                                        "Push" to pushDays.toInt(),
                                        "Pull" to pullDays.toInt(),
                                        "Legs" to legsDays.toInt()
                                    ),
                                    experienceMonths = experienceMonths.toIntOrNull() ?: 0
                                )
                                showForm = false
                            }
                        )

                        if (state.report != null) {
                            OutlinedButton(
                                onClick = { showForm = false },
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, TextSecondaryColor)
                            ) {
                                Text("Cancel", color = TextPrimaryColor)
                            }
                        }
                    }
                } else {
                    // Show report
                    val report = state.report!!
                    val data = report.report_data

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Overall score display
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = SurfaceColor),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Structural Balance Score",
                                    color = TextSecondaryColor,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                
                                Text(
                                    text = "${data.overall_balance_score}/100",
                                    color = if (data.overall_balance_score >= 80) Color.Green else if (data.overall_balance_score >= 60) PrimaryColor else Color.Red,
                                    fontSize = 48.sp,
                                    fontWeight = FontWeight.Black
                                )

                                Text(
                                    text = if (data.overall_balance_score >= 80) "Optimal structural integrity" else if (data.overall_balance_score >= 60) "Moderate imbalances detected" else "High injury risk - fix priorities",
                                    color = TextPrimaryColor,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        // Strengths
                        if (data.strengths.isNotEmpty()) {
                            Text("Structural Strengths", color = TextPrimaryColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = SurfaceColor)
                            ) {
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    data.strengths.forEach { strength ->
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text("✓", color = Color.Green, fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 8.dp))
                                            Text(strength, color = TextPrimaryColor, fontSize = 14.sp)
                                        }
                                    }
                                }
                            }
                        }

                        // Imbalance Findings
                        Text("Imbalance Analysis", color = TextPrimaryColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        data.imbalances.forEach { item ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = SurfaceColor),
                                border = BorderStroke(
                                    width = 1.dp,
                                    color = if (item.severity.lowercase() == "severe") Color.Red else if (item.severity.lowercase() == "moderate") PrimaryColor else Color.Transparent
                                )
                            ) {
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = item.area,
                                            color = TextPrimaryColor,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                        Badge(
                                            containerColor = if (item.severity.lowercase() == "severe") Color.Red else if (item.severity.lowercase() == "moderate") PrimaryColor else SecondaryColor,
                                            contentColor = Color.White
                                        ) {
                                            Text(item.severity.uppercase(), modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), fontSize = 10.sp)
                                        }
                                    }
                                    Text(text = "Finding: ${item.finding}", color = TextSecondaryColor, fontSize = 13.sp)
                                    HorizontalDivider(color = BackgroundColor)
                                    Text(text = "Corrective Plan:", color = PrimaryColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text(text = item.recommendation, color = TextPrimaryColor, fontSize = 13.sp)
                                }
                            }
                        }

                        // Priority fixes
                        if (data.priority_fixes.isNotEmpty()) {
                            Text("Priority Action Plan", color = TextPrimaryColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = SurfaceColor)
                            ) {
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    data.priority_fixes.forEachIndexed { index, fix ->
                                        Row(verticalAlignment = Alignment.Top) {
                                            Text("${index + 1}.", color = PrimaryColor, fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 8.dp))
                                            Text(fix, color = TextPrimaryColor, fontSize = 14.sp)
                                        }
                                    }
                                }
                            }
                        }

                        // Re-run
                        AppButton(
                            text = "Re-Calculate Profile",
                            onClick = { showForm = true }
                        )
                    }
                }
            }
        }
    }
}
