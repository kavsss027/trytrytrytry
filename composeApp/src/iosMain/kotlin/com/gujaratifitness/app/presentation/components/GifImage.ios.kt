package com.gujaratifitness.app.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import platform.WebKit.WKWebView
import platform.Foundation.NSURL
import platform.Foundation.NSURLRequest
import platform.UIKit.UIColor
import platform.UIKit.clearColor

@Composable
actual fun GifImage(
    url: String,
    modifier: Modifier,
    contentDescription: String?
) {
    UIKitView(
        factory = {
            WKWebView().apply {
                scrollView.scrollEnabled = false
                scrollView.bounces = false
                opaque = false
                backgroundColor = UIColor.clearColor
                val nsUrl = NSURL.URLWithString(url)
                if (nsUrl != null) {
                    loadRequest(NSURLRequest.requestWithURL(nsUrl))
                }
            }
        },
        modifier = modifier
    )
}
