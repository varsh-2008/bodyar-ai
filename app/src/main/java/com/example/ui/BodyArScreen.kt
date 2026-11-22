package com.example.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.R
import com.example.data.AnatomyCatalog
import com.example.model.CameraStatus
import com.example.ui.components.AssistantPanelCard
import com.example.ui.components.BodyExplorerCard
import com.example.ui.components.CameraStageCard
import com.example.ui.theme.CobaltBlue
import com.example.ui.theme.DeepNavyBg
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.GlassBorderCyan
import com.example.ui.theme.NavyCardSurface
import com.example.ui.theme.SkyBlue
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.BodyArUiState
import com.example.viewmodel.BodyArViewModel

@Composable
fun BodyArScreen(
    viewModel: BodyArViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        viewModel.onPermissionResult(granted)
    }

    fun requestCameraStart() {
        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA,
        ) == PackageManager.PERMISSION_GRANTED
        viewModel.onStartCameraRequested(hasPermission = granted)
        if (!granted) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Scaffold(
        containerColor = DeepNavyBg,
        contentWindowInsets = WindowInsets.safeDrawing,
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            DeepNavyBg,
                            Color(0xFF061329),
                            DeepNavyBg,
                        ),
                    ),
                )
                .padding(innerPadding),
        ) {
            // Subtle HUD Grid Background
            HudGridBackground(modifier = Modifier.fillMaxSize())

            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 14.dp),
            ) {
                val isWideLayout = maxWidth >= 840.dp

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    TopDashboardHeader(uiState = uiState)

                    if (isWideLayout) {
                        // Wide / Tablet / Landscape Layout: Left Camera, Right Body Explorer, Bottom AI Chat
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.Top,
                        ) {
                            CameraStageCard(
                                selectedRegion = uiState.selectedRegion,
                                cameraStatus = uiState.cameraStatus,
                                cameraErrorMessage = uiState.cameraErrorMessage,
                                useFrontCamera = uiState.useFrontCamera,
                                showArLabels = uiState.showArLabels,
                                telemetry = uiState.telemetry,
                                landmarks = uiState.landmarks,
                                regionBounds = uiState.regionBounds,
                                onStartCamera = ::requestCameraStart,
                                onStopCamera = viewModel::stopCamera,
                                onToggleLens = viewModel::toggleCameraLens,
                                onToggleArLabels = viewModel::toggleArLabels,
                                onSelectRegion = viewModel::selectRegion,
                                onCameraReady = viewModel::onCameraReady,
                                onCameraError = viewModel::onCameraError,
                                onPoseFrame = viewModel::onPoseFrame,
                                modifier = Modifier.weight(1.05f),
                            )

                            BodyExplorerCard(
                                selectedRegion = uiState.selectedRegion,
                                telemetry = uiState.telemetry,
                                onSelectRegion = viewModel::selectRegion,
                                onAskAiAboutRegion = viewModel::sendMessage,
                                modifier = Modifier.weight(1f),
                            )
                        }
                    } else {
                        // Phone / Compact Vertical Layout
                        CameraStageCard(
                            selectedRegion = uiState.selectedRegion,
                            cameraStatus = uiState.cameraStatus,
                            cameraErrorMessage = uiState.cameraErrorMessage,
                            useFrontCamera = uiState.useFrontCamera,
                            showArLabels = uiState.showArLabels,
                            telemetry = uiState.telemetry,
                            landmarks = uiState.landmarks,
                            regionBounds = uiState.regionBounds,
                            onStartCamera = ::requestCameraStart,
                            onStopCamera = viewModel::stopCamera,
                            onToggleLens = viewModel::toggleCameraLens,
                            onToggleArLabels = viewModel::toggleArLabels,
                            onSelectRegion = viewModel::selectRegion,
                            onCameraReady = viewModel::onCameraReady,
                            onCameraError = viewModel::onCameraError,
                            onPoseFrame = viewModel::onPoseFrame,
                            modifier = Modifier.fillMaxWidth(),
                        )

                        BodyExplorerCard(
                            selectedRegion = uiState.selectedRegion,
                            telemetry = uiState.telemetry,
                            onSelectRegion = viewModel::selectRegion,
                            onAskAiAboutRegion = viewModel::sendMessage,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }

                    // Bottom: BodyAR AI Assistant Panel
                    AssistantPanelCard(
                        selectedRegion = uiState.selectedRegion,
                        messages = uiState.messages,
                        isThinking = uiState.isAssistantThinking,
                        onSendMessage = viewModel::sendMessage,
                        onSelectRegion = viewModel::selectRegion,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TopDashboardHeader(uiState: BodyArUiState) {
    val activeRegionName = AnatomyCatalog.getRegion(uiState.selectedRegion).name
    val landmarkLabel = if (uiState.telemetry.personDetected) {
        "${uiState.telemetry.landmarkCount}/33"
    } else {
        "0/33"
    }
    val fpsLabel = if (uiState.cameraStatus == CameraStatus.ACTIVE) {
        "${uiState.telemetry.fps} FPS"
    } else {
        "—"
    }

    Surface(
        shape = MaterialTheme.shapes.large,
        color = NavyCardSurface.copy(alpha = 0.85f),
        border = BorderStroke(1.dp, GlassBorderCyan),
        tonalElevation = 4.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        FlowRow(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                BrandMark()
                Column {
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.headlineSmall,
                        color = TextPrimary,
                    )
                    Text(
                        text = stringResource(R.string.app_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TelemetryChip(label = "ACTIVE REGION", value = activeRegionName)
                TelemetryChip(label = "LANDMARKS", value = landmarkLabel)
                TelemetryChip(label = "RATE", value = fpsLabel)
            }
        }
    }
}

@Composable
private fun BrandMark() {
    Box(
        modifier = Modifier
            .size(46.dp)
            .clip(MaterialTheme.shapes.small)
            .background(ElectricCyan.copy(alpha = 0.1f))
            .border(1.dp, ElectricCyan.copy(alpha = 0.45f), MaterialTheme.shapes.small),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(30.dp)) {
            val c = Offset(size.width / 2f, size.height / 2f)
            drawCircle(
                brush = Brush.linearGradient(listOf(ElectricCyan, CobaltBlue)),
                radius = size.minDimension / 2.2f,
                center = c,
                style = Stroke(width = 2.2.dp.toPx()),
            )
            // Stylized anatomical node figure
            drawCircle(
                color = ElectricCyan,
                radius = 3.dp.toPx(),
                center = Offset(c.x, size.height * 0.29f),
            )
            drawLine(
                color = ElectricCyan,
                start = Offset(size.width * 0.28f, size.height * 0.47f),
                end = Offset(size.width * 0.72f, size.height * 0.47f),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round,
            )
            drawLine(
                color = ElectricCyan,
                start = Offset(c.x, size.height * 0.39f),
                end = Offset(c.x, size.height * 0.66f),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round,
            )
            drawLine(
                color = SkyBlue,
                start = Offset(c.x, size.height * 0.66f),
                end = Offset(size.width * 0.36f, size.height * 0.84f),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round,
            )
            drawLine(
                color = SkyBlue,
                start = Offset(c.x, size.height * 0.66f),
                end = Offset(size.width * 0.64f, size.height * 0.84f),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round,
            )
        }
    }
}

@Composable
private fun TelemetryChip(
    label: String,
    value: String,
) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = DeepNavyBg.copy(alpha = 0.82f),
        border = BorderStroke(1.dp, GlassBorderCyan.copy(alpha = 0.6f)),
        modifier = Modifier.widthIn(min = 84.dp),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.labelMedium,
                color = ElectricCyan,
            )
        }
    }
}

@Composable
private fun HudGridBackground(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val step = 42.dp.toPx()
        val lineColor = ElectricCyan.copy(alpha = 0.04f)
        var x = 0f
        while (x <= size.width) {
            drawLine(
                color = lineColor,
                start = Offset(x, 0f),
                end = Offset(x, size.height),
                strokeWidth = 1f,
            )
            x += step
        }
        var y = 0f
        while (y <= size.height) {
            drawLine(
                color = lineColor,
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 1f,
            )
            y += step
        }
    }
}
