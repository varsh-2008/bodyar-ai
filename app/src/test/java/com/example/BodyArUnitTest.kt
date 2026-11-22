package com.example

import com.example.data.AnatomyCatalog
import com.example.data.BuiltInAnatomyRepository
import com.example.model.BodyRegionId
import com.example.model.CameraStatus
import com.example.model.PoseLandmarkPoint
import com.example.pose.PoseRegionMapper
import com.example.viewmodel.BodyArViewModel
import com.google.mlkit.vision.pose.PoseLandmark
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BodyArUnitTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun catalog_hasAllEightRegionsWithCompleteData() {
        assertEquals(8, AnatomyCatalog.regionOrder.size)
        for (regionId in BodyRegionId.entries) {
            val info = AnatomyCatalog.getRegion(regionId)
            assertTrue(info.name.isNotBlank())
            assertTrue(info.associatedAnatomy.isNotEmpty())
            assertTrue(info.functions.isNotEmpty())
            assertTrue(info.interestingFacts.isNotEmpty())
            assertTrue(info.relatedSystems.isNotEmpty())
        }
    }

    @Test
    fun builtInRepository_answersTopicAndContextQueries() = runTest {
        val repo = BuiltInAnatomyRepository()

        val heartAnswer = repo.askAnatomyQuestion("How does the heart pump blood?", BodyRegionId.HEAD)
        assertTrue(heartAnswer.content.contains("heart", ignoreCase = true))
        assertEquals(BodyRegionId.CHEST, heartAnswer.suggestedRegion)

        val contextFact = repo.askAnatomyQuestion("Tell me an interesting fact about this region", BodyRegionId.HANDS)
        assertTrue(contextFact.content.contains("Hands", ignoreCase = true))
        assertEquals(BodyRegionId.HANDS, contextFact.suggestedRegion)
    }

    @Test
    fun poseRegionMapper_computesAllEightBoundsWhenFullBodyVisible() {
        val landmarks = mapOf(
            PoseLandmark.NOSE to PoseLandmarkPoint(PoseLandmark.NOSE, 500f, 120f, 0.95f),
            PoseLandmark.LEFT_EYE to PoseLandmarkPoint(PoseLandmark.LEFT_EYE, 485f, 108f, 0.95f),
            PoseLandmark.RIGHT_EYE to PoseLandmarkPoint(PoseLandmark.RIGHT_EYE, 515f, 108f, 0.95f),
            PoseLandmark.LEFT_EAR to PoseLandmarkPoint(PoseLandmark.LEFT_EAR, 468f, 115f, 0.9f),
            PoseLandmark.RIGHT_EAR to PoseLandmarkPoint(PoseLandmark.RIGHT_EAR, 532f, 115f, 0.9f),
            PoseLandmark.LEFT_SHOULDER to PoseLandmarkPoint(PoseLandmark.LEFT_SHOULDER, 430f, 240f, 0.96f),
            PoseLandmark.RIGHT_SHOULDER to PoseLandmarkPoint(PoseLandmark.RIGHT_SHOULDER, 570f, 240f, 0.96f),
            PoseLandmark.LEFT_ELBOW to PoseLandmarkPoint(PoseLandmark.LEFT_ELBOW, 390f, 360f, 0.92f),
            PoseLandmark.RIGHT_ELBOW to PoseLandmarkPoint(PoseLandmark.RIGHT_ELBOW, 610f, 360f, 0.92f),
            PoseLandmark.LEFT_WRIST to PoseLandmarkPoint(PoseLandmark.LEFT_WRIST, 360f, 470f, 0.91f),
            PoseLandmark.RIGHT_WRIST to PoseLandmarkPoint(PoseLandmark.RIGHT_WRIST, 640f, 470f, 0.91f),
            PoseLandmark.LEFT_INDEX to PoseLandmarkPoint(PoseLandmark.LEFT_INDEX, 350f, 510f, 0.88f),
            PoseLandmark.RIGHT_INDEX to PoseLandmarkPoint(PoseLandmark.RIGHT_INDEX, 650f, 510f, 0.88f),
            PoseLandmark.LEFT_HIP to PoseLandmarkPoint(PoseLandmark.LEFT_HIP, 450f, 520f, 0.95f),
            PoseLandmark.RIGHT_HIP to PoseLandmarkPoint(PoseLandmark.RIGHT_HIP, 550f, 520f, 0.95f),
            PoseLandmark.LEFT_KNEE to PoseLandmarkPoint(PoseLandmark.LEFT_KNEE, 455f, 700f, 0.9f),
            PoseLandmark.RIGHT_KNEE to PoseLandmarkPoint(PoseLandmark.RIGHT_KNEE, 545f, 700f, 0.9f),
            PoseLandmark.LEFT_ANKLE to PoseLandmarkPoint(PoseLandmark.LEFT_ANKLE, 458f, 880f, 0.88f),
            PoseLandmark.RIGHT_ANKLE to PoseLandmarkPoint(PoseLandmark.RIGHT_ANKLE, 542f, 880f, 0.88f),
            PoseLandmark.LEFT_HEEL to PoseLandmarkPoint(PoseLandmark.LEFT_HEEL, 452f, 905f, 0.85f),
            PoseLandmark.RIGHT_HEEL to PoseLandmarkPoint(PoseLandmark.RIGHT_HEEL, 548f, 905f, 0.85f),
        )

        val bounds = PoseRegionMapper.computeRegionBounds(landmarks, 1000f, 1000f)
        val regionIds = bounds.map { it.regionId }.toSet()

        assertEquals(BodyRegionId.entries.toSet(), regionIds)
    }

    @Test
    fun viewModel_updatesStateOnRegionCameraAndChat() = runTest {
        val vm = BodyArViewModel()

        assertEquals(BodyRegionId.CHEST, vm.uiState.value.selectedRegion)
        assertEquals(CameraStatus.IDLE, vm.uiState.value.cameraStatus)

        vm.selectRegion(BodyRegionId.HEAD)
        assertEquals(BodyRegionId.HEAD, vm.uiState.value.selectedRegion)

        vm.onStartCameraRequested(hasPermission = true)
        assertEquals(CameraStatus.STARTING, vm.uiState.value.cameraStatus)

        vm.onCameraReady()
        assertEquals(CameraStatus.ACTIVE, vm.uiState.value.cameraStatus)

        vm.sendMessage("Tell me about the brain")
        advanceUntilIdle()

        assertFalse(vm.uiState.value.isAssistantThinking)
        assertEquals(3, vm.uiState.value.messages.size)
        assertTrue(vm.uiState.value.messages.last().content.contains("brain", ignoreCase = true))

        vm.stopCamera()
        assertEquals(CameraStatus.IDLE, vm.uiState.value.cameraStatus)
    }
}
