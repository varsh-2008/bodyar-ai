package com.example.model

import androidx.compose.ui.graphics.Color

enum class BodyRegionId {
    HEAD,
    SHOULDERS,
    CHEST,
    ARMS,
    HANDS,
    ABDOMEN,
    LEGS,
    FEET,
}

data class AnatomySubgroup(
    val category: String,
    val items: List<String>,
)

data class BodyRegionInfo(
    val id: BodyRegionId,
    val name: String,
    val clinicalName: String,
    val shortLabel: String,
    val summary: String,
    val associatedAnatomy: List<AnatomySubgroup>,
    val functions: List<String>,
    val interestingFacts: List<String>,
    val relatedSystems: List<String>,
    val accentColor: Color,
)

enum class CameraStatus {
    IDLE,
    REQUESTING_PERMISSION,
    PERMISSION_DENIED,
    STARTING,
    ACTIVE,
    ERROR,
}

data class PoseLandmarkPoint(
    val type: Int,
    val x: Float,
    val y: Float,
    val inFrameLikelihood: Float,
)

data class RegionBounds(
    val regionId: BodyRegionId,
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float,
    val confidence: Float,
) {
    val width: Float get() = (right - left).coerceAtLeast(0f)
    val height: Float get() = (bottom - top).coerceAtLeast(0f)
    val centerX: Float get() = (left + right) / 2f
    val centerY: Float get() = (top + bottom) / 2f

    fun contains(px: Float, py: Float): Boolean {
        return px in left..right && py in top..bottom
    }
}

data class PoseTelemetry(
    val personDetected: Boolean = false,
    val landmarkCount: Int = 0,
    val fps: Int = 0,
    val trackedRegions: Set<BodyRegionId> = emptySet(),
)

data class ChatMessage(
    val id: String,
    val isUser: Boolean,
    val content: String,
    val timestamp: String,
    val suggestedRegion: BodyRegionId? = null,
)
