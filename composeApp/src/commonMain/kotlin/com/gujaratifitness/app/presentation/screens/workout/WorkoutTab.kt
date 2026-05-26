package com.gujaratifitness.app.presentation.screens.workout

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
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

object WorkoutTab : Tab {

    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Default.Home)
            return remember {
                TabOptions(
                    index = 2u,
                    title = "Workout",
                    icon = icon
                )
            }
        }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<WorkoutScreenModel>()
        val state by screenModel.state.collectAsState()

        var showForm by remember { mutableStateOf(false) }

        // Form Fields
        var goal by remember { mutableStateOf("Muscle Gain") }
        val goals = listOf("Muscle Gain", "Fat Loss", "Strength", "Endurance")
        
        var daysPerWeek by remember { mutableStateOf(4.0f) }
        var sessionDuration by remember { mutableStateOf("") }
        var injuries by remember { mutableStateOf("") }
        var includeCorrectives by remember { mutableStateOf(true) }

        // Equipment list
        val equipments = listOf("Barbell", "Dumbbells", "Cables", "Machines", "Bands", "Bodyweight")
        val selectedEquipment = remember { mutableStateListOf("Barbell", "Dumbbells", "Bodyweight") }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("AI Workout Planner", fontWeight = FontWeight.Bold, color = TextPrimaryColor) },
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
                            Text("Gemini is constructing your 8-week routine...", color = TextSecondaryColor, fontSize = 14.sp)
                        }
                    }
                } else if (state.activePlan == null || showForm) {
                    // Show Questionnaire Form
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Workout Preferences",
                            color = TextPrimaryColor,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        // Error Card
                        if (state.error != null) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = state.error!!,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.padding(12.dp),
                                    fontSize = 14.sp
                                )
                            }
                        }

                        // Goal Select
                        Text("Fitness Goal", color = TextSecondaryColor, fontSize = 14.sp)
                        Row(
                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            goals.forEach { g ->
                                FilterChip(
                                    selected = goal == g,
                                    onClick = { goal = g },
                                    label = { Text(g) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        containerColor = SurfaceColor,
                                        labelColor = TextSecondaryColor,
                                        selectedContainerColor = PrimaryContainerColor,
                                        selectedLabelColor = PrimaryColor
                                    )
                                )
                            }
                        }

                        // Days slider
                        Column {
                            Text("Training Frequency: ${daysPerWeek.toInt()} Days/Week", color = TextSecondaryColor, fontSize = 14.sp)
                            Slider(
                                value = daysPerWeek,
                                onValueChange = { daysPerWeek = it },
                                valueRange = 2f..6f,
                                steps = 3,
                                colors = SliderDefaults.colors(thumbColor = PrimaryColor, activeTrackColor = PrimaryColor)
                            )
                        }

                        AppTextField(
                            value = sessionDuration,
                            onValueChange = { sessionDuration = it },
                            label = "Session Duration (minutes)",
                            placeholder = "e.g., 60"
                        )

                        AppTextField(
                            value = injuries,
                            onValueChange = { injuries = it },
                            label = "Injuries or Physical Limitations",
                            placeholder = "e.g., Lower back pain, shoulder stiffness"
                        )

                        HorizontalDivider(color = SecondaryColor)

                        // Equipment Checklist
                        Text("Available Equipment", color = TextPrimaryColor, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            equipments.forEach { eq ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            if (selectedEquipment.contains(eq)) {
                                                selectedEquipment.remove(eq)
                                            } else {
                                                selectedEquipment.add(eq)
                                            }
                                        }
                                        .padding(vertical = 4.dp)
                                ) {
                                    Checkbox(
                                        checked = selectedEquipment.contains(eq),
                                        onCheckedChange = { checked ->
                                            if (checked == true) {
                                                selectedEquipment.add(eq)
                                            } else {
                                                selectedEquipment.remove(eq)
                                            }
                                        },
                                        colors = CheckboxDefaults.colors(checkedColor = PrimaryColor)
                                    )
                                    Text(eq, color = TextPrimaryColor, fontSize = 14.sp)
                                }
                            }
                        }

                        // Imbalance correctives checkbox
                        if (state.latestImbalance != null) {
                            HorizontalDivider(color = SecondaryColor)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { includeCorrectives = !includeCorrectives }
                                    .padding(vertical = 4.dp)
                            ) {
                                Checkbox(
                                    checked = includeCorrectives,
                                    onCheckedChange = { includeCorrectives = it ?: false },
                                    colors = CheckboxDefaults.colors(checkedColor = PrimaryColor)
                                )
                                Column(modifier = Modifier.padding(start = 4.dp)) {
                                    Text("Integrate Muscle Imbalance Correctives", color = TextPrimaryColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Text(
                                        "Uses Gemini to auto-insert target exercises for lagging groups based on your score (${state.latestImbalance!!.report_data.overall_balance_score}/100)",
                                        color = TextSecondaryColor,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        AppButton(
                            text = "Generate 8-Week Program",
                            onClick = {
                                screenModel.generatePlan(
                                    fitnessLevel = "intermediate", // Auto detect or default
                                    goal = goal,
                                    daysPerWeek = daysPerWeek.toInt(),
                                    durationMin = sessionDuration.toIntOrNull() ?: 60,
                                    equipment = selectedEquipment.toList(),
                                    injuries = injuries.ifBlank { null },
                                    includeImbalanceCorrectives = includeCorrectives
                                )
                                showForm = false
                            }
                        )

                        if (state.activePlan != null) {
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
                    // Show generated active plan
                    val plan = state.activePlan!!
                    val planData = plan.plan_data

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = SurfaceColor),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(
                                    text = planData.title,
                                    color = TextPrimaryColor,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${planData.duration_weeks}-Week Personalized Routine",
                                    color = PrimaryColor,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                if (plan.imbalance_used) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Info, contentDescription = null, tint = AccentColor, modifier = Modifier.size(16.dp))
                                        Text(
                                            text = " Imbalance correctives injected",
                                            color = AccentColor,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        // Accordion/List of Days
                        planData.days.forEach { day ->
                            Text(
                                text = "${day.day} — ${day.focus}",
                                color = TextPrimaryColor,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                            
                            day.exercises.forEach { exercise ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = SurfaceColor)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = exercise.name,
                                                color = TextPrimaryColor,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp,
                                                modifier = Modifier.weight(1f)
                                            )
                                            Text(
                                                text = "${exercise.sets} sets x ${exercise.reps}",
                                                color = PrimaryColor,
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 14.sp
                                            )
                                        }

                                        Row(
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = "Rest: ${exercise.rest_seconds}s",
                                                color = TextSecondaryColor,
                                                fontSize = 12.sp
                                            )
                                        }
                                        
                                        exercise.notes?.let { note ->
                                            HorizontalDivider(color = BackgroundColor)
                                            Text(
                                                text = "Coach tip: $note",
                                                color = TextSecondaryColor,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // General Notes & Nutrition
                        if (!planData.general_notes.isNullOrBlank()) {
                            Text("Coach's Progression Notes", color = TextPrimaryColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = SurfaceColor)
                            ) {
                                Text(
                                    text = planData.general_notes!!,
                                    color = TextPrimaryColor,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }

                        if (!planData.nutrition_reminder.isNullOrBlank()) {
                            Text("Routine Nutrition Guidelines", color = TextPrimaryColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = SurfaceColor)
                            ) {
                                Text(
                                    text = planData.nutrition_reminder!!,
                                    color = TextPrimaryColor,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }

                        // Generate New Plan Button
                        AppButton(
                            text = "Re-Generate Routine",
                            onClick = { showForm = true }
                        )
                    }
                }
            }
        }
    }
}
