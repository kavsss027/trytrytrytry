package com.gujaratifitness.app.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun GifImage(
    url: String,
    modifier: Modifier = Modifier,
    contentDescription: String? = null
)
