package com.sary.task.util

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.compose.ImagePainter
import coil.compose.LocalImageLoader
import coil.compose.rememberImagePainter
import coil.size.OriginalSize
import com.sary.task.R
import com.sary.task.theme.AppTheme
import com.sary.task.theme.compositedOnSurface

/**
 * A wrapper around [Image] and [rememberImagePainter], setting a
 * default [contentScale] and showing content while loading.
 */
@OptIn(ExperimentalCoilApi::class)
@Composable
fun NetworkImage(
    url: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Inside,
    placeholderColor: Color? = AppTheme.colors.compositedOnSurface(0.2f)
) {
    Box(modifier) {
        val painter = rememberImagePainter(
            data = url,
            builder = {
                size(OriginalSize)
                placeholder(drawableResId = R.drawable.coffee)
            }
        )

        Image(
            painter = painter,
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = Modifier.fillMaxSize()
        )

        if (painter.state is ImagePainter.State.Loading && placeholderColor != null) {
            Spacer(
                modifier = Modifier
                    .matchParentSize()
                    .background(placeholderColor)
            )
        }
    }
}

@Composable
fun ProvideImageLoader(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val loader = remember(context) { ImageLoader.Builder(context).build() }
    CompositionLocalProvider(LocalImageLoader provides loader, content = content)
}
