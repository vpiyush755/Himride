package com.example.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MountainBackgroundIllustration(
    modifier: Modifier = Modifier,
    primaryColor: Color,
    secondaryColor: Color
) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        // Background gradients list
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(primaryColor.copy(alpha = 0.15f), Color.Transparent),
                startY = 0f,
                endY = height
            )
        )

        // Draw scenic mountains range
        val path1 = Path().apply {
            moveTo(0f, height * 0.8f)
            lineTo(width * 0.25f, height * 0.45f)
            lineTo(width * 0.5f, height * 0.75f)
            lineTo(width * 0.75f, height * 0.35f)
            lineTo(width, height * 0.85f)
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }
        drawPath(path = path1, color = secondaryColor.copy(alpha = 0.2f))

        val path2 = Path().apply {
            moveTo(0f, height)
            lineTo(width * 0.15f, height * 0.65f)
            lineTo(width * 0.4f, height * 0.5f)
            lineTo(width * 0.7f, height * 0.7f)
            lineTo(width * 0.85f, height * 0.45f)
            lineTo(width, height)
            close()
        }
        drawPath(path = path2, color = secondaryColor.copy(alpha = 0.3f))
    }
}

@Composable
fun SimpleHorizontalDivider(color: Color = Color.LightGray.copy(alpha = 0.5f), thickness: Dp = 1.dp) {
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(thickness)
            .background(color)
    )
}

@Composable
fun SimulatedNetworkStatusIndicator(isOnline: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
            .background(
                if (isOnline) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(
                    if (isOnline) Color(0xFF4CAF50) else Color(0xFFF44336),
                    RoundedCornerShape(50)
                )
        )
        Text(
            text = if (isOnline) "SERVER CONNECTED" else "OFFLINE SIMULATOR",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = if (isOnline) Color(0xFF2E7D32) else Color(0xFFC62828)
        )
    }
}

// Analytical bar chart using modern canvas rendering
@Composable
fun AnalyticsBarChart(
    modifier: Modifier = Modifier,
    data: List<Float>,
    labels: List<String>,
    barColor: Color
) {
    if (data.isEmpty()) return
    val maxVal = data.maxOrNull()?.coerceAtLeast(1f) ?: 1f

    Column(modifier = modifier) {
        Canvas(modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            val paddingLeft = 40f
            val paddingBottom = 40f
            val graphWidth = canvasWidth - paddingLeft - 20f
            val graphHeight = canvasHeight - paddingBottom - 20f

            // Draw axis line grid
            drawLine(
                color = Color.LightGray.copy(alpha = 0.5f),
                start = Offset(paddingLeft, graphHeight + 10f),
                end = Offset(canvasWidth, graphHeight + 10f),
                strokeWidth = 2f
            )

            val barSpacing = graphWidth / data.size
            val barWidth = barSpacing * 0.6f

            data.forEachIndexed { idx, value ->
                val barHeight = (value / maxVal) * graphHeight
                val left = paddingLeft + (idx * barSpacing) + (barSpacing - barWidth) / 2
                val top = graphHeight - barHeight

                drawRoundRect(
                    color = barColor,
                    topLeft = Offset(left, top),
                    size = androidx.compose.ui.geometry.Size(barWidth, barHeight),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f, 10f)
                )
                
                // Draw tiny text tags over bar if fits
                // Draw indicator circle or outline
            }
        }
        
        // Show labels row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            labels.forEach { label ->
                Text(
                    text = label,
                    fontSize = 10.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
