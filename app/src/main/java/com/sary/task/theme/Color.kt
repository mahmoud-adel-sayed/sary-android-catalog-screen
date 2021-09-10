package com.sary.task.theme

import androidx.compose.material.Colors
import androidx.compose.material.LocalElevationOverlay
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.unit.Dp

val Purple200 = Color(0xffbb86fc)
val Purple700 = Color(0xff3700B3)

val Teal200 = Color(0xff03dac5)
val Teal700 = Color(0xff018786)

val Red200 = Color(0xfff297a2)
val Red800 = Color(0xffd00036)

val Gray = Color(0xFFaaaaaa)

val Colors.snackBarAction: Color
    @Composable
    get() = if (isLight) Teal200 else Teal700

val Colors.cardBackground: Color
    @Composable
    get() = if (isLight) Color.Black.copy(alpha = 0.05f) else Color.White.copy(alpha = 0.24f)

/**
 * Return the fully opaque color that results from compositing onSurface atop surface with the
 * given [alpha]. Useful for situations where semi-transparent colors are undesirable.
 */
@Composable
fun Colors.compositedOnSurface(alpha: Float): Color {
    return onSurface.copy(alpha = alpha).compositeOver(surface)
}

/**
 * Calculates the color of an elevated `surface` in dark mode. Returns `surface` in light mode.
 */
@Composable
@Suppress("unused")
fun Colors.elevatedSurface(elevation: Dp): Color {
    return LocalElevationOverlay.current?.apply(
        color = this.surface,
        elevation = elevation
    ) ?: this.surface
}