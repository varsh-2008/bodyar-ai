package com.example.ui.components

import androidx.camera.core.CameraSelector
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.R
import com.example.data.AnatomyCatalog
import com.example.model.BodyRegionId
import com.example.model.CameraStatus
import com.example.model.PoseLandmarkPoint
import com.example.model.PoseTelemetry
import com.example.model.RegionBounds
import com.example.pose.PoseRegionMapper
import com.example.ui.theme.AlertAmber
import com.example.ui.theme.BioluminescentTeal
import com.example.ui.theme.CobaltBlue
import com.example.ui.theme.CoralRed
import com.example.ui.theme.DeepNavyBg
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.ElectricCyanGlow
import com.example.ui.theme.GlassBorderBright
import com.example.ui.theme.GlassBorderCyan
import com.example.ui.theme.NavyCardElevated
import com.example.ui.theme.NavyCardSurface
import com.example.ui.theme.OnCyanDark
import com.example.ui.theme.OrbitronFontFamily
import com.example.ui.theme.PlusJakartaSansFontFamily
import com.example.ui.theme.SkyBlue
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseLandmark
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CameraStageCard(
    selectedRegion: BodyRegionId,
    cameraStatus: CameraStatus,
    cameraErrorMessage: String?,
    useFrontCamera: Boolean,
    showArLabels: Boolean,
    telemetry: PoseTelemetry,
    landmarks: Map<Int, PoseLandmarkPoint>,
    regionBounds: List<RegionBounds>,
    onStartCamera: () -> Unit,
    onStopCamera: () -> Unit,
    onToggleLens: () -> Unit,
    onToggleArLabels: () -> Unit,
    onSelectRegion: (BodyRegionId) -> Unit,
    onCameraReady: () -> Unit,
    onCameraError: (String) -> Unit,
    onPoseFrame: (Map<Int, PoseLandmarkPoint>, Float, Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isCameraRunning = cameraStatus == CameraStatus.STARTING || cameraStatus == CameraStatus.ACTIVE

    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        color = NavyCardSurface.copy(alpha = 0.85f),
        border = BorderStroke(1.dp, GlassBorderCyan),
        tonalElevation = 4.dp,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Header + Status Indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.scanner_eyebrow),
                        style = MaterialTheme.typography.labelMedium,
                        color = ElectricCyan,
                    )
                    Text(
                        text = stringResource(R.string.scanner_title),
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                    )
                }
                CameraStatusBadge(cameraStatus = cameraStatus)
            }

            // Live Camera + Pose Skeleton Canvas Viewport
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(3f / 4f)
                    .clip(MaterialTheme.shapes.medium)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color(0xFF0A2044), DeepNavyBg),
                        ),
                    )
                    .border(1.dp, ElectricCyan.copy(alpha = 0.35f), MaterialTheme.shapes.medium),
            ) {
                if (isCameraRunning) {
                    LiveCameraPosePreview(
                        useFrontCamera = useFrontCamera,
                        onCameraReady = onCameraReady,
                        onCameraError = onCameraError,
                        onPoseFrame = onPoseFrame,
                        modifier = Modifier.fillMaxSize(),
                    )
                }

                // Glowing Cyan Skeleton & Interactive Region Overlay
                if (cameraStatus == CameraStatus.ACTIVE) {
                    PoseSkeletonOverlay(
                        selectedRegion = selectedRegion,
                        showArLabels = showArLabels,
                        landmarks = landmarks,
                        regionBounds = regionBounds,
                        onSelectRegion = onSelectRegion,
                        modifier = Modifier.fillMaxSize(),
                    )
                }

                // HUD Corner Brackets
                ScannerCornerBrackets(modifier = Modifier.fillMaxSize().padding(12.dp))

                // Standby / Permission / Loading / Error Overlays
                when (cameraStatus) {
                    CameraStatus.IDLE -> {
                        StandbyOverlay(onStartCamera = onStartCamera)
                    }
                    CameraStatus.REQUESTING_PERMISSION, CameraStatus.STARTING -> {
                        LoadingOverlay(
                            title = if (cameraStatus == CameraStatus.REQUESTING_PERMISSION) {
                                stringResource(R.string.status_requesting_permission)
                            } else {
                                stringResource(R.string.status_starting_camera)
                            },
                            subtitle = "Preparing CameraX stream and 33-point ML Kit Pose Landmarker…",
                        )
                    }
                    CameraStatus.PERMISSION_DENIED -> {
                        ErrorOverlay(
                            title = stringResource(R.string.permission_denied_title),
                            message = stringResource(R.string.permission_denied_message),
                            onRetry = onStartCamera,
                        )
                    }
                    CameraStatus.ERROR -> {
                        ErrorOverlay(
                            title = stringResource(R.string.status_camera_error),
                            message = cameraErrorMessage ?: "Unable to open camera feed.",
                            onRetry = onStartCamera,
                        )
                    }
                    CameraStatus.ACTIVE -> Unit
                }

                // Instruction when no person is detected
                androidx.compose.animation.AnimatedVisibility(
                    visible = cameraStatus == CameraStatus.ACTIVE && !telemetry.personDetected,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                ) {
                    Surface(
                        shape = CircleShape,
                        color = DeepNavyBg.copy(alpha = 0.88f),
                        border = BorderStroke(1.dp, AlertAmber.copy(alpha = 0.65f)),
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(AlertAmber),
                            )
                            Text(
                                text = stringResource(R.string.no_person_detected),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFFEF08A),
                            )
                        }
                    }
                }

                // Interactive tap hint when tracked
                androidx.compose.animation.AnimatedVisibility(
                    visible = cameraStatus == CameraStatus.ACTIVE && telemetry.personDetected,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                ) {
                    Surface(
                        shape = CircleShape,
                        color = DeepNavyBg.copy(alpha = 0.8f),
                        border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.35f)),
                    ) {
                        Text(
                            text = stringResource(R.string.tap_region_hint),
                            style = MaterialTheme.typography.labelSmall,
                            color = SkyBlue,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        )
                    }
                }
            }

            // Camera Action Controls
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Button(
                    onClick = onStartCamera,
                    enabled = !isCameraRunning,
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("start_camera_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElectricCyan,
                        contentColor = OnCyanDark,
                        disabledContainerColor = ElectricCyan.copy(alpha = 0.28f),
                        disabledContentColor = TextMuted,
                    ),
                    shape = MaterialTheme.shapes.small,
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.btn_start_camera),
                        fontWeight = FontWeight.Bold,
                    )
                }

                OutlinedButton(
                    onClick = onStopCamera,
                    enabled = isCameraRunning || cameraStatus == CameraStatus.REQUESTING_PERMISSION,
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("stop_camera_button"),
                    border = BorderStroke(
                        1.dp,
                        if (isCameraRunning) CoralRed.copy(alpha = 0.7f) else GlassBorderCyan.copy(alpha = 0.25f),
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = CoralRed.copy(alpha = if (isCameraRunning) 0.16f else 0.04f),
                        contentColor = Color(0xFFFF99AC),
                        disabledContentColor = TextMuted,
                    ),
                    shape = MaterialTheme.shapes.small,
                ) {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(stringResource(R.string.btn_stop_camera))
                }

                OutlinedButton(
                    onClick = onToggleLens,
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("switch_lens_button"),
                    border = BorderStroke(1.dp, GlassBorderCyan),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = NavyCardElevated.copy(alpha = 0.65f),
                        contentColor = TextSecondary,
                    ),
                    shape = MaterialTheme.shapes.small,
                ) {
                    Icon(
                        imageVector = Icons.Default.Cameraswitch,
                        contentDescription = stringResource(R.string.cd_switch_lens),
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(if (useFrontCamera) "Front" else "Rear")
                }

                OutlinedButton(
                    onClick = onToggleArLabels,
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("toggle_ar_tags_button"),
                    border = BorderStroke(
                        1.dp,
                        if (showArLabels) GlassBorderBright else GlassBorderCyan,
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (showArLabels) ElectricCyan.copy(alpha = 0.14f) else NavyCardElevated.copy(alpha = 0.5f),
                        contentColor = if (showArLabels) ElectricCyan else TextSecondary,
                    ),
                    shape = MaterialTheme.shapes.small,
                ) {
                    Text(
                        text = if (showArLabels) {
                            stringResource(R.string.btn_ar_tags_on)
                        } else {
                            stringResource(R.string.btn_ar_tags_off)
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun LiveCameraPosePreview(
    useFrontCamera: Boolean,
    onCameraReady: () -> Unit,
    onCameraError: (String) -> Unit,
    onPoseFrame: (Map<Int, PoseLandmarkPoint>, Float, Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var previewViewRef by remember { mutableStateOf<PreviewView?>(null) }

    val cameraController = remember(context) {
        LifecycleCameraController(context)
    }

    val poseDetector = remember {
        val options = PoseDetectorOptions.Builder()
            .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
            .build()
        PoseDetection.getClient(options)
    }

    DisposableEffect(lifecycleOwner, useFrontCamera, previewViewRef) {
        val previewView = previewViewRef
        if (previewView != null) {
            try {
                cameraController.cameraSelector = if (useFrontCamera) {
                    CameraSelector.DEFAULT_FRONT_CAMERA
                } else {
                    CameraSelector.DEFAULT_BACK_CAMERA
                }
                val mainExecutor = ContextCompat.getMainExecutor(context)
                cameraController.setImageAnalysisAnalyzer(
                    mainExecutor,
                    MlKitAnalyzer(
                        listOf(poseDetector),
                        CameraController.COORDINATE_SYSTEM_VIEW_REFERENCED,
                        mainExecutor,
                    ) { result ->
                        val pose = result.getValue(poseDetector)
                        val width = previewView.width.toFloat()
                        val height = previewView.height.toFloat()
                        if (pose != null && width > 0f && height > 0f) {
                            val mapped = pose.allPoseLandmarks.associate { lm ->
                                lm.landmarkType to PoseLandmarkPoint(
                                    type = lm.landmarkType,
                                    x = lm.position.x,
                                    y = lm.position.y,
                                    inFrameLikelihood = lm.inFrameLikelihood,
                                )
                            }
                            onPoseFrame(mapped, width, height)
                        }
                    },
                )
                cameraController.bindToLifecycle(lifecycleOwner)
                previewView.controller = cameraController
                onCameraReady()
            } catch (e: Exception) {
                onCameraError(e.localizedMessage ?: "Camera initialization failed on this device.")
            }
        }

        onDispose {
            try {
                cameraController.clearImageAnalysisAnalyzer()
                cameraController.unbind()
            } catch (_: Exception) {
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            try {
                poseDetector.close()
            } catch (_: Exception) {
            }
        }
    }

    AndroidView(
        factory = { ctx ->
            PreviewView(ctx).apply {
                scaleType = PreviewView.ScaleType.FILL_CENTER
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                previewViewRef = this
            }
        },
        update = { view ->
            if (previewViewRef !== view) {
                previewViewRef = view
            }
        },
        modifier = modifier,
    )
}

@Composable
private fun PoseSkeletonOverlay(
    selectedRegion: BodyRegionId,
    showArLabels: Boolean,
    landmarks: Map<Int, PoseLandmarkPoint>,
    regionBounds: List<RegionBounds>,
    onSelectRegion: (BodyRegionId) -> Unit,
    modifier: Modifier = Modifier,
) {
    val textMeasurer = rememberTextMeasurer()
    val infiniteTransition = rememberInfiniteTransition(label = "arPulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.55f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulseAlpha",
    )

    Canvas(
        modifier = modifier.pointerInput(regionBounds) {
            detectTapGestures { tapOffset ->
                val hit = regionBounds
                    .filter { it.contains(tapOffset.x, tapOffset.y) }
                    .minByOrNull { it.width * it.height }
                if (hit != null) {
                    onSelectRegion(hit.regionId)
                }
            }
        },
    ) {
        if (landmarks.isEmpty()) return@Canvas

        val activeLandmarkTypes = PoseRegionMapper.regionLandmarkIndices[selectedRegion].orEmpty()

        // 1. Selected region pulsing AR reticle box
        val selectedBox = regionBounds.firstOrNull { it.regionId == selectedRegion }
        if (selectedBox != null) {
            val accent = AnatomyCatalog.getRegion(selectedRegion).accentColor
            drawRoundRect(
                color = accent.copy(alpha = 0.10f + pulse * 0.08f),
                topLeft = Offset(selectedBox.left, selectedBox.top),
                size = Size(selectedBox.width, selectedBox.height),
                cornerRadius = CornerRadius(18f, 18f),
            )
            drawRoundRect(
                color = accent.copy(alpha = 0.85f),
                topLeft = Offset(selectedBox.left, selectedBox.top),
                size = Size(selectedBox.width, selectedBox.height),
                cornerRadius = CornerRadius(18f, 18f),
                style = Stroke(width = 3f),
            )

            // Crisp white corner brackets
            val cLen = 18f
            val bracketPath = Path().apply {
                moveTo(selectedBox.left, selectedBox.top + cLen)
                lineTo(selectedBox.left, selectedBox.top)
                lineTo(selectedBox.left + cLen, selectedBox.top)

                moveTo(selectedBox.right - cLen, selectedBox.top)
                lineTo(selectedBox.right, selectedBox.top)
                lineTo(selectedBox.right, selectedBox.top + cLen)

                moveTo(selectedBox.right, selectedBox.bottom - cLen)
                lineTo(selectedBox.right, selectedBox.bottom)
                lineTo(selectedBox.right - cLen, selectedBox.bottom)

                moveTo(selectedBox.left + cLen, selectedBox.bottom)
                lineTo(selectedBox.left, selectedBox.bottom)
                lineTo(selectedBox.left, selectedBox.bottom - cLen)
            }
            drawPath(
                path = bracketPath,
                color = Color.White,
                style = Stroke(width = 4.5f, cap = StrokeCap.Round),
            )

            if (showArLabels) {
                val label = "${AnatomyCatalog.getRegion(selectedRegion).shortLabel} • LOCKED"
                val measured = textMeasurer.measure(
                    text = label,
                    style = TextStyle(
                        fontFamily = OrbitronFontFamily,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                    ),
                )
                val pillW = measured.size.width + 20f
                val pillH = measured.size.height + 10f
                val pillX = selectedBox.left.coerceIn(10f, (size.width - pillW - 10f).coerceAtLeast(10f))
                val pillY = (selectedBox.top - pillH - 8f).coerceAtLeast(10f)

                drawRoundRect(
                    color = DeepNavyBg.copy(alpha = 0.9f),
                    topLeft = Offset(pillX, pillY),
                    size = Size(pillW, pillH),
                    cornerRadius = CornerRadius(10f, 10f),
                )
                drawRoundRect(
                    color = accent,
                    topLeft = Offset(pillX, pillY),
                    size = Size(pillW, pillH),
                    cornerRadius = CornerRadius(10f, 10f),
                    style = Stroke(width = 2f),
                )
                drawText(
                    textLayoutResult = measured,
                    topLeft = Offset(pillX + 10f, pillY + 5f),
                )
            }
        }

        // 2. Glowing Cyan Skeleton Connections
        for (conn in PoseRegionMapper.connections) {
            val p1 = landmarks[conn.start] ?: continue
            val p2 = landmarks[conn.end] ?: continue
            if (p1.inFrameLikelihood < 0.4f || p2.inFrameLikelihood < 0.4f) continue

            val isHighlighted = conn.region == selectedRegion ||
                (selectedRegion == BodyRegionId.CHEST && conn.region == BodyRegionId.SHOULDERS) ||
                (selectedRegion == BodyRegionId.ABDOMEN && conn.region == BodyRegionId.CHEST)

            val startOffset = Offset(p1.x, p1.y)
            val endOffset = Offset(p2.x, p2.y)

            // Outer glow aura
            drawLine(
                color = if (isHighlighted) ElectricCyan.copy(alpha = 0.42f) else CobaltBlue.copy(alpha = 0.30f),
                start = startOffset,
                end = endOffset,
                strokeWidth = if (isHighlighted) 14f else 9f,
                cap = StrokeCap.Round,
            )
            // Core electric cyan beam
            drawLine(
                color = if (isHighlighted) ElectricCyanGlow else ElectricCyan.copy(alpha = 0.85f),
                start = startOffset,
                end = endOffset,
                strokeWidth = if (isHighlighted) 5.5f else 3.2f,
                cap = StrokeCap.Round,
            )
        }

        // 3. Central spinal telemetry line (mid-shoulders to mid-hips)
        val lSh = landmarks[PoseLandmark.LEFT_SHOULDER]
        val rSh = landmarks[PoseLandmark.RIGHT_SHOULDER]
        val lHip = landmarks[PoseLandmark.LEFT_HIP]
        val rHip = landmarks[PoseLandmark.RIGHT_HIP]
        if (lSh != null && rSh != null && lHip != null && rHip != null &&
            lSh.inFrameLikelihood >= 0.45f && rSh.inFrameLikelihood >= 0.45f &&
            lHip.inFrameLikelihood >= 0.45f && rHip.inFrameLikelihood >= 0.45f
        ) {
            val midSh = Offset((lSh.x + rSh.x) / 2f, (lSh.y + rSh.y) / 2f)
            val midHip = Offset((lHip.x + rHip.x) / 2f, (lHip.y + rHip.y) / 2f)
            drawLine(
                color = if (selectedRegion == BodyRegionId.CHEST || selectedRegion == BodyRegionId.ABDOMEN) {
                    ElectricCyanGlow
                } else {
                    SkyBlue.copy(alpha = 0.65f)
                },
                start = midSh,
                end = midHip,
                strokeWidth = 3f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f), 0f),
            )
        }

        // 4. Glowing Landmark Nodes
        for ((type, pt) in landmarks) {
            if (pt.inFrameLikelihood < 0.45f) continue
            if (type == PoseLandmark.LEFT_EYE_INNER ||
                type == PoseLandmark.LEFT_EYE_OUTER ||
                type == PoseLandmark.RIGHT_EYE_INNER ||
                type == PoseLandmark.RIGHT_EYE_OUTER
            ) {
                continue
            }

            val isActive = activeLandmarkTypes.contains(type)
            val center = Offset(pt.x, pt.y)
            drawCircle(
                color = ElectricCyan.copy(alpha = if (isActive) 0.45f else 0.28f),
                radius = if (isActive) 15f else 10f,
                center = center,
            )
            drawCircle(
                color = if (isActive) Color.White else ElectricCyan,
                radius = if (isActive) 7f else 4.8f,
                center = center,
            )
            if (isActive) {
                drawCircle(
                    color = ElectricCyan,
                    radius = 12f,
                    center = center,
                    style = Stroke(width = 2f),
                )
            }
        }

        // 5. Region Pills for non-selected tracked regions
        if (showArLabels) {
            for (box in regionBounds) {
                if (box.regionId == selectedRegion) continue
                val label = AnatomyCatalog.getRegion(box.regionId).shortLabel
                val measured = textMeasurer.measure(
                    text = label,
                    style = TextStyle(
                        fontFamily = PlusJakartaSansFontFamily,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFBAE6FD),
                    ),
                )
                val pw = measured.size.width + 16f
                val ph = measured.size.height + 6f
                val px = (box.centerX - pw / 2f).coerceIn(8f, (size.width - pw - 8f).coerceAtLeast(8f))
                val py = (box.top + 6f).coerceIn(8f, (size.height - ph - 8f).coerceAtLeast(8f))

                drawRoundRect(
                    color = DeepNavyBg.copy(alpha = 0.72f),
                    topLeft = Offset(px, py),
                    size = Size(pw, ph),
                    cornerRadius = CornerRadius(ph / 2f, ph / 2f),
                )
                drawRoundRect(
                    color = ElectricCyan.copy(alpha = 0.42f),
                    topLeft = Offset(px, py),
                    size = Size(pw, ph),
                    cornerRadius = CornerRadius(ph / 2f, ph / 2f),
                    style = Stroke(width = 1.5f),
                )
                drawText(
                    textLayoutResult = measured,
                    topLeft = Offset(px + 8f, py + 3f),
                )
            }
        }
    }
}

@Composable
private fun ScannerCornerBrackets(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val len = 24.dp.toPx()
        val stroke = 2.dp.toPx()
        val color = ElectricCyan.copy(alpha = 0.55f)
        val path = Path().apply {
            moveTo(0f, len)
            lineTo(0f, 0f)
            lineTo(len, 0f)

            moveTo(size.width - len, 0f)
            lineTo(size.width, 0f)
            lineTo(size.width, len)

            moveTo(size.width, size.height - len)
            lineTo(size.width, size.height)
            lineTo(size.width - len, size.height)

            moveTo(len, size.height)
            lineTo(0f, size.height)
            lineTo(0f, size.height - len)
        }
        drawPath(path, color = color, style = Stroke(width = stroke, cap = StrokeCap.Square))
    }
}

@Composable
private fun CameraStatusBadge(cameraStatus: CameraStatus) {
    val (label, dotColor) = when (cameraStatus) {
        CameraStatus.IDLE -> stringResource(R.string.status_camera_standby) to TextMuted
        CameraStatus.REQUESTING_PERMISSION -> stringResource(R.string.status_requesting_permission) to AlertAmber
        CameraStatus.STARTING -> stringResource(R.string.status_starting_camera) to AlertAmber
        CameraStatus.ACTIVE -> stringResource(R.string.status_live_tracking) to BioluminescentTeal
        CameraStatus.PERMISSION_DENIED -> stringResource(R.string.status_permission_needed) to CoralRed
        CameraStatus.ERROR -> stringResource(R.string.status_camera_error) to CoralRed
    }

    Surface(
        shape = CircleShape,
        color = DeepNavyBg.copy(alpha = 0.85f),
        border = BorderStroke(1.dp, dotColor.copy(alpha = 0.5f)),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(dotColor),
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
            )
        }
    }
}

