package com.example.pose

import com.example.model.BodyRegionId
import com.example.model.PoseLandmarkPoint
import com.example.model.RegionBounds
import com.google.mlkit.vision.pose.PoseLandmark
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min

data class PoseConnectionDef(
    val start: Int,
    val end: Int,
    val region: BodyRegionId,
)

object PoseRegionMapper {

    val connections: List<PoseConnectionDef> = listOf(
        // Head & Facial Landmarks
        PoseConnectionDef(PoseLandmark.NOSE, PoseLandmark.LEFT_EYE_INNER, BodyRegionId.HEAD),
        PoseConnectionDef(PoseLandmark.LEFT_EYE_INNER, PoseLandmark.LEFT_EYE, BodyRegionId.HEAD),
        PoseConnectionDef(PoseLandmark.LEFT_EYE, PoseLandmark.LEFT_EYE_OUTER, BodyRegionId.HEAD),
        PoseConnectionDef(PoseLandmark.LEFT_EYE_OUTER, PoseLandmark.LEFT_EAR, BodyRegionId.HEAD),
        PoseConnectionDef(PoseLandmark.NOSE, PoseLandmark.RIGHT_EYE_INNER, BodyRegionId.HEAD),
        PoseConnectionDef(PoseLandmark.RIGHT_EYE_INNER, PoseLandmark.RIGHT_EYE, BodyRegionId.HEAD),
        PoseConnectionDef(PoseLandmark.RIGHT_EYE, PoseLandmark.RIGHT_EYE_OUTER, BodyRegionId.HEAD),
        PoseConnectionDef(PoseLandmark.RIGHT_EYE_OUTER, PoseLandmark.RIGHT_EAR, BodyRegionId.HEAD),
        PoseConnectionDef(PoseLandmark.LEFT_MOUTH, PoseLandmark.RIGHT_MOUTH, BodyRegionId.HEAD),

        // Shoulders
        PoseConnectionDef(PoseLandmark.LEFT_SHOULDER, PoseLandmark.RIGHT_SHOULDER, BodyRegionId.SHOULDERS),

        // Chest / Upper Torso
        PoseConnectionDef(PoseLandmark.LEFT_SHOULDER, PoseLandmark.LEFT_HIP, BodyRegionId.CHEST),
        PoseConnectionDef(PoseLandmark.RIGHT_SHOULDER, PoseLandmark.RIGHT_HIP, BodyRegionId.CHEST),

        // Abdomen / Pelvic Girdle
        PoseConnectionDef(PoseLandmark.LEFT_HIP, PoseLandmark.RIGHT_HIP, BodyRegionId.ABDOMEN),

        // Arms
        PoseConnectionDef(PoseLandmark.LEFT_SHOULDER, PoseLandmark.LEFT_ELBOW, BodyRegionId.ARMS),
        PoseConnectionDef(PoseLandmark.LEFT_ELBOW, PoseLandmark.LEFT_WRIST, BodyRegionId.ARMS),
        PoseConnectionDef(PoseLandmark.RIGHT_SHOULDER, PoseLandmark.RIGHT_ELBOW, BodyRegionId.ARMS),
        PoseConnectionDef(PoseLandmark.RIGHT_ELBOW, PoseLandmark.RIGHT_WRIST, BodyRegionId.ARMS),

        // Hands
        PoseConnectionDef(PoseLandmark.LEFT_WRIST, PoseLandmark.LEFT_PINKY, BodyRegionId.HANDS),
        PoseConnectionDef(PoseLandmark.LEFT_WRIST, PoseLandmark.LEFT_INDEX, BodyRegionId.HANDS),
        PoseConnectionDef(PoseLandmark.LEFT_WRIST, PoseLandmark.LEFT_THUMB, BodyRegionId.HANDS),
        PoseConnectionDef(PoseLandmark.LEFT_PINKY, PoseLandmark.LEFT_INDEX, BodyRegionId.HANDS),
        PoseConnectionDef(PoseLandmark.RIGHT_WRIST, PoseLandmark.RIGHT_PINKY, BodyRegionId.HANDS),
        PoseConnectionDef(PoseLandmark.RIGHT_WRIST, PoseLandmark.RIGHT_INDEX, BodyRegionId.HANDS),
        PoseConnectionDef(PoseLandmark.RIGHT_WRIST, PoseLandmark.RIGHT_THUMB, BodyRegionId.HANDS),
        PoseConnectionDef(PoseLandmark.RIGHT_PINKY, PoseLandmark.RIGHT_INDEX, BodyRegionId.HANDS),

        // Legs
        PoseConnectionDef(PoseLandmark.LEFT_HIP, PoseLandmark.LEFT_KNEE, BodyRegionId.LEGS),
        PoseConnectionDef(PoseLandmark.LEFT_KNEE, PoseLandmark.LEFT_ANKLE, BodyRegionId.LEGS),
        PoseConnectionDef(PoseLandmark.RIGHT_HIP, PoseLandmark.RIGHT_KNEE, BodyRegionId.LEGS),
        PoseConnectionDef(PoseLandmark.RIGHT_KNEE, PoseLandmark.RIGHT_ANKLE, BodyRegionId.LEGS),

        // Feet
        PoseConnectionDef(PoseLandmark.LEFT_ANKLE, PoseLandmark.LEFT_HEEL, BodyRegionId.FEET),
        PoseConnectionDef(PoseLandmark.LEFT_ANKLE, PoseLandmark.LEFT_FOOT_INDEX, BodyRegionId.FEET),
        PoseConnectionDef(PoseLandmark.LEFT_HEEL, PoseLandmark.LEFT_FOOT_INDEX, BodyRegionId.FEET),
        PoseConnectionDef(PoseLandmark.RIGHT_ANKLE, PoseLandmark.RIGHT_HEEL, BodyRegionId.FEET),
        PoseConnectionDef(PoseLandmark.RIGHT_ANKLE, PoseLandmark.RIGHT_FOOT_INDEX, BodyRegionId.FEET),
        PoseConnectionDef(PoseLandmark.RIGHT_HEEL, PoseLandmark.RIGHT_FOOT_INDEX, BodyRegionId.FEET),
    )

