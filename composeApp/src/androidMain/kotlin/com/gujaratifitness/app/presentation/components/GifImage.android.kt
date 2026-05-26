package com.gujaratifitness.app.presentation.components

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.gif.AnimatedImageDecoder
import coil3.gif.GifDecoder

@Composable
actual fun GifImage(
    url: String,
    modifier: Modifier,
    contentDescription: String?
) {
    val context = LocalContext.current
    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(url)
            .decoderFactory { result, options, imageLoader ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    AnimatedImageDecoder.Factory().create(result, options, imageLoader)
                } else {
                    GifDecoder.Factory().create(result, options, imageLoader)
                }
            }
            .crossfade(true)
            .build(),
        contentDescription = contentDescription,
        modifier = modifier
    )
}
