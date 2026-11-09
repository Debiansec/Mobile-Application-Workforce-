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
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.service.LocationService
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class RemarkInputMode(val label: String, val iconDescription: String) {
    TYPING("Typing (Keyboard)", "Manual typing"),
    VOICE("Voice-to-Text", "Hands-free dictation")
}

/**
 * FindingRemarkEditor
 *
 * Provides a dedicated, high-contrast, glove-friendly interface giving inspectors an explicit option
 * between Voice-to-Text Dictation and Manual Typing for comments or remarks on hygiene inspection findings,
 * combined with precise Geolocation / GPS Coordinates capture.
 */
@Composable
fun FindingRemarkEditor(
    modifier: Modifier = Modifier,
    findingId: String,
    findingTitle: String,
    initialRemark: String = "",
    initialMode: RemarkInputMode = RemarkInputMode.TYPING,
    currentLatitude: Double? = null,
    currentLongitude: Double? = null,
    currentAccuracy: Float? = null,
    currentGeoTimestamp: String? = null,
    onRemarkChanged: (String, RemarkInputMode) -> Unit,
    onGeoCoordinatesStamped: (latitude: Double, longitude: Double, accuracy: Float) -> Unit,
    onOpenFullVoiceDialog: (() -> Unit)? = null
) {
    val context = LocalContext.current
    var selectedMode by remember(initialMode) { mutableStateOf(initialMode) }
    var textRemark by remember(initialRemark) { mutableStateOf(initialRemark) }

    // GPS states
    var lat by remember(currentLatitude) { mutableStateOf(currentLatitude) }
    var lon by remember(currentLongitude) { mutableStateOf(currentLongitude) }
    var accuracy by remember(currentAccuracy) { mutableStateOf(currentAccuracy) }
    var geoTime by remember(currentGeoTimestamp) { mutableStateOf(currentGeoTimestamp) }
    var isAcquiringGps by remember { mutableStateOf(false) }

    // Audio & Dictation states
    var isListening by remember { mutableStateOf(false) }
    var voiceStatusText by remember { mutableStateOf("Tap microphone or speak hands-free...") }
    var hasAudioPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Audio permission launcher
    val audioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasAudioPermission = granted
        if (granted) {
            voiceStatusText = "Microphone ready. Tap mic to record."
        } else {
            voiceStatusText = "Audio permission required for dictation."
        }
    }

    // Location permission launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        if (fineGranted || coarseGranted) {
            val loc = LocationService.getCurrentGpsLocation(context)
            lat = loc.latitude
            lon = loc.longitude
            accuracy = loc.accuracyMeters
            geoTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
            onGeoCoordinatesStamped(loc.latitude, loc.longitude, loc.accuracyMeters)
        }
        isAcquiringGps = false
    }

    fun stampGps() {
        isAcquiringGps = true
        if (LocationService.hasLocationPermission(context)) {
            val loc = LocationService.getCurrentGpsLocation(context)
            lat = loc.latitude
            lon = loc.longitude
            accuracy = loc.accuracyMeters
            geoTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
            onGeoCoordinatesStamped(loc.latitude, loc.longitude, loc.accuracyMeters)
            isAcquiringGps = false
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    // SpeechRecognizer Activity fallback
    val systemSpeechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val spokenList = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
        if (!spokenList.isNullOrEmpty()) {
            val spoken = spokenList[0]
            val newText = if (textRemark.isNotBlank()) "$textRemark. $spoken" else spoken
            textRemark = newText
            onRemarkChanged(newText, RemarkInputMode.VOICE)
            voiceStatusText = "Dictated: \"$spoken\""
        }
        isListening = false
    }

    // SpeechRecognizer instance
    val speechRecognizer = remember {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            SpeechRecognizer.createSpeechRecognizer(context)
        } else {
            null
        }
    }

    fun startVoiceDictation() {
        if (!hasAudioPermission) {
            audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            return
        }

        if (speechRecognizer != null) {
            try {
                isListening = true
                voiceStatusText = "Listening... Speak findings clearly"
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                    putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                }

                speechRecognizer.setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        voiceStatusText = "Listening... Microphone active"
                    }
                    override fun onBeginningOfSpeech() {
                        voiceStatusText = "Audio detected... transcribing"
                    }
                    override fun onRmsChanged(rmsdB: Float) {}
                    override fun onBufferReceived(buffer: ByteArray?) {}
                    override fun onEndOfSpeech() {
                        voiceStatusText = "Processing audio..."
                        isListening = false
                    }
                    override fun onError(error: Int) {
                        isListening = false
                        voiceStatusText = "Speech paused (Code $error)"
                    }
                    override fun onResults(results: Bundle?) {
                        isListening = false
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        if (!matches.isNullOrEmpty()) {
                            val transcript = matches[0]
                            val newText = if (textRemark.isNotBlank()) "$textRemark. $transcript" else transcript
                            textRemark = newText
                            onRemarkChanged(newText, RemarkInputMode.VOICE)
                            voiceStatusText = "Transcribed: \"$transcript\""
                        }
                    }
                    override fun onPartialResults(partialResults: Bundle?) {
                        val partials = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        if (!partials.isNullOrEmpty()) {
                            voiceStatusText = "Dictating: ${partials[0]}"
                        }
                    }
                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
                speechRecognizer.startListening(intent)
            } catch (e: Exception) {
                isListening = false
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_PROMPT, "Dictate findings for $findingTitle")
                }
                systemSpeechLauncher.launch(intent)
            }
        } else {
            isListening = true
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Dictate findings for $findingTitle")
            }
            try {
                systemSpeechLauncher.launch(intent)
            } catch (e: Exception) {
                isListening = false
                voiceStatusText = "Speech engine unavailable"
            }
        }
    }

    fun stopVoiceDictation() {
        if (isListening) {
            speechRecognizer?.stopListening()
            isListening = false
            voiceStatusText = "Microphone paused."
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            speechRecognizer?.destroy()
        }
    }

    // Mic Pulsing Animation
    val infiniteTransition = rememberInfiniteTransition(label = "findingMicPulse")
    val micScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(animation = tween(650), repeatMode = RepeatMode.Reverse),
        label = "micScale"
    )

    // Common occupational hygiene phrases in South African mining change houses
    val quickPhrases = listOf(
        "Mould on shower tile grout",
        "Flush mechanism broken",
        "Water drainage pooling hazard",
        "Soap dispenser empty",
        "Ventilation extractor broken",
        "Missing SDS compliance sheet",
        "Floor tiles broken",
        "Potable water pressure low"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("finding_remark_editor_$findingId"),
        colors = CardDefaults.cardColors(containerColor = WorkforceDarkBg),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceCardBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header Row: Input Mode Selector (Typing vs Voice Dictation)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "FINDING REMARK & GEO COORDINATES",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = WorkforceOrange,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                )

                // Mode Selector Toggle Chips
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.background(WorkforceSurfaceVariant, RoundedCornerShape(8.dp)).padding(2.dp)
                ) {
                    // Typing Option
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (selectedMode == RemarkInputMode.TYPING) WorkforceRed else Color.Transparent,
                        modifier = Modifier
                            .clickable {
                                selectedMode = RemarkInputMode.TYPING
                                stopVoiceDictation()
                            }
                            .testTag("btn_mode_typing_$findingId")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Keyboard,
                                contentDescription = "Manual Typing",
                                tint = if (selectedMode == RemarkInputMode.TYPING) Color.White else WorkforceTextSecondary,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Type",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedMode == RemarkInputMode.TYPING) Color.White else WorkforceTextSecondary
                                )
                            )
                        }
                    }

                    // Voice Dictation Option
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (selectedMode == RemarkInputMode.VOICE) WorkforceOrange else Color.Transparent,
                        modifier = Modifier
                            .clickable {
                                selectedMode = RemarkInputMode.VOICE
                            }
                            .testTag("btn_mode_voice_$findingId")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Voice Dictation",
                                tint = if (selectedMode == RemarkInputMode.VOICE) Color.White else WorkforceTextSecondary,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Voice",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedMode == RemarkInputMode.VOICE) Color.White else WorkforceTextSecondary
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Geolocation / GPS Coordinates Strip
            Surface(
                color = WorkforceSurface,
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (lat != null) ComplianceGreen.copy(alpha = 0.5f) else WorkforceCardBorder
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = "GPS Coordinates",
                            tint = if (lat != null) ComplianceGreen else WorkforceOrange,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            if (lat != null && lon != null) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = String.format(Locale.US, "%.5f, %.5f", lat, lon),
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    )
                                    if (accuracy != null) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            color = ComplianceGreen.copy(alpha = 0.2f),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = "±${accuracy?.toInt()}m",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = ComplianceGreen,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = "GPS Stamped • ${geoTime ?: "Live"}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = WorkforceTextSecondary,
                                        fontSize = 10.sp
                                    )
                                )
                            } else {
                                Text(
                                    text = "No GPS Coordinates Stamped",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = WorkforceTextMuted,
                                        fontSize = 11.sp
                                    )
                                )
                                Text(
                                    text = "Tap to stamp current GPS coordinates to this finding",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = WorkforceTextSecondary,
                                        fontSize = 9.sp
                                    )
                                )
                            }
                        }
                    }

                    // Stamp / Refresh GPS Button
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (lat != null) WorkforceSurfaceVariant else WorkforceOrange,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (lat != null) ComplianceGreen else WorkforceOrange
                        ),
                        modifier = Modifier
                            .clickable(enabled = !isAcquiringGps) { stampGps() }
                            .testTag("btn_stamp_gps_$findingId")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (lat != null) Icons.Default.Refresh else Icons.Default.MyLocation,
                                contentDescription = "Stamp GPS",
                                tint = if (lat != null) ComplianceGreen else Color.White,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (lat != null) "Update GPS" else "Stamp GPS",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (lat != null) ComplianceGreen else Color.White,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Body content based on selected mode: VOICE vs TYPING
            if (selectedMode == RemarkInputMode.VOICE) {
                // Voice Dictation Mode UI
                Surface(
                    color = WorkforceSurface,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isListening) WorkforceOrange else WorkforceCardBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Glove-friendly Large Mic Button
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(58.dp)
                                .scale(if (isListening) micScale else 1.0f)
                                .clip(CircleShape)
                                .background(if (isListening) WorkforceRed else WorkforceOrange)
                                .clickable {
                                    if (isListening) stopVoiceDictation() else startVoiceDictation()
                                }
                                .testTag("btn_mic_inline_record_$findingId")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = if (isListening) "Stop Dictating" else "Start Voice Dictation",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Status badge
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isListening) WorkforceRed else WorkforceSurfaceVariant
                        ) {
                            Text(
                                text = if (isListening) "● LISTENING..." else "TAP MIC TO DICTATE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isListening) Color.White else WorkforceOrange,
                                    fontSize = 10.sp
                                ),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = voiceStatusText,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (isListening) WorkforceOrange else WorkforceTextSecondary,
                                fontSize = 11.sp
                            )
                        )

                        if (onOpenFullVoiceDialog != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            TextButton(
                                onClick = onOpenFullVoiceDialog,
                                modifier = Modifier.height(30.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = null,
                                    modifier = Modifier.size(13.dp),
                                    tint = WorkforceOrange
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "Open Full Voice Studio Dialog",
                                    color = WorkforceOrange,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp)
                                )
                            }
                        }
                    }
                }
            }

            // Text input area (always editable and previewable)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = textRemark,
                onValueChange = {
                    textRemark = it
                    onRemarkChanged(it, selectedMode)
                },
                placeholder = {
                    Text(
                        if (selectedMode == RemarkInputMode.VOICE)
                            "Voice transcription will appear here. You can also edit manually..."
                        else
                            "Type observation, violation notes, or required remedial action...",
                        color = WorkforceTextMuted,
                        fontSize = 12.sp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(95.dp)
                    .testTag("input_finding_remark_$findingId"),
                maxLines = 4,
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (selectedMode == RemarkInputMode.VOICE) WorkforceOrange else WorkforceRed,
                    unfocusedBorderColor = WorkforceCardBorder,
                    focusedTextColor = WorkforceTextPrimary,
                    unfocusedTextColor = WorkforceTextPrimary,
                    focusedContainerColor = WorkforceSurface,
                    unfocusedContainerColor = WorkforceSurface
                )
            )

            // One-tap quick hygiene phrase insertion chips
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                quickPhrases.forEach { phrase ->
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = WorkforceSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceCardBorder),
                        modifier = Modifier.clickable {
                            val updated = if (textRemark.isNotBlank()) "$textRemark. $phrase" else phrase
                            textRemark = updated
                            onRemarkChanged(updated, selectedMode)
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = WorkforceOrange,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = phrase,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = WorkforceTextPrimary,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
