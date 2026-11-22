package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.AnatomyCatalog
import com.example.model.BodyRegionId
import com.example.model.BodyRegionInfo
import com.example.model.PoseTelemetry
import com.example.ui.theme.BioluminescentTeal
import com.example.ui.theme.CobaltBlue
import com.example.ui.theme.DeepNavyBg
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.GlassBorderCyan
import com.example.ui.theme.NavyCardElevated
import com.example.ui.theme.NavyCardSurface
import com.example.ui.theme.SkyBlue
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BodyExplorerCard(
    selectedRegion: BodyRegionId,
    telemetry: PoseTelemetry,
    onSelectRegion: (BodyRegionId) -> Unit,
    onAskAiAboutRegion: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val region = AnatomyCatalog.getRegion(selectedRegion)
    val isLiveTracked = telemetry.trackedRegions.contains(selectedRegion)

    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        color = NavyCardSurface.copy(alpha = 0.85f),
        border = BorderStroke(1.dp, GlassBorderCyan),
        tonalElevation = 4.dp,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.explorer_eyebrow),
                        style = MaterialTheme.typography.labelMedium,
                        color = ElectricCyan,
                    )
                    Text(
                        text = stringResource(R.string.explorer_title),
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = DeepNavyBg.copy(alpha = 0.85f),
                    border = BorderStroke(
                        1.dp,
                        if (isLiveTracked) BioluminescentTeal.copy(alpha = 0.6f) else GlassBorderCyan,
                    ),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (isLiveTracked) BioluminescentTeal else TextMuted),
                        )
                        Text(
                            text = if (isLiveTracked) {
                                stringResource(R.string.badge_detected_in_frame)
                            } else {
                                stringResource(R.string.badge_atlas_preview)
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isLiveTracked) BioluminescentTeal else TextSecondary,
                        )
                    }
                }
            }

            // Clickable Body Region Selector Buttons
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                AnatomyCatalog.regionOrder.forEach { regionId ->
                    val item = AnatomyCatalog.getRegion(regionId)
                    val isSelected = regionId == selectedRegion
                    val isTracked = telemetry.trackedRegions.contains(regionId)

                    Surface(
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .clip(MaterialTheme.shapes.small)
                            .clickable { onSelectRegion(regionId) }
                            .testTag("region_button_${regionId.name.lowercase()}"),
                        shape = MaterialTheme.shapes.small,
                        color = if (isSelected) {
                            ElectricCyan.copy(alpha = 0.22f)
                        } else {
                            DeepNavyBg.copy(alpha = 0.78f)
                        },
                        border = BorderStroke(
                            1.dp,
                            when {
                                isSelected -> ElectricCyan
                                isTracked -> BioluminescentTeal.copy(alpha = 0.55f)
                                else -> GlassBorderCyan
                            },
                        ),
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(7.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isTracked -> BioluminescentTeal
                                            isSelected -> ElectricCyan
                                            else -> TextMuted.copy(alpha = 0.5f)
                                        },
                                    ),
                            )
                            Text(
                                text = item.name,
                                style = MaterialTheme.typography.labelLarge,
                                color = if (isSelected) Color.White else TextSecondary,
                            )
                        }
                    }
                }
            }

            // Hologram + Anatomical Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.Top,
            ) {
                BodyHologramCanvas(
                    selectedRegion = selectedRegion,
                    trackedRegions = telemetry.trackedRegions,
                    onSelectRegion = onSelectRegion,
                    modifier = Modifier.width(126.dp),
                )

                AnimatedContent(
                    targetState = region,
                    modifier = Modifier.weight(1f),
                    label = "regionDetailTransition",
                ) { activeInfo ->
                    RegionSummaryColumn(
                        region = activeInfo,
                        onAskAiAboutRegion = onAskAiAboutRegion,
                    )
                }
            }

            // Associated Anatomy, Function & Interesting Facts
            AnimatedContent(
                targetState = region,
                label = "regionSpecsTransition",
            ) { activeInfo ->
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Associated Anatomy Subgroups
                    AnatomyInfoSection(title = stringResource(R.string.section_associated_anatomy)) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            activeInfo.associatedAnatomy.forEach { subgroup ->
                                Surface(
                                    shape = MaterialTheme.shapes.small,
                                    color = NavyCardElevated.copy(alpha = 0.55f),
                                    border = BorderStroke(1.dp, GlassBorderCyan.copy(alpha = 0.35f)),
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp),
                                    ) {
                                        Text(
                                            text = subgroup.category,
                                            style = MaterialTheme.typography.titleSmall,
                                            color = SkyBlue,
                                        )
                                        subgroup.items.forEach { item ->
                                            Text(
                                                text = "• $item",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = TextSecondary,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Function
                    AnatomyInfoSection(title = stringResource(R.string.section_function)) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            activeInfo.functions.forEach { fn ->
                                Text(
                                    text = "• $fn",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary,
                                )
                            }
                        }
                    }

                    // Interesting Facts
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        color = BioluminescentTeal.copy(alpha = 0.07f),
                        border = BorderStroke(1.dp, BioluminescentTeal.copy(alpha = 0.32f)),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Text(
                                text = stringResource(R.string.section_interesting_facts).uppercase(),
                                style = MaterialTheme.typography.labelMedium,
                                color = BioluminescentTeal,
                            )
                            activeInfo.interestingFacts.forEach { fact ->
                                Text(
                                    text = "• $fact",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextPrimary.copy(alpha = 0.92f),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RegionSummaryColumn(
    region: BodyRegionInfo,
    onAskAiAboutRegion: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = region.clinicalName.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = ElectricCyan,
        )
        Text(
            text = region.name,
            style = MaterialTheme.typography.displaySmall,
            color = Color.White,
        )
        Text(
            text = region.summary,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
        )

        OutlinedButton(
            onClick = {
                onAskAiAboutRegion("Tell me more about the ${region.name.lowercase()} anatomy and function.")
            },
            modifier = Modifier
                .height(48.dp)
                .testTag("ask_ai_region_button"),
            border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.6f)),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = ElectricCyan.copy(alpha = 0.12f),
                contentColor = ElectricCyan,
            ),
            shape = MaterialTheme.shapes.small,
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = "Ask AI: ${region.name}",
                fontWeight = FontWeight.SemiBold,
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = stringResource(R.string.section_related_systems).uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = ElectricCyan,
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                region.relatedSystems.forEach { systemName ->
                    Surface(
                        shape = CircleShape,
                        color = CobaltBlue.copy(alpha = 0.22f),
                        border = BorderStroke(1.dp, SkyBlue.copy(alpha = 0.42f)),
                    ) {
                        Text(
                            text = systemName,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFBAE6FD),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AnatomyInfoSection(
    title: String,
    content: @Composable () -> Unit,
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = DeepNavyBg.copy(alpha = 0.72f),
        border = BorderStroke(1.dp, GlassBorderCyan.copy(alpha = 0.45f)),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = ElectricCyan,
            )
            content()
        }
    }
}