    val regionLandmarkIndices: Map<BodyRegionId, Set<Int>> = mapOf(
        BodyRegionId.HEAD to setOf(
            PoseLandmark.NOSE,
            PoseLandmark.LEFT_EYE_INNER,
            PoseLandmark.LEFT_EYE,
            PoseLandmark.LEFT_EYE_OUTER,
            PoseLandmark.RIGHT_EYE_INNER,
            PoseLandmark.RIGHT_EYE,
            PoseLandmark.RIGHT_EYE_OUTER,
            PoseLandmark.LEFT_EAR,
            PoseLandmark.RIGHT_EAR,
            PoseLandmark.LEFT_MOUTH,
            PoseLandmark.RIGHT_MOUTH,
        ),
        BodyRegionId.SHOULDERS to setOf(PoseLandmark.LEFT_SHOULDER, PoseLandmark.RIGHT_SHOULDER),
        BodyRegionId.CHEST to setOf(
            PoseLandmark.LEFT_SHOULDER,
            PoseLandmark.RIGHT_SHOULDER,
            PoseLandmark.LEFT_HIP,
            PoseLandmark.RIGHT_HIP,
        ),
        BodyRegionId.ARMS to setOf(
            PoseLandmark.LEFT_SHOULDER,
            PoseLandmark.RIGHT_SHOULDER,
            PoseLandmark.LEFT_ELBOW,
            PoseLandmark.RIGHT_ELBOW,
            PoseLandmark.LEFT_WRIST,
            PoseLandmark.RIGHT_WRIST,
        ),
        BodyRegionId.HANDS to setOf(
            PoseLandmark.LEFT_WRIST,
            PoseLandmark.RIGHT_WRIST,
            PoseLandmark.LEFT_PINKY,
            PoseLandmark.RIGHT_PINKY,
            PoseLandmark.LEFT_INDEX,
            PoseLandmark.RIGHT_INDEX,
            PoseLandmark.LEFT_THUMB,
            PoseLandmark.RIGHT_THUMB,
        ),
        BodyRegionId.ABDOMEN to setOf(
            PoseLandmark.LEFT_SHOULDER,
            PoseLandmark.RIGHT_SHOULDER,
            PoseLandmark.LEFT_HIP,
            PoseLandmark.RIGHT_HIP,
        ),
        BodyRegionId.LEGS to setOf(
            PoseLandmark.LEFT_HIP,
            PoseLandmark.RIGHT_HIP,
            PoseLandmark.LEFT_KNEE,
            PoseLandmark.RIGHT_KNEE,
            PoseLandmark.LEFT_ANKLE,
            PoseLandmark.RIGHT_ANKLE,
        ),
        BodyRegionId.FEET to setOf(
            PoseLandmark.LEFT_ANKLE,
            PoseLandmark.RIGHT_ANKLE,
            PoseLandmark.LEFT_HEEL,
            PoseLandmark.RIGHT_HEEL,
            PoseLandmark.LEFT_FOOT_INDEX,
            PoseLandmark.RIGHT_FOOT_INDEX,
        ),
    )

