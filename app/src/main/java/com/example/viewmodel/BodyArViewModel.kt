package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AiRepository
import com.example.data.BuiltInAnatomyRepository
import com.example.model.BodyRegionId
import com.example.model.CameraStatus
import com.example.model.ChatMessage
import com.example.model.PoseLandmarkPoint
import com.example.model.PoseTelemetry
import com.example.model.RegionBounds
import com.example.pose.PoseRegionMapper
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class BodyArUiState(
    val selectedRegion: BodyRegionId = BodyRegionId.CHEST,
    val cameraStatus: CameraStatus = CameraStatus.IDLE,
    val cameraErrorMessage: String? = null,
    val useFrontCamera: Boolean = true,
    val showArLabels: Boolean = true,
    val telemetry: PoseTelemetry = PoseTelemetry(),
    val landmarks: Map<Int, PoseLandmarkPoint> = emptyMap(),
    val regionBounds: List<RegionBounds> = emptyList(),
    val messages: List<ChatMessage> = emptyList(),
    val isAssistantThinking: Boolean = false,
)

class BodyArViewModel(
    private val aiRepository: AiRepository = BuiltInAnatomyRepository(),
) : ViewModel() {

    private val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
    private val frameTimestamps = ArrayDeque<Long>()

    private val _uiState = MutableStateFlow(
        BodyArUiState(
            messages = listOf(
                ChatMessage(
                    id = "welcome-1",
                    isUser = false,
                    content = "Welcome to **BodyAR AI**! Tap **Start Camera** to track 33 skeletal landmarks in real time, select any body region on the hologram or video feed, and ask me anything about human anatomy.",
                    timestamp = currentTimeLabel(),
                ),
            ),
        ),
    )
    val uiState: StateFlow<BodyArUiState> = _uiState.asStateFlow()

    fun selectRegion(regionId: BodyRegionId) {
        _uiState.update { it.copy(selectedRegion = regionId) }
    }

    fun onStartCameraRequested(hasPermission: Boolean) {
        if (hasPermission) {
            _uiState.update {
                it.copy(
                    cameraStatus = CameraStatus.STARTING,
                    cameraErrorMessage = null,
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    cameraStatus = CameraStatus.REQUESTING_PERMISSION,
                    cameraErrorMessage = null,
                )
            }
        }
    }

    fun onPermissionResult(granted: Boolean) {
        if (granted) {
            _uiState.update {
                it.copy(
                    cameraStatus = CameraStatus.STARTING,
                    cameraErrorMessage = null,
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    cameraStatus = CameraStatus.PERMISSION_DENIED,
                    cameraErrorMessage = "Camera permission is required for live AR body tracking.",
                )
            }
        }
    }

    fun onCameraReady() {
        _uiState.update {
            if (it.cameraStatus == CameraStatus.STARTING || it.cameraStatus == CameraStatus.REQUESTING_PERMISSION) {
                it.copy(cameraStatus = CameraStatus.ACTIVE, cameraErrorMessage = null)
            } else {
                it
            }
        }
    }

    fun onCameraError(message: String) {
        frameTimestamps.clear()
        _uiState.update {
            it.copy(
                cameraStatus = CameraStatus.ERROR,
                cameraErrorMessage = message,
                landmarks = emptyMap(),
                regionBounds = emptyList(),
                telemetry = PoseTelemetry(),
            )
        }
    }

    fun stopCamera() {
        frameTimestamps.clear()
        _uiState.update {
            it.copy(
                cameraStatus = CameraStatus.IDLE,
                cameraErrorMessage = null,
                landmarks = emptyMap(),
                regionBounds = emptyList(),
                telemetry = PoseTelemetry(),
            )
        }
    }

    fun toggleCameraLens() {
        _uiState.update {
            it.copy(
                useFrontCamera = !it.useFrontCamera,
                landmarks = emptyMap(),
                regionBounds = emptyList(),
            )
        }
    }

    fun toggleArLabels() {
        _uiState.update { it.copy(showArLabels = !it.showArLabels) }
    }

    fun onPoseFrame(
        landmarks: Map<Int, PoseLandmarkPoint>,
        viewWidth: Float,
        viewHeight: Float,
    ) {
        val now = System.currentTimeMillis()
        frameTimestamps.addLast(now)
        while (frameTimestamps.isNotEmpty() && now - frameTimestamps.first() > 1000L) {
            frameTimestamps.removeFirst()
        }

        val detectedCount = landmarks.values.count { it.inFrameLikelihood >= 0.45f }
        val personDetected = detectedCount >= 4
        val bounds = if (personDetected) {
            PoseRegionMapper.computeRegionBounds(landmarks, viewWidth, viewHeight)
        } else {
            emptyList()
        }

        _uiState.update { state ->
            if (state.cameraStatus != CameraStatus.ACTIVE && state.cameraStatus != CameraStatus.STARTING) {
                return@update state
            }
            state.copy(
                cameraStatus = CameraStatus.ACTIVE,
                landmarks = if (personDetected) landmarks else emptyMap(),
                regionBounds = bounds,
                telemetry = PoseTelemetry(
                    personDetected = personDetected,
                    landmarkCount = detectedCount,
                    fps = frameTimestamps.size,
                    trackedRegions = bounds.map { it.regionId }.toSet(),
                ),
            )
        }
    }

    fun sendMessage(rawText: String) {
        val trimmed = rawText.trim()
        if (trimmed.isEmpty() || _uiState.value.isAssistantThinking) return

        val userMessage = ChatMessage(
            id = "u-${System.currentTimeMillis()}",
            isUser = true,
            content = trimmed,
            timestamp = currentTimeLabel(),
        )
        val activeRegion = _uiState.value.selectedRegion

        _uiState.update {
            it.copy(
                messages = it.messages + userMessage,
                isAssistantThinking = true,
            )
        }

        viewModelScope.launch {
            try {
                val answer = aiRepository.askAnatomyQuestion(trimmed, activeRegion)
                val assistantMessage = ChatMessage(
                    id = "a-${System.currentTimeMillis()}",
                    isUser = false,
                    content = answer.content,
                    timestamp = currentTimeLabel(),
                    suggestedRegion = answer.suggestedRegion,
                )
                _uiState.update {
                    it.copy(
                        messages = it.messages + assistantMessage,
                        isAssistantThinking = false,
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                val fallback = ChatMessage(
                    id = "a-${System.currentTimeMillis()}",
                    isUser = false,
                    content = "I could not process that query right now. Try selecting a body region in the explorer or asking about a specific organ or bone.",
                    timestamp = currentTimeLabel(),
                )
                _uiState.update {
                    it.copy(
                        messages = it.messages + fallback,
                        isAssistantThinking = false,
                    )
                }
            }
        }
    }

    private fun currentTimeLabel(): String = timeFormatter.format(Date())
}
