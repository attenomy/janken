package com.attenomy.janken.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.random.Random

data class Particle(
    val x: Float, // 0..1 fraction
    val ySpeed: Float,
    val xSpeed: Float,
    val size: Float,
    val color: Color,
    val rotationSpeed: Float,
    val isCircle: Boolean
)

@Composable
fun ConfettiEffect(
    modifier: Modifier = Modifier,
    particleCount: Int = 60
) {
    val progress = remember { Animatable(0f) }

    val colors = listOf(
        Color(0xFFEF4444),
        Color(0xFF3B82F6),
        Color(0xFF10B981),
        Color(0xFFF59E0B),
        Color(0xFF8B5CF6),
        Color(0xFFEC4899),
        Color(0xFF06B6D4)
    )

    val particles = remember {
        List(particleCount) {
            Particle(
                x = Random.nextFloat(),
                ySpeed = Random.nextFloat() * 0.8f + 0.5f,
                xSpeed = (Random.nextFloat() - 0.5f) * 0.2f,
                size = Random.nextFloat() * 14f + 8f,
                color = colors.random(),
                rotationSpeed = (Random.nextFloat() - 0.5f) * 720f,
                isCircle = Random.nextBoolean()
            )
        }
    }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 2600, easing = LinearEasing)
        )
    }

    if (progress.value < 1f) {
        Canvas(modifier = modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val currentProgress = progress.value

            particles.forEach { p ->
                val currentY = (currentProgress * p.ySpeed * canvasHeight * 1.2f) - (p.size * 2)
                val currentX = (p.x * canvasWidth) + (p.xSpeed * currentProgress * canvasWidth)
                val rotation = currentProgress * p.rotationSpeed
                val alpha = (1f - (currentProgress - 0.7f) / 0.3f).coerceIn(0f, 1f)

                if (currentY in -50f..canvasHeight + 50f) {
                    rotate(rotation, pivot = Offset(currentX, currentY)) {
                        if (p.isCircle) {
                            drawCircle(
                                color = p.color.copy(alpha = alpha),
                                radius = p.size / 2,
                                center = Offset(currentX, currentY)
                            )
                        } else {
                            drawRect(
                                color = p.color.copy(alpha = alpha),
                                topLeft = Offset(currentX - p.size / 2, currentY - p.size / 4),
                                size = Size(p.size, p.size / 2)
                            )
                        }
                    }
                }
            }
        }
    }
}
