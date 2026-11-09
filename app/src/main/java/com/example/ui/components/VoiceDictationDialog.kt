package com.example.ui.components

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ui.theme.ComplianceGreen
import com.example.ui.theme.ComplianceRed
import com.example.ui.theme.WorkforceCardBorder
import com.example.ui.theme.WorkforceDarkBg
import com.example.ui.theme.WorkforceOrange
import com.example.ui.theme.WorkforceRed
import com.example.ui.theme.WorkforceSurface
import com.example.ui.theme.WorkforceSurfaceVariant
import com.example.ui.theme.WorkforceTextMuted
import com.example.ui.theme.WorkforceTextPrimary
import com.example.ui.theme.WorkforceTextSecondary
import java.util.Locale

/**
 * Hands-Free Voice-to-Text Dictation Dialog for inspection remarks and hygiene violation notes.
 * Designed for field hygiene officers wearing gloves and PPE in ablution facilities.
 */
@Composable
fun VoiceDictationDialog(
    questionNumber: Int,
    questionText: String,
    initialRemarks: String,
    onDismiss: () -> Unit,
    onSaveRemarks: (String) -> Unit
) {
    val context = LocalContext.current
    var remarksText by remember { mutableStateOf(initialRemarks) }
    var recognizedTextBuffer by remember { mutableStateOf("") }
    var isListening by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf("Tap the microphone or speak hands-free...") }
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Fallback system speech recognizer activity launcher
    val systemSpeechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val spokenTextList = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
        if (!spokenTextList.isNullOrEmpty()) {
            val spoken = spokenTextList[0]
            recognizedTextBuffer = spoken
            remarksText = if (remarksText.isNotBlank()) "$remarksText. $spoken" else spoken
            statusMessage = "Dictation captured: \"$spoken\""
        }
        isListening = false
    }

    // Permission launcher for RECORD_AUDIO
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
        if (isGranted) {
            statusMessage = "Microphone permission granted. Ready to dictate."
        } else {
            statusMessage = "Audio permission required for hands-free dictation."
        }
    }

    // SpeechRecognizer instance management
    val speechRecognizer = remember {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            SpeechRecognizer.createSpeechRecognizer(context)
        } else {
            null
        }
    }

    fun startListening() {
        if (!hasPermission) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            return
        }

        if (speechRecognizer != null) {
            try {
                isListening = true
                statusMessage = "Listening... Speak findings clearly into microphone"
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                    putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                    putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
                }

                speechRecognizer.setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        statusMessage = "Listening... (Microphone active)"
                    }

                    override fun onBeginningOfSpeech() {
                        statusMessage = "Audio detected... transcribing"
                    }

                    override fun onRmsChanged(rmsdB: Float) {
                        // Sound level tracking
                    }

                    override fun onBufferReceived(buffer: ByteArray?) {}

                    override fun onEndOfSpeech() {
                        statusMessage = "Processing speech transcription..."
                        isListening = false
                    }

                    override fun onError(error: Int) {
                        isListening = false
                        val errorText = when (error) {
                            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                            SpeechRecognizer.ERROR_CLIENT -> "Client error"
                            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Audio permissions missing"
                            SpeechRecognizer.ERROR_NETWORK -> "Network required for cloud speech"
                            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                            SpeechRecognizer.ERROR_NO_MATCH -> "No speech detected. Please speak closer to mic."
                            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Speech service busy"
                            SpeechRecognizer.ERROR_SERVER -> "Speech server error"
                            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech heard"
                            else -> "Recognition issue (Code $error)"
                        }
                        statusMessage = errorText
                    }

                    override fun onResults(results: Bundle?) {
                        isListening = false
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        if (!matches.isNullOrEmpty()) {
                            val transcript = matches[0]
                            recognizedTextBuffer = transcript
                            remarksText = if (remarksText.isNotBlank()) "$remarksText. $transcript" else transcript
                            statusMessage = "Transcribed: \"$transcript\""
                        } else {
                            statusMessage = "Dictation finished (Ready)"
                        }
                    }

                    override fun onPartialResults(partialResults: Bundle?) {
                        val partials = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        if (!partials.isNullOrEmpty()) {
                            recognizedTextBuffer = partials[0]
                            statusMessage = "Dictating: ${partials[0]}"
                        }
                    }

                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })

                speechRecognizer.startListening(intent)
            } catch (e: Exception) {
                isListening = false
                statusMessage = "Launching standard speech dialog..."
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_PROMPT, "Dictate ablution facility hygiene remarks")
                }
                systemSpeechLauncher.launch(intent)
            }
        } else {
            // SpeechRecognizer not directly available, launch standard Intent
            isListening = true
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Dictate inspection findings")
            }
            try {
                systemSpeechLauncher.launch(intent)
            } catch (e: Exception) {
                isListening = false
                statusMessage = "No speech recognition engine installed on this device."
            }
        }
    }

    fun stopListening() {
        if (isListening) {
            speechRecognizer?.stopListening()
            isListening = false
            statusMessage = "Microphone paused."
        }
    }

    // Auto-cleanup speech recognizer
    DisposableEffect(Unit) {
        onDispose {
            speechRecognizer?.destroy()
        }
    }

    // Pulsing circle animation for active microphone
    val infiniteTransition = rememberInfiniteTransition(label = "micPulse")
    val micScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(700),
            repeatMode = RepeatMode.Reverse
        ),
        label = "micScale"
    )

    // Common occupational hygiene phrases in South African mining change houses
    val standardHygienePhrases = listOf(
        "Mould build-up on shower tile grout",
        "Broken flush mechanism in cubicle",
        "No hand soap or paper towels supplied",
        "Stagnant water drainage pooling hazard",
        "Ventilation extractor fan defective",
        "Missing SDS compliance chemical sheet",
        "Cracked floor tiles causing trip hazard",
        "Deep sanitization required immediately",
        "Hot water supply temperature below 45°C",
        "Locker area clean and ventilated"
    )

    AlertDialog(
        onDismissRequest = {
            stopListening()
            onDismiss()
        },
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Dictation",
                        tint = WorkforceOrange,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "VOICE-TO-TEXT DICTATION",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = WorkforceOrange,
                                letterSpacing = 0.5.sp
                            )
                        )
                        Text(
                            text = "Question $questionNumber • Hands-Free Audio",
                            style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextSecondary, fontSize = 11.sp)
                        )
                    }
                }
                IconButton(onClick = {
                    stopListening()
                    onDismiss()
                }) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = WorkforceTextSecondary)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Question text summary
                Surface(
                    color = WorkforceSurfaceVariant,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Q$questionNumber: $questionText",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = WorkforceTextPrimary,
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier.padding(10.dp)
                    )
                }

                // Glove-friendly Large Microphone Recording Control Area
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isListening) WorkforceRed.copy(alpha = 0.15f) else WorkforceDarkBg
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isListening) WorkforceRed else WorkforceCardBorder
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Pulsing Mic Button (Large 72dp touch target for gloved hands)
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(76.dp)
                                .scale(if (isListening) micScale else 1.0f)
                                .clip(CircleShape)
                                .background(if (isListening) WorkforceRed else WorkforceOrange)
                                .clickable {
                                    if (isListening) stopListening() else startListening()
                                }
                                .testTag("btn_mic_record_toggle")
                        ) {
                            Icon(
                                imageVector = if (isListening) Icons.Default.Mic else Icons.Default.Mic,
                                contentDescription = if (isListening) "Stop Listening" else "Start Dictation",
                                tint = Color.White,
                                modifier = Modifier.size(38.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Status Badge
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isListening) WorkforceRed else WorkforceSurfaceVariant
                        ) {
                            Text(
                                text = if (isListening) "● LISTENING..." else "TAP TO DICTATE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isListening) Color.White else WorkforceTextSecondary,
                                    fontSize = 11.sp
                                ),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = statusMessage,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (isListening) WorkforceOrange else WorkforceTextSecondary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                // Remarks Text Field (Real-time transcription preview & edit)
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Remarks & Violation Observations",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = WorkforceTextSecondary
                            )
                        )
                        if (remarksText.isNotEmpty()) {
                            TextButton(
                                onClick = {
                                    remarksText = ""
                                    recognizedTextBuffer = ""
                                }
                            ) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(12.dp), tint = ComplianceRed)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Clear", color = ComplianceRed, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }

                    OutlinedTextField(
                        value = remarksText,
                        onValueChange = { remarksText = it },
                        placeholder = {
                            Text(
                                "Dictate or type remarks hands-free...",
                                color = WorkforceTextMuted,
                                fontSize = 13.sp
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .testTag("remarks_dictation_input"),
                        maxLines = 5,
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = WorkforceOrange,
                            unfocusedBorderColor = WorkforceCardBorder,
                            focusedTextColor = WorkforceTextPrimary,
                            unfocusedTextColor = WorkforceTextPrimary,
                            focusedContainerColor = WorkforceDarkBg,
                            unfocusedContainerColor = WorkforceDarkBg
                        )
                    )
                }

                // Glove-Friendly Quick-Insert Standard Hygiene Phrase Chips
                Column {
                    Text(
                        text = "Quick Mining & Hygiene Phrases (One-Tap Insert)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = WorkforceOrange
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        standardHygienePhrases.forEach { phrase ->
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = WorkforceSurfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceCardBorder),
                                modifier = Modifier.clickable {
                                    remarksText = if (remarksText.isNotBlank()) "$remarksText. $phrase" else phrase
                                    statusMessage = "Added: \"$phrase\""
                                }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = WorkforceOrange, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = phrase,
                                        style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextPrimary, fontSize = 11.sp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    stopListening()
                    onSaveRemarks(remarksText)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = WorkforceRed),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("btn_save_voice_remarks")
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Save Remarks", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = {
                    stopListening()
                    onDismiss()
                },
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Cancel", color = WorkforceTextSecondary)
            }
        },
        containerColor = WorkforceSurface,
        shape = RoundedCornerShape(14.dp)
    )
}
