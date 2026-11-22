package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.model.BodyRegionId
import com.example.ui.theme.BioluminescentTeal
import com.example.ui.theme.DeepNavyBg
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.GlassBorderCyan
import com.example.ui.theme.SkyBlue
import com.example.ui.theme.TextMuted

@Composable
fun BodyHologramCanvas(
    selectedRegion: BodyRegionId,
    trackedRegions: Set<BodyRegionId>,
    onSelectRegion: (BodyRegionId) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.testTag("body_hologram_diagram"),
        shape = MaterialTheme.shapes.medium,
        color = DeepNavyBg.copy(alpha = 0.85f),
        border = BorderStroke(1.dp, GlassBorderCyan),
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(200f / 350f)
                    .semantics {
                        contentDescription = "Interactive holographic human body diagram"
                    }
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            val nx = offset.x / size.width * 200f
                            val ny = offset.y / size.height * 350f
                            val tapped = hitTestDiagram(nx, ny)
                            if (tapped != null) {
                                onSelectRegion(tapped)
                            }
                        }
                    },
            ) {
                val sx = size.width / 200f
                val sy = size.height / 350f

                // Holographic background glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(ElectricCyan.copy(alpha = 0.16f), Color.Transparent),
                        center = Offset(100f * sx, 170f * sy),
                        radius = 96f * sx,
                    ),
                    radius = 96f * sx,
                    center = Offset(100f * sx, 170f * sy),
                )

                // Base scanning ring
                drawOval(
                    color = ElectricCyan.copy(alpha = 0.35f),
                    topLeft = Offset(40f * sx, 330f * sy),
                    size = Size(120f * sx, 16f * sy),
                    style = Stroke(
                        width = 1.6f * sx,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f)),
                    ),
                )

                fun regionColors(id: BodyRegionId): Pair<Color, Color> {
                    val isSelected = selectedRegion == id
                    val isTracked = trackedRegions.contains(id)
                    return when {
                        isSelected -> ElectricCyan.copy(alpha = 0.50f) to Color.White
                        isTracked -> BioluminescentTeal.copy(alpha = 0.25f) to BioluminescentTeal
                        else -> SkyBlue.copy(alpha = 0.14f) to ElectricCyan.copy(alpha = 0.48f)
                    }
                }

                // HEAD
                run {
                    val (fill, stroke) = regionColors(BodyRegionId.HEAD)
                    drawOval(
                        color = fill,
                        topLeft = Offset(80f * sx, 14f * sy),
                        size = Size(40f * sx, 50f * sy),
                    )
                    drawOval(
                        color = stroke,
                        topLeft = Offset(80f * sx, 14f * sy),
                        size = Size(40f * sx, 50f * sy),
                        style = Stroke(width = if (selectedRegion == BodyRegionId.HEAD) 2.5f * sx else 1.5f * sx),
                    )
                    drawRoundRect(
                        color = fill,
                        topLeft = Offset(92f * sx, 62f * sy),
                        size = Size(16f * sx, 12f * sy),
                        cornerRadius = CornerRadius(4f * sx, 4f * sy),
                    )
                }

                // SHOULDERS
                run {
                    val (fill, stroke) = regionColors(BodyRegionId.SHOULDERS)
                    val path = Path().apply {
                        moveTo(56f * sx, 82f * sy)
                        quadraticTo(100f * sx, 68f * sy, 144f * sx, 82f * sy)
                        lineTo(148f * sx, 98f * sy)
                        quadraticTo(100f * sx, 88f * sy, 52f * sx, 98f * sy)
                        close()
                    }
                    drawRegionPath(path, fill, stroke, selectedRegion == BodyRegionId.SHOULDERS, sx)
                }

                // CHEST
                run {
                    val (fill, stroke) = regionColors(BodyRegionId.CHEST)
                    val path = Path().apply {
                        moveTo(64f * sx, 98f * sy)
                        lineTo(136f * sx, 98f * sy)
                        lineTo(130f * sx, 146f * sy)
                        lineTo(70f * sx, 146f * sy)
                        close()
                    }
                    drawRegionPath(path, fill, stroke, selectedRegion == BodyRegionId.CHEST, sx)
                }

                // ABDOMEN
                run {
                    val (fill, stroke) = regionColors(BodyRegionId.ABDOMEN)
                    val path = Path().apply {
                        moveTo(70f * sx, 150f * sy)
                        lineTo(130f * sx, 150f * sy)
                        lineTo(134f * sx, 198f * sy)
                        quadraticTo(100f * sx, 208f * sy, 66f * sx, 198f * sy)
                        close()
                    }
                    drawRegionPath(path, fill, stroke, selectedRegion == BodyRegionId.ABDOMEN, sx)
                }

                // ARMS (Left + Right)
                run {
                    val (fill, stroke) = regionColors(BodyRegionId.ARMS)
                    val strokeW = if (selectedRegion == BodyRegionId.ARMS) 2.5f * sx else 1.5f * sx
                    drawRoundRect(
                        color = fill,
                        topLeft = Offset(38f * sx, 96f * sy),
                        size = Size(16f * sx, 82f * sy),
                        cornerRadius = CornerRadius(8f * sx, 8f * sy),
                    )
                    drawRoundRect(
                        color = stroke,
                        topLeft = Offset(38f * sx, 96f * sy),
                        size = Size(16f * sx, 82f * sy),
                        cornerRadius = CornerRadius(8f * sx, 8f * sy),
                        style = Stroke(width = strokeW),
                    )
                    drawRoundRect(
                        color = fill,
                        topLeft = Offset(146f * sx, 96f * sy),
                        size = Size(16f * sx, 82f * sy),
                        cornerRadius = CornerRadius(8f * sx, 8f * sy),
                    )
                    drawRoundRect(
                        color = stroke,
                        topLeft = Offset(146f * sx, 96f * sy),
                        size = Size(16f * sx, 82f * sy),
                        cornerRadius = CornerRadius(8f * sx, 8f * sy),
                        style = Stroke(width = strokeW),
                    )
                }

                // HANDS (Left + Right)
                run {
                    val (fill, stroke) = regionColors(BodyRegionId.HANDS)
                    val strokeW = if (selectedRegion == BodyRegionId.HANDS) 2.5f * sx else 1.5f * sx
                    drawOval(fill, Offset(36f * sx, 182f * sy), Size(18f * sx, 26f * sy))
                    drawOval(stroke, Offset(36f * sx, 182f * sy), Size(18f * sx, 26f * sy), style = Stroke(strokeW))
                    drawOval(fill, Offset(146f * sx, 182f * sy), Size(18f * sx, 26f * sy))
                    drawOval(stroke, Offset(146f * sx, 182f * sy), Size(18f * sx, 26f * sy), style = Stroke(strokeW))
                }

                // LEGS (Left + Right)
                run {
                    val (fill, stroke) = regionColors(BodyRegionId.LEGS)
                    val strokeW = if (selectedRegion == BodyRegionId.LEGS) 2.5f * sx else 1.5f * sx
                    drawRoundRect(
                        color = fill,
                        topLeft = Offset(69f * sx, 204f * sy),
                        size = Size(24f * sx, 108f * sy),
                        cornerRadius = CornerRadius(10f * sx, 10f * sy),
                    )
                    drawRoundRect(
                        color = stroke,
                        topLeft = Offset(69f * sx, 204f * sy),
                        size = Size(24f * sx, 108f * sy),
                        cornerRadius = CornerRadius(10f * sx, 10f * sy),
                        style = Stroke(width = strokeW),
                    )
                    drawRoundRect(
                        color = fill,
                        topLeft = Offset(107f * sx, 204f * sy),
                        size = Size(24f * sx, 108f * sy),
                        cornerRadius = CornerRadius(10f * sx, 10f * sy),
                    )
                    drawRoundRect(
                        color = stroke,
                        topLeft = Offset(107f * sx, 204f * sy),
                        size = Size(24f * sx, 108f * sy),
                        cornerRadius = CornerRadius(10f * sx, 10f * sy),
                        style = Stroke(width = strokeW),
                    )
                }

                // FEET (Left + Right)
                run {
                    val (fill, stroke) = regionColors(BodyRegionId.FEET)
                    val leftFoot = Path().apply {
                        moveTo(68f * sx, 315f * sy)
                        lineTo(92f * sx, 315f * sy)
                        lineTo(93f * sx, 334f * sy)
                        lineTo(60f * sx, 334f * sy)
                        quadraticTo(60f * sx, 320f * sy, 68f * sx, 315f * sy)
                        close()
                    }
                    val rightFoot = Path().apply {
                        moveTo(108f * sx, 315f * sy)
                        lineTo(132f * sx, 315f * sy)
                        quadraticTo(140f * sx, 320f * sy, 140f * sx, 334f * sy)
                        lineTo(107f * sx, 334f * sy)
                        close()
                    }
                    drawRegionPath(leftFoot, fill, stroke, selectedRegion == BodyRegionId.FEET, sx)
                    drawRegionPath(rightFoot, fill, stroke, selectedRegion == BodyRegionId.FEET, sx)
                }
            }

            Text(
                text = "HOLOGRAM MAP",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
            )
        }
    }
}

