package com.artha.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Backspace
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.artha.ui.theme.LocalArthaColors

@Composable
fun MathKeypad(
    onDigitClick: (String) -> Unit,
    onOperatorClick: (String) -> Unit,
    onBackspaceClick: () -> Unit,
    onClearClick: () -> Unit,
    onEvaluateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalArthaColors.current
    val view = LocalView.current

    val haptic = {
        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        // Row 1: Operators (+, -, *, /)
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            KeypadButton(
                text = "+",
                backgroundColor = colors.primaryContainer.copy(alpha = 0.6f),
                textColor = colors.onPrimaryContainer,
                onClick = { haptic(); onOperatorClick("+") },
                modifier = Modifier.weight(1f)
            )
            KeypadButton(
                text = "-",
                backgroundColor = colors.primaryContainer.copy(alpha = 0.6f),
                textColor = colors.onPrimaryContainer,
                onClick = { haptic(); onOperatorClick("-") },
                modifier = Modifier.weight(1f)
            )
            KeypadButton(
                text = "×",
                backgroundColor = colors.primaryContainer.copy(alpha = 0.6f),
                textColor = colors.onPrimaryContainer,
                onClick = { haptic(); onOperatorClick("*") },
                modifier = Modifier.weight(1f)
            )
            KeypadButton(
                text = "÷",
                backgroundColor = colors.primaryContainer.copy(alpha = 0.6f),
                textColor = colors.onPrimaryContainer,
                onClick = { haptic(); onOperatorClick("/") },
                modifier = Modifier.weight(1f)
            )
        }

        // Row 2: 7, 8, 9, C
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            KeypadButton(text = "7", onClick = { haptic(); onDigitClick("7") }, modifier = Modifier.weight(1f))
            KeypadButton(text = "8", onClick = { haptic(); onDigitClick("8") }, modifier = Modifier.weight(1f))
            KeypadButton(text = "9", onClick = { haptic(); onDigitClick("9") }, modifier = Modifier.weight(1f))
            KeypadButton(
                text = "C",
                backgroundColor = colors.surfaceVariant,
                textColor = colors.expense,
                onClick = { haptic(); onClearClick() },
                modifier = Modifier.weight(1f)
            )
        }

        // Row 3: 4, 5, 6, =
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            KeypadButton(text = "4", onClick = { haptic(); onDigitClick("4") }, modifier = Modifier.weight(1f))
            KeypadButton(text = "5", onClick = { haptic(); onDigitClick("5") }, modifier = Modifier.weight(1f))
            KeypadButton(text = "6", onClick = { haptic(); onDigitClick("6") }, modifier = Modifier.weight(1f))
            KeypadButton(
                text = "=",
                backgroundColor = colors.primary,
                textColor = colors.onPrimary,
                onClick = { haptic(); onEvaluateClick() },
                modifier = Modifier.weight(1f)
            )
        }

        // Row 4: 1, 2, 3, ⌫
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            KeypadButton(text = "1", onClick = { haptic(); onDigitClick("1") }, modifier = Modifier.weight(1f))
            KeypadButton(text = "2", onClick = { haptic(); onDigitClick("2") }, modifier = Modifier.weight(1f))
            KeypadButton(text = "3", onClick = { haptic(); onDigitClick("3") }, modifier = Modifier.weight(1f))
            KeypadIconButton(
                onClick = { haptic(); onBackspaceClick() },
                backgroundColor = colors.surfaceVariant,
                iconTint = colors.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )
        }

        // Row 5: 00, 0, .
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            KeypadButton(text = "00", onClick = { haptic(); onDigitClick("00") }, modifier = Modifier.weight(1f))
            KeypadButton(text = "0", onClick = { haptic(); onDigitClick("0") }, modifier = Modifier.weight(2f))
            KeypadButton(text = ".", onClick = { haptic(); onDigitClick(".") }, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun KeypadButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = LocalArthaColors.current.surfaceVariant.copy(alpha = 0.7f),
    textColor: Color = LocalArthaColors.current.onSurface
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(backgroundColor)
            .clickable { onClick() }
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun KeypadIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = LocalArthaColors.current.surfaceVariant.copy(alpha = 0.7f),
    iconTint: Color = LocalArthaColors.current.onSurface
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(backgroundColor)
            .clickable { onClick() }
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.Backspace,
            contentDescription = "Backspace",
            tint = iconTint
        )
    }
}