@Composable
private fun StandbyOverlay(onStartCamera: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepNavyBg.copy(alpha = 0.72f))
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(ElectricCyan.copy(alpha = 0.12f))
                    .border(1.dp, ElectricCyan.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Videocam,
                    contentDescription = stringResource(R.string.cd_camera_icon),
                    tint = ElectricCyan,
                    modifier = Modifier.size(30.dp),
                )
            }
            Text(
                text = stringResource(R.string.standby_title),
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                textAlign = TextAlign.Center,
            )
            Text(
                text = stringResource(R.string.standby_message),
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(4.dp))
            Button(
                onClick = onStartCamera,
                modifier = Modifier
                    .height(48.dp)
                    .testTag("standby_start_camera_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ElectricCyan,
                    contentColor = OnCyanDark,
                ),
                shape = MaterialTheme.shapes.small,
            ) {
                Text(
                    text = stringResource(R.string.btn_start_camera),
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun LoadingOverlay(title: String, subtitle: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepNavyBg.copy(alpha = 0.82f))
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            CircularProgressIndicator(
                color = ElectricCyan,
                trackColor = ElectricCyan.copy(alpha = 0.18f),
                modifier = Modifier.size(44.dp),
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                textAlign = TextAlign.Center,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun ErrorOverlay(
    title: String,
    message: String,
    onRetry: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepNavyBg.copy(alpha = 0.88f))
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(CoralRed.copy(alpha = 0.15f))
                    .border(1.dp, CoralRed.copy(alpha = 0.55f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.WarningAmber,
                    contentDescription = null,
                    tint = CoralRed,
                    modifier = Modifier.size(28.dp),
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                textAlign = TextAlign.Center,
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center,
            )
            Button(
                onClick = onRetry,
                modifier = Modifier
                    .height(48.dp)
                    .testTag("retry_camera_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ElectricCyan,
                    contentColor = OnCyanDark,
                ),
                shape = MaterialTheme.shapes.small,
            ) {
                Text(
                    text = stringResource(R.string.btn_retry_camera),
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}
