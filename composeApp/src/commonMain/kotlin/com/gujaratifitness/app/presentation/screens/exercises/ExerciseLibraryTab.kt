package com.gujaratifitness.app.presentation.screens.exercises

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.gujaratifitness.app.core.utils.*
import com.gujaratifitness.app.data.model.ExerciseDto
import com.gujaratifitness.app.presentation.components.EmptyState
import com.gujaratifitness.app.presentation.components.GifImage

object ExerciseLibraryTab : Tab {

    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Default.List)
            return remember {
                TabOptions(
                    index = 0u,
                    title = "Library",
                    icon = icon
                )
            }
        }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<ExerciseScreenModel>()
        val state by screenModel.state.collectAsState()

        var searchQuery by remember { mutableStateOf("") }
        var selectedCategory by remember { mutableStateOf("All") }
        var selectedExercise by remember { mutableStateOf<ExerciseDto?>(null) }

        val categories = listOf("All", "Chest", "Back", "Shoulders", "Biceps", "Triceps", "Legs", "Glutes", "Core", "Full_Body")

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Exercise Library", fontWeight = FontWeight.Bold, color = TextPrimaryColor) },
                    actions = {
                        IconButton(onClick = { screenModel.forceSync() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Sync", tint = PrimaryColor)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundColor)
                )
            },
            containerColor = BackgroundColor
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Search field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        screenModel.filterExercises(selectedCategory, it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search exercises...", color = TextSecondaryColor) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondaryColor) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimaryColor,
                        unfocusedTextColor = TextPrimaryColor,
                        focusedBorderColor = PrimaryColor,
                        unfocusedBorderColor = SecondaryColor,
                        cursorColor = PrimaryColor
                    )
                )

                // Horizontal Categories Selector
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = {
                                selectedCategory = cat
                                screenModel.filterExercises(cat, searchQuery)
                            },
                            label = { Text(cat) },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = SurfaceColor,
                                labelColor = TextSecondaryColor,
                                selectedContainerColor = PrimaryContainerColor,
                                selectedLabelColor = PrimaryColor
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selectedCategory == cat,
                                borderColor = Color.Transparent,
                                selectedBorderColor = PrimaryColor
                            )
                        )
                    }
                }

                // Exercise List Cache loader
                if (state.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = PrimaryColor)
                    }
                } else if (state.exercises.isEmpty()) {
                    EmptyState(
                        title = "No Exercises Found",
                        description = "Sync with remote database or adjust search query."
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(state.exercises) { exercise ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedExercise = exercise },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = SurfaceColor)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = exercise.name,
                                            color = TextPrimaryColor,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = exercise.muscle_group.uppercase(),
                                            color = PrimaryColor,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                    
                                    Badge(
                                        containerColor = SecondaryColor,
                                        contentColor = TextSecondaryColor,
                                        modifier = Modifier.padding(start = 8.dp)
                                    ) {
                                        Text(
                                            text = exercise.difficulty.uppercase(),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Details Modal Dialog showing gif and description
        selectedExercise?.let { exercise ->
            Dialog(onDismissRequest = { selectedExercise = null }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceColor)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = exercise.name,
                            color = TextPrimaryColor,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Render GifImage expect/actual component
                        GifImage(
                            url = exercise.gif_url,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .background(BackgroundColor, RoundedCornerShape(16.dp)),
                            contentDescription = exercise.name
                        )

                        Text(
                            text = exercise.description ?: "No description provided for this exercise.",
                            color = TextSecondaryColor,
                            fontSize = 14.sp,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = { selectedExercise = null },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Close", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
