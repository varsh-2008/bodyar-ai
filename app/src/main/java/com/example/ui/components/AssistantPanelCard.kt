package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.AnatomyCatalog
import com.example.model.BodyRegionId
import com.example.model.ChatMessage
import com.example.ui.theme.BioluminescentTeal
import com.example.ui.theme.CobaltBlue
import com.example.ui.theme.DeepNavyBg
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.GlassBorderCyan
import com.example.ui.theme.NavyCardElevated
import com.example.ui.theme.NavyCardSurface
import com.example.ui.theme.OnCyanDark
import com.example.ui.theme.SkyBlue
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AssistantPanelCard(
    selectedRegion: BodyRegionId,
    messages: List<ChatMessage>,
    isThinking: Boolean,
    onSendMessage: (String) -> Unit,
    onSelectRegion: (BodyRegionId) -> Unit,
    modifier: Modifier = Modifier,
) {
    var inputText by rememberSaveable { mutableStateOf("") }
    val activeRegionName = AnatomyCatalog.getRegion(selectedRegion).name

    val quickPrompts = listOf(
        "What bones and muscles are in the ${activeRegionName.lowercase()}?",
        "Give me an interesting fact about the ${activeRegionName.lowercase()}",
        "How do the heart and lungs work together?",
        "How many bones are in the hands and feet?",
    )

    fun submitQuery() {
        val trimmed = inputText.trim()
        if (trimmed.isNotEmpty() && !isThinking) {
            inputText = ""
            onSendMessage(trimmed)
        }
    }

    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        color = NavyCardSurface.copy(alpha = 0.88f),
        border = BorderStroke(1.dp, GlassBorderCyan),
        tonalElevation = 4.dp,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f),
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(ElectricCyan.copy(alpha = 0.35f), CobaltBlue.copy(alpha = 0.15f)),
                                ),
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            tint = ElectricCyan,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                    Column {
                        Text(
                            text = stringResource(R.string.assistant_title),
                            style = MaterialTheme.typography.titleLarge,
                            color = TextPrimary,
                        )
                        Text(
                            text = "${stringResource(R.string.assistant_subtitle)} • Focus: $activeRegionName",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                        )
                    }
                }

                Surface(
                    shape = CircleShape,
                    color = DeepNavyBg.copy(alpha = 0.85f),
                    border = BorderStroke(1.dp, BioluminescentTeal.copy(alpha = 0.5f)),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(BioluminescentTeal),
                        )
                        Text(
                            text = "Knowledge Base",
                            style = MaterialTheme.typography.labelSmall,
                            color = BioluminescentTeal,
                        )
                    }
                }
            }

            // Firebase Notice Banner (since Firebase AI setup was declined)
            Surface(
                shape = MaterialTheme.shapes.small,
                color = CobaltBlue.copy(alpha = 0.14f),
                border = BorderStroke(1.dp, SkyBlue.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = SkyBlue,
                        modifier = Modifier.size(16.dp),
                    )
                    Text(
                        text = stringResource(R.string.assistant_firebase_notice),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFBAE6FD),
                    )
                }
            }

            // Suggested Quick Prompt Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                quickPrompts.forEachIndexed { idx, prompt ->
                    Surface(
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .clip(CircleShape)
                            .clickable(enabled = !isThinking) { onSendMessage(prompt) }
                            .testTag("quick_prompt_$idx"),
                        shape = CircleShape,
                        color = DeepNavyBg.copy(alpha = 0.82f),
                        border = BorderStroke(1.dp, GlassBorderCyan),
                    ) {
                        Text(
                            text = prompt,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFBAE6FD),
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        )
                    }
                }
            }

            // Conversation Thread
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = DeepNavyBg.copy(alpha = 0.82f),
                border = BorderStroke(1.dp, GlassBorderCyan.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    messages.forEach { msg ->
                        ChatBubble(
                            message = msg,
                            selectedRegion = selectedRegion,
                            onSelectRegion = onSelectRegion,
                        )
                    }

                    if (isThinking) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.padding(vertical = 4.dp),
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = ElectricCyan,
                            )
                            Text(
                                text = "Analyzing anatomical query…",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                            )
                        }
                    }
                }
            }

            // Input Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = {
                        Text(
                            text = stringResource(R.string.assistant_input_hint),
                            color = TextMuted,
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = { submitQuery() }),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DeepNavyBg.copy(alpha = 0.9f),
                        unfocusedContainerColor = DeepNavyBg.copy(alpha = 0.85f),
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = GlassBorderCyan,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = ElectricCyan,
                    ),
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("assistant_input_field"),
                )

                Button(
                    onClick = { submitQuery() },
                    enabled = inputText.isNotBlank() && !isThinking,
                    modifier = Modifier
                        .height(54.dp)
                        .testTag("assistant_send_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElectricCyan,
                        contentColor = OnCyanDark,
                        disabledContainerColor = ElectricCyan.copy(alpha = 0.28f),
                        disabledContentColor = TextMuted,
                    ),
                    shape = MaterialTheme.shapes.small,
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = stringResource(R.string.cd_send_message),
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.btn_send),
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Composable
private fun ChatBubble(
    message: ChatMessage,
    selectedRegion: BodyRegionId,
    onSelectRegion: (BodyRegionId) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start,
    ) {
        Surface(
            modifier = Modifier.widthIn(max = 560.dp),
            shape = MaterialTheme.shapes.medium,
            color = if (message.isUser) {
                CobaltBlue.copy(alpha = 0.30f)
            } else {
                NavyCardElevated.copy(alpha = 0.88f)
            },
            border = BorderStroke(
                1.dp,
                if (message.isUser) ElectricCyan.copy(alpha = 0.55f) else SkyBlue.copy(alpha = 0.32f),
            ),
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = if (message.isUser) "You" else stringResource(R.string.assistant_title),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (message.isUser) ElectricCyan else SkyBlue,
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = message.timestamp,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                    )
                }

                Text(
                    text = parseRichMarkdown(message.content),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary,
                )

                val suggested = message.suggestedRegion
                if (!message.isUser && suggested != null && suggested != selectedRegion) {
                    val regionName = AnatomyCatalog.getRegion(suggested).name
                    Surface(
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .clip(MaterialTheme.shapes.small)
                            .clickable { onSelectRegion(suggested) }
                            .testTag("jump_region_${suggested.name.lowercase()}"),
                        shape = MaterialTheme.shapes.small,
                        color = ElectricCyan.copy(alpha = 0.14f),
                        border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.5f)),
                    ) {
                        Text(
                            text = "Focus $regionName in Body Explorer →",
                            style = MaterialTheme.typography.labelLarge,
                            color = ElectricCyan,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        )
                    }
                }
            }
        }
    }
}

private fun parseRichMarkdown(text: String): AnnotatedString {
    return buildAnnotatedString {
        var idx = 0
        while (idx < text.length) {
            if (text.startsWith("**", idx)) {
                val end = text.indexOf("**", idx + 2)
                if (end != -1) {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color.White)) {
                        append(text.substring(idx + 2, end))
                    }
                    idx = end + 2
                    continue
                }
            }
            if (text[idx] == '*' && (idx == 0 || text[idx - 1] != '*')) {
                val end = text.indexOf('*', idx + 1)
                if (end != -1 && end > idx + 1) {
                    withStyle(SpanStyle(fontStyle = FontStyle.Italic, color = SkyBlue)) {
                        append(text.substring(idx + 1, end))
                    }
                    idx = end + 1
                    continue
                }
            }
            append(text[idx])
            idx++
        }
    }
}