private fun DrawScope.drawRegionPath(
    path: Path,
    fill: Color,
    stroke: Color,
    isSelected: Boolean,
    scaleX: Float,
) {
    drawPath(path = path, color = fill)
    drawPath(
        path = path,
        color = stroke,
        style = Stroke(width = if (isSelected) 2.5f * scaleX else 1.5f * scaleX),
    )
}

private fun hitTestDiagram(x: Float, y: Float): BodyRegionId? {
    val hitZones: List<Pair<BodyRegionId, Rect>> = listOf(
        BodyRegionId.HEAD to Rect(74f, 10f, 126f, 74f),
        BodyRegionId.SHOULDERS to Rect(50f, 70f, 150f, 99f),
        BodyRegionId.CHEST to Rect(62f, 98f, 138f, 148f),
        BodyRegionId.ABDOMEN to Rect(64f, 148f, 136f, 204f),
        BodyRegionId.HANDS to Rect(28f, 178f, 58f, 214f),
        BodyRegionId.HANDS to Rect(142f, 178f, 172f, 214f),
        BodyRegionId.ARMS to Rect(32f, 94f, 58f, 180f),
        BodyRegionId.ARMS to Rect(142f, 94f, 168f, 180f),
        BodyRegionId.LEGS to Rect(65f, 204f, 135f, 314f),
        BodyRegionId.FEET to Rect(56f, 314f, 144f, 342f),
    )
    return hitZones.firstOrNull { (_, rect) -> rect.contains(Offset(x, y)) }?.first
}
