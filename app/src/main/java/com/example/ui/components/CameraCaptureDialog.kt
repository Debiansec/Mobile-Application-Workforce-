package com.example.ui.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.data.model.ChecklistQuestion
import com.example.ui.theme.ComplianceGreen
import com.example.ui.theme.WorkforceCardBorder
import com.example.ui.theme.WorkforceDarkBg
import com.example.ui.theme.WorkforceOrange
import com.example.ui.theme.WorkforceRed
import com.example.ui.theme.WorkforceSurface
import com.example.ui.theme.WorkforceSurfaceVariant
import com.example.ui.theme.WorkforceTextMuted
import com.example.ui.theme.WorkforceTextPrimary
import com.example.ui.theme.WorkforceTextSecondary
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.Executors

@Composable
fun CameraCaptureDialog(
    question: ChecklistQuestion,
    onDismiss: () -> Unit,
    onPhotoCaptured: (photoUri: String, caption: String, lat: Double?, lon: Double?) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    var captionText by remember {
        mutableStateOf("Evidence for ${question.sectionId.name.removePrefix("SECTION_")}.Q${question.questionNumber}: ${question.questionText.take(45)}")
    }

    var isCapturing by remember { mutableStateOf(false) }
    var lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }

    // Representative mining site GPS coordinates (e.g. Letaba Crushers Tzaneen Quarry)
    val defaultLat = -23.83120 + (question.questionNumber * 0.00012)
    val defaultLon = 30.16340 + (question.questionNumber * 0.00008)

    // Camera permission request launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    var pendingMarkupPhotoPath by remember { mutableStateOf<String?>(null) }

    // Photo picker fallback launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            pendingMarkupPhotoPath = uri.toString()
        }
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .testTag("camera_capture_dialog"),
        containerColor = WorkforceDarkBg,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "HYGIENE EVIDENCE CAPTURE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = WorkforceOrange,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                    Text(
                        text = "Item ${question.sectionId.name.removePrefix("SECTION_")}.Q${question.questionNumber}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = WorkforceTextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = WorkforceTextSecondary)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = question.questionText,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = WorkforceTextSecondary,
                        lineHeight = 16.sp
                    )
                )

                // Live CameraX Viewfinder or Permission Prompt
                if (hasCameraPermission) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black)
                            .border(1.dp, WorkforceCardBorder, RoundedCornerShape(12.dp))
                    ) {
                        AndroidView(
                            factory = { ctx ->
                                val previewView = PreviewView(ctx).apply {
                                    layoutParams = ViewGroup.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT
                                    )
                                    scaleType = PreviewView.ScaleType.FILL_CENTER
                                }

                                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                                cameraProviderFuture.addListener({
                                    val cameraProvider = cameraProviderFuture.get()
                                    val preview = Preview.Builder().build().also {
                                        it.surfaceProvider = previewView.surfaceProvider
                                    }

                                    val capture = ImageCapture.Builder()
                                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                                        .build()
                                    imageCapture = capture

                                    val selector = CameraSelector.Builder()
                                        .requireLensFacing(lensFacing)
                                        .build()

                                    try {
                                        cameraProvider.unbindAll()
                                        cameraProvider.bindToLifecycle(
                                            lifecycleOwner,
                                            selector,
                                            preview,
                                            capture
                                        )
                                    } catch (exc: Exception) {
                                        exc.printStackTrace()
                                    }
                                }, ContextCompat.getMainExecutor(ctx))

                                previewView
                            },
                            modifier = Modifier.fillMaxSize()
                        )

                        // Camera controls overlay: Switch Camera & GPS indicator
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .align(Alignment.TopCenter),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color.Black.copy(alpha = 0.6f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MyLocation,
                                        contentDescription = null,
                                        tint = WorkforceOrange,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = String.format(Locale.US, "%.4f, %.4f", defaultLat, defaultLon),
                                        style = MaterialTheme.typography.bodySmall.copy(color = Color.White, fontSize = 10.sp)
                                    )
                                }
                            }

                            IconButton(
                                onClick = {
                                    lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
                                        CameraSelector.LENS_FACING_FRONT
                                    } else {
                                        CameraSelector.LENS_FACING_BACK
                                    }
                                },
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Cameraswitch,
                                    contentDescription = "Switch Camera",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        // Shutter button inside viewfinder
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 12.dp)
                        ) {
                            Button(
                                onClick = {
                                    val capture = imageCapture
                                    if (capture != null && !isCapturing) {
                                        isCapturing = true
                                        takePhoto(
                                            context = context,
                                            imageCapture = capture,
                                            question = question,
                                            onSuccess = { file ->
                                                isCapturing = false
                                                pendingMarkupPhotoPath = file.absolutePath
                                            },
                                            onError = {
                                                isCapturing = false
                                                val simUri = "wfhsa_photo_${question.id}_${System.currentTimeMillis()}.jpg"
                                                pendingMarkupPhotoPath = simUri
                                            }
                                        )
                                    } else {
                                        val simUri = "wfhsa_photo_${question.id}_${System.currentTimeMillis()}.jpg"
                                        pendingMarkupPhotoPath = simUri
                                    }
                                },
                                shape = CircleShape,
                                colors = ButtonDefaults.buttonColors(containerColor = WorkforceRed),
                                modifier = Modifier
                                    .size(54.dp)
                                    .testTag("camerax_shutter_button")
                            ) {
                                if (isCapturing) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                                } else {
                                    Icon(imageVector = Icons.Default.CameraAlt, contentDescription = "Snap Photo", tint = Color.White)
                                }
                            }
                        }
                    }
                } else {
                    // Fallback when camera permission is denied or pending
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = WorkforceSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceCardBorder)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, tint = WorkforceOrange, modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Camera Permission Required",
                                style = MaterialTheme.typography.titleSmall.copy(color = WorkforceTextPrimary, fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Grant permission or choose an existing evidence image from device storage.",
                                style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextSecondary, fontSize = 11.sp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                                    colors = ButtonDefaults.buttonColors(containerColor = WorkforceRed)
                                ) {
                                    Text("Grant Camera")
                                }
                                OutlinedButton(
                                    onClick = { photoPickerLauncher.launch("image/*") }
                                ) {
                                    Icon(imageVector = Icons.Default.Image, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Pick Photo", color = WorkforceTextPrimary)
                                }
                            }
                        }
                    }
                }

                // Evidence Caption & Observation notes
                OutlinedTextField(
                    value = captionText,
                    onValueChange = { captionText = it },
                    label = { Text("Photo Evidence Caption & Violation Details") },
                    placeholder = { Text("e.g. Mould on shower 4 rear wall grout") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = WorkforceRed,
                        unfocusedBorderColor = WorkforceCardBorder,
                        focusedTextColor = WorkforceTextPrimary,
                        unfocusedTextColor = WorkforceTextPrimary
                    )
                )

                // Pick from Gallery or Snapshot Action
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = { photoPickerLauncher.launch("image/*") }) {
                        Icon(imageVector = Icons.Default.Image, contentDescription = null, modifier = Modifier.size(16.dp), tint = WorkforceOrange)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Pick from Gallery", color = WorkforceOrange, style = MaterialTheme.typography.labelSmall)
                    }

                    // Direct Snapshot Button (fallback)
                    Button(
                        onClick = {
                            val simUri = "wfhsa_photo_${question.id}_${System.currentTimeMillis()}.jpg"
                            pendingMarkupPhotoPath = simUri
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = WorkforceSurfaceVariant),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text("Simulate Shutter", color = WorkforceTextPrimary, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {}
    )

    pendingMarkupPhotoPath?.let { photoPath ->
        PhotoMarkupEditorDialog(
            initialPhotoPath = photoPath,
            questionTitle = "Q${question.questionNumber}: ${question.questionText}",
            onDismiss = {
                // If dismissed, save raw photo without annotations
                onPhotoCaptured(photoPath, captionText, defaultLat, defaultLon)
                pendingMarkupPhotoPath = null
                onDismiss()
            },
            onSaveAnnotatedPhoto = { annotatedPath ->
                onPhotoCaptured(annotatedPath, captionText, defaultLat, defaultLon)
                pendingMarkupPhotoPath = null
                onDismiss()
            }
        )
    }
}

private fun takePhoto(
    context: Context,
    imageCapture: ImageCapture,
    question: ChecklistQuestion,
    onSuccess: (File) -> Unit,
    onError: (Exception) -> Unit
) {
    val photoDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) ?: context.filesDir
    if (!photoDir.exists()) photoDir.mkdirs()

    val fileName = "WFHSA_Q${question.questionNumber}_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.jpg"
    val photoFile = File(photoDir, fileName)

    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                onSuccess(photoFile)
            }

            override fun onError(exception: ImageCaptureException) {
                onError(exception)
            }
        }
    )
}
