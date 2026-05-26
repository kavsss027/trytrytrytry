package com.gujaratifitness.app.presentation.screens.diet

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.gujaratifitness.app.core.utils.*
import com.gujaratifitness.app.presentation.components.AppButton
import com.gujaratifitness.app.presentation.components.AppTextField

object DietTab : Tab {

    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Default.Favorite)
            return remember {
                TabOptions(
                    index = 3u,
                    title = "Diet",
                    icon = icon
                )
            }
        }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<DietScreenModel>()
        val state by screenModel.state.collectAsState()

        var showForm by remember { mutableStateOf(false) }

        // Form Fields
        var goal by remember { mutableStateOf("Fat Loss") }
        val goals = listOf("Fat Loss", "Muscle Gain", "Maintenance")

        var age by remember { mutableStateOf("") }
        var weight by remember { mutableStateOf("") }
        var height by remember { mutableStateOf("") }
        var gender by remember { mutableStateOf("Male") }
        val genders = listOf("Male", "Female", "Other")

        var activityLevel by remember { mutableStateOf("Moderately Active") }
        val activities = listOf("Sedentary", "Lightly Active", "Moderately Active", "Very Active")

        var dietPreference by remember { mutableStateOf("Vegetarian") }
        val preferences = listOf("Vegetarian", "Non-Vegetarian", "Vegan")

        var gujaratiPreference by remember { mutableStateOf(true) }
        var allergies by remember { mutableStateOf("") }
        var mealsPerDay by remember { mutableStateOf(4.0f) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("AI Nutrition Planner", fontWeight = FontWeight.Bold, color = TextPrimaryColor) },
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
                            Text("Gemini is cooking your diet plan...", color = TextSecondaryColor, fontSize = 14.sp)
                        }
                    }
                } else if (state.activePlan == null || showForm) {
                    // Show Form
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Diet Preferences",
                            color = TextPrimaryColor,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

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

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            AppTextField(
                                value = age,
                                onValueChange = { age = it },
                                label = "Age",
                                placeholder = "e.g., 25",
                                modifier = Modifier.weight(1f)
                            )
                            AppTextField(
                                value = weight,
                                onValueChange = { weight = it },
                                label = "Weight (kg)",
                                placeholder = "e.g., 70",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            AppTextField(
                                value = height,
                                onValueChange = { height = it },
                                label = "Height (cm)",
                                placeholder = "e.g., 175",
                                modifier = Modifier.weight(1f)
                            )
                            
                            // Gender Select inline
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Gender", color = TextSecondaryColor, fontSize = 12.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                var expanded by remember { mutableStateOf(false) }
                                Box {
                                    OutlinedButton(
                                        onClick = { expanded = true },
                                        modifier = Modifier.fillMaxWidth().height(56.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimaryColor),
                                        border = BorderStroke(1.dp, TextSecondaryColor.copy(alpha = 0.5f))
                                    ) {
                                        Text(gender)
                                    }
                                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                        genders.forEach { g ->
                                            DropdownMenuItem(
                                                text = { Text(g) },
                                                onClick = {
                                                    gender = g
                                                    expanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Activity Level
                        Text("Activity Level", color = TextSecondaryColor, fontSize = 14.sp)
                        Row(
                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            activities.forEach { a ->
                                FilterChip(
                                    selected = activityLevel == a,
                                    onClick = { activityLevel = a },
                                    label = { Text(a) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        containerColor = SurfaceColor,
                                        labelColor = TextSecondaryColor,
                                        selectedContainerColor = PrimaryContainerColor,
                                        selectedLabelColor = PrimaryColor
                                    )
                                )
                            }
                        }

                        // Dietary Preference
                        Text("Diet Type", color = TextSecondaryColor, fontSize = 14.sp)
                        Row(
                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            preferences.forEach { p ->
                                FilterChip(
                                    selected = dietPreference == p,
                                    onClick = { dietPreference = p },
                                    label = { Text(p) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        containerColor = SurfaceColor,
                                        labelColor = TextSecondaryColor,
                                        selectedContainerColor = PrimaryContainerColor,
                                        selectedLabelColor = PrimaryColor
                                    )
                                )
                            }
                        }

                        // Gujarati preference check box
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { gujaratiPreference = !gujaratiPreference }
                                .padding(vertical = 4.dp)
                        ) {
                            Checkbox(
                                checked = gujaratiPreference,
                                onCheckedChange = { gujaratiPreference = it ?: false },
                                colors = CheckboxDefaults.colors(checkedColor = PrimaryColor)
                            )
                            Column(modifier = Modifier.padding(start = 4.dp)) {
                                Text("Prefer Traditional Gujarati Healthy Options", color = TextPrimaryColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text(
                                    "Integrates protein-rich Gujarati items (Handvo, Hand-made Rotli, Dal, Hand-churned buttermilk, Sprouted Mung paneer mix, Muthia, Khichdi, Handvo)",
                                    color = TextSecondaryColor,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        AppTextField(
                            value = allergies,
                            onValueChange = { allergies = it },
                            label = "Food Exclusions / Allergies",
                            placeholder = "e.g., Gluten free, Lactose intolerant, No peanuts"
                        )

                        Column {
                            Text("Meals per Day: ${mealsPerDay.toInt()}", color = TextSecondaryColor, fontSize = 14.sp)
                            Slider(
                                value = mealsPerDay,
                                onValueChange = { mealsPerDay = it },
                                valueRange = 3f..5f,
                                steps = 1,
                                colors = SliderDefaults.colors(thumbColor = PrimaryColor, activeTrackColor = PrimaryColor)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        AppButton(
                            text = "Generate Nutrition Plan",
                            onClick = {
                                screenModel.generatePlan(
                                    fitnessLevel = "intermediate",
                                    goal = goal,
                                    age = age.toIntOrNull() ?: 25,
                                    gender = gender,
                                    weightKg = weight.toDoubleOrNull() ?: 70.0,
                                    heightCm = height.toDoubleOrNull() ?: 175.0,
                                    activityLevel = activityLevel,
                                    dietaryPreference = dietPreference,
                                    allergies = allergies.ifBlank { null },
                                    mealsPerDay = mealsPerDay.toInt(),
                                    gujaratiPreference = gujaratiPreference
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
                    // Show Diet Plan
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
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Daily Target", color = TextSecondaryColor, fontSize = 12.sp)
                                        Text("${planData.daily_calories} kcal", color = PrimaryColor, fontSize = 24.sp, fontWeight = FontWeight.Black)
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Water Target", color = TextSecondaryColor, fontSize = 12.sp)
                                        Text("${planData.hydration_litres} L/day", color = AccentColor, fontSize = 24.sp, fontWeight = FontWeight.Black)
                                    }
                                }
                            }
                        }

                        // Macros Breakdown
                        Text("Macro Distribution", color = TextPrimaryColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Protein
                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(containerColor = SurfaceColor)
                            ) {
                                Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("PROTEIN", color = PrimaryColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("${planData.macros.protein_g}g", color = TextPrimaryColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            
                            // Carbs
                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(containerColor = SurfaceColor)
                            ) {
                                Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("CARBS", color = AccentColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("${planData.macros.carbs_g}g", color = TextPrimaryColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            // Fat
                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(containerColor = SurfaceColor)
                            ) {
                                Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("FATS", color = Color(0xFFFFC107), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("${planData.macros.fat_g}g", color = TextPrimaryColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // Meals
                        Text("Suggested Daily Meal Schedule", color = TextPrimaryColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        planData.meals.forEach { meal ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = SurfaceColor)
                            ) {
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(meal.meal, color = TextPrimaryColor, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                            Text(meal.time, color = TextSecondaryColor, fontSize = 12.sp)
                                        }
                                        Badge(containerColor = SecondaryColor, contentColor = PrimaryColor) {
                                            Text("${meal.calories} kcal", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    
                                    Divider(color = BackgroundColor)
                                    
                                    meal.foods.forEach { food ->
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text("•", color = PrimaryColor, fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 8.dp))
                                            Text(food, color = TextPrimaryColor, fontSize = 14.sp)
                                        }
                                    }
                                }
                            }
                        }

                        // Notes
                        if (!planData.notes.isNullOrBlank()) {
                            Text("Nutritionist Notes", color = TextPrimaryColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = SurfaceColor)
                            ) {
                                Text(
                                    text = planData.notes!!,
                                    color = TextPrimaryColor,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }

                        // Generate New Plan Button
                        AppButton(
                            text = "Re-Generate Nutrition Plan",
                            onClick = { showForm = true }
                        )
                    }
                }
            }
        }
    }
}