    fun computeRegionBounds(
        landmarks: Map<Int, PoseLandmarkPoint>,
        viewWidth: Float,
        viewHeight: Float,
    ): List<RegionBounds> {
        if (landmarks.isEmpty() || viewWidth <= 0f || viewHeight <= 0f) return emptyList()

        fun visible(type: Int, minLikelihood: Float = 0.45f): PoseLandmarkPoint? {
            val pt = landmarks[type] ?: return null
            if (pt.inFrameLikelihood < minLikelihood) return null
            if (pt.x < -30f || pt.x > viewWidth + 30f || pt.y < -30f || pt.y > viewHeight + 30f) {
                return null
            }
            return pt
        }

        val result = mutableListOf<RegionBounds>()

        fun addBox(
            regionId: BodyRegionId,
            points: List<PoseLandmarkPoint>,
            padX: Float,
            padY: Float,
        ) {
            if (points.isEmpty()) return
            val minX = points.minOf { it.x }
            val maxX = points.maxOf { it.x }
            val minY = points.minOf { it.y }
            val maxY = points.maxOf { it.y }

            val left = max(8f, minX - padX)
            val top = max(8f, minY - padY)
            val right = min(viewWidth - 8f, maxX + padX)
            val bottom = min(viewHeight - 8f, maxY + padY)
            if (right - left <= 12f || bottom - top <= 12f) return

            val avgConfidence = points.map { it.inFrameLikelihood }.average().toFloat()
            result.add(
                RegionBounds(
                    regionId = regionId,
                    left = left,
                    top = top,
                    right = right,
                    bottom = bottom,
                    confidence = avgConfidence,
                ),
            )
        }

        // 1. Head
        val headPts = listOf(
            PoseLandmark.NOSE,
            PoseLandmark.LEFT_EYE,
            PoseLandmark.RIGHT_EYE,
            PoseLandmark.LEFT_EAR,
            PoseLandmark.RIGHT_EAR,
            PoseLandmark.LEFT_MOUTH,
            PoseLandmark.RIGHT_MOUTH,
        ).mapNotNull { visible(it, 0.5f) }
        if (headPts.size >= 3) {
            addBox(BodyRegionId.HEAD, headPts, padX = 36f, padY = 44f)
        }

        // 2. Shoulders
        val lSh = visible(PoseLandmark.LEFT_SHOULDER, 0.5f)
        val rSh = visible(PoseLandmark.RIGHT_SHOULDER, 0.5f)
        if (lSh != null && rSh != null) {
            val shoulderSpan = hypot((rSh.x - lSh.x).toDouble(), (rSh.y - lSh.y).toDouble()).toFloat()
            addBox(
                BodyRegionId.SHOULDERS,
                listOf(lSh, rSh),
                padX = 30f,
                padY = max(24f, shoulderSpan * 0.14f),
            )
        }

        // 3. Chest & 6. Abdomen
        val lHip = visible(PoseLandmark.LEFT_HIP, 0.4f)
        val rHip = visible(PoseLandmark.RIGHT_HIP, 0.4f)
        if (lSh != null && rSh != null && lHip != null && rHip != null) {
            fun lerp(a: PoseLandmarkPoint, b: PoseLandmarkPoint, t: Float): PoseLandmarkPoint {
                return PoseLandmarkPoint(
                    type = a.type,
                    x = a.x + (b.x - a.x) * t,
                    y = a.y + (b.y - a.y) * t,
                    inFrameLikelihood = (a.inFrameLikelihood + b.inFrameLikelihood) / 2f,
                )
            }
            addBox(
                BodyRegionId.CHEST,
                listOf(
                    lerp(lSh, lHip, 0.08f),
                    lerp(rSh, rHip, 0.08f),
                    lerp(lSh, lHip, 0.48f),
                    lerp(rSh, rHip, 0.48f),
                ),
                padX = 18f,
                padY = 12f,
            )
            addBox(
                BodyRegionId.ABDOMEN,
                listOf(
                    lerp(lSh, lHip, 0.52f),
                    lerp(rSh, rHip, 0.52f),
                    lerp(lSh, lHip, 0.98f),
                    lerp(rSh, rHip, 0.98f),
                ),
                padX = 18f,
                padY = 14f,
            )
        } else if (lSh != null && rSh != null) {
            val shoulderSpan = hypot((rSh.x - lSh.x).toDouble(), (rSh.y - lSh.y).toDouble()).toFloat()
            val estBottomY = min(viewHeight - 12f, max(lSh.y, rSh.y) + shoulderSpan * 0.65f)
            addBox(
                BodyRegionId.CHEST,
                listOf(
                    lSh,
                    rSh,
                    lSh.copy(y = estBottomY),
                    rSh.copy(y = estBottomY),
                ),
                padX = 14f,
                padY = 14f,
            )
        }

        // 4. Arms
        val leftArm = listOf(
            PoseLandmark.LEFT_SHOULDER,
            PoseLandmark.LEFT_ELBOW,
            PoseLandmark.LEFT_WRIST,
        ).mapNotNull { visible(it, 0.45f) }
        val rightArm = listOf(
            PoseLandmark.RIGHT_SHOULDER,
            PoseLandmark.RIGHT_ELBOW,
            PoseLandmark.RIGHT_WRIST,
        ).mapNotNull { visible(it, 0.45f) }
        val hasElbow = visible(PoseLandmark.LEFT_ELBOW, 0.45f) != null ||
            visible(PoseLandmark.RIGHT_ELBOW, 0.45f) != null
        val primaryArm = if (leftArm.size >= rightArm.size) leftArm else rightArm
        if (hasElbow && primaryArm.size >= 2) {
            addBox(BodyRegionId.ARMS, primaryArm, padX = 28f, padY = 24f)
        }

        // 5. Hands
        val leftHand = listOf(
            PoseLandmark.LEFT_WRIST,
            PoseLandmark.LEFT_PINKY,
            PoseLandmark.LEFT_INDEX,
            PoseLandmark.LEFT_THUMB,
        ).mapNotNull { visible(it, 0.45f) }
        val rightHand = listOf(
            PoseLandmark.RIGHT_WRIST,
            PoseLandmark.RIGHT_PINKY,
            PoseLandmark.RIGHT_INDEX,
            PoseLandmark.RIGHT_THUMB,
        ).mapNotNull { visible(it, 0.45f) }
        val primaryHand = if (leftHand.size >= rightHand.size) leftHand else rightHand
        if (primaryHand.size >= 2) {
            addBox(BodyRegionId.HANDS, primaryHand, padX = 26f, padY = 26f)
        }

        // 7. Legs
        val legPts = listOf(
            PoseLandmark.LEFT_HIP,
            PoseLandmark.RIGHT_HIP,
            PoseLandmark.LEFT_KNEE,
            PoseLandmark.RIGHT_KNEE,
            PoseLandmark.LEFT_ANKLE,
            PoseLandmark.RIGHT_ANKLE,
        ).mapNotNull { visible(it, 0.4f) }
        val hasKnee = visible(PoseLandmark.LEFT_KNEE, 0.4f) != null ||
            visible(PoseLandmark.RIGHT_KNEE, 0.4f) != null
        if (hasKnee && legPts.size >= 3) {
            addBox(BodyRegionId.LEGS, legPts, padX = 28f, padY = 22f)
        }

        // 8. Feet
        val footPts = listOf(
            PoseLandmark.LEFT_ANKLE,
            PoseLandmark.RIGHT_ANKLE,
            PoseLandmark.LEFT_HEEL,
            PoseLandmark.RIGHT_HEEL,
            PoseLandmark.LEFT_FOOT_INDEX,
            PoseLandmark.RIGHT_FOOT_INDEX,
        ).mapNotNull { visible(it, 0.4f) }
        if (footPts.size >= 2) {
            addBox(BodyRegionId.FEET, footPts, padX = 24f, padY = 20f)
        }

        return result
    }
}
