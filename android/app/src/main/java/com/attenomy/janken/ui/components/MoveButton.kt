package com.attenomy.janken.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.attenomy.janken.data.Move
import com.attenomy.janken.ui.theme.LizardColor
import com.attenomy.janken.ui.theme.PaperColor
import com.attenomy.janken.ui.theme.RockColor
import com.attenomy.janken.ui.theme.ScissorsColor
import com.attenomy.janken.ui.theme.SpockColor

fun Move.getColor(): Color = when (this) {
    Move.ROCK -> RockColor
    Move.PAPER -> PaperColor
    Move.SCISSORS -> ScissorsColor
    Move.LIZARD -> LizardColor
    Move.SPOCK -> SpockColor
}

@Composable
fun MoveButton(
    move: Move,
    modifier: Modifier = Modifier,
    size: Dp = 90.dp,
    selected: Boolean = false,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.88f else if (selected) 1.06f else 1f,
        animationSpec = spring(dampingRatio = 0.6f),
        label = "move_scale"
    )

    val color = move.getColor()

    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .size(size)
            .scale(scale),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(
            width = if (selected) 3.dp else 1.5.dp,
            color = if (selected) color else color.copy(alpha = 0.5f)
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (selected) color.copy(alpha = 0.18f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        contentPadding = PaddingValues(4.dp),
        interactionSource = interactionSource
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = move.emoji,
                fontSize = if (size < 80.dp) 26.sp else 34.sp
            )
            Text(
                text = move.title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = if (selected) color else MaterialTheme.colorScheme.onSurface,
                fontSize = if (size < 80.dp) 11.sp else 13.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}
