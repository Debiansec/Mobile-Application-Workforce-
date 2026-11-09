package com.example.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas as AndroidCanvas
import android.graphics.Paint as AndroidPaint
import android.graphics.RectF as AndroidRectF
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.ArrowOutward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CropSquare
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FormatColorText
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Title
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

enum class MarkupTool(val displayName: String) {
    FREEHAND("Pen"),
    ARROW("Arrow"),
    RECTANGLE("Box"),
    CIRCLE("Circle"),
    TEXT("Text Label")
}

data class StrokePath(
    val tool: MarkupTool,
    val points: List<Offset>,
    val color: Color,
    val strokeWidth: Float,
    val startPoint: Offset = Offset.Zero,
    val endPoint: Offset = Offset.Zero
)

data class TextAnnotation(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val position: Offset,
    val color: Color,
    val backgroundColor: Color = Color.Black.copy(alpha = 0.75f)
)

/**
 * Interactive Photo Markup & Image Annotation Dialog.
 * Enables inspectors to draw arrows, boxes, circles, freehand lines,
 * and overlay high-contrast regulatory text labels directly on evidence photos.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PhotoMarkupEditorDialog(
    initialPhotoPath: String,
    questionTitle: String,
    onDismiss: () -> Unit,
    onSaveAnnotatedPhoto: (newPhotoPath: String) -> Unit
) {
    val context = LocalContext.current
    var activeTool by remember { mutableStateOf(MarkupTool.ARROW) }
    var currentColor by remember { mutableStateOf(Color(0xFFDC2626)) } // Regulatory Red default
    var currentStrokeWidth by remember { mutableFloatStateOf(6f) }

    val strokes = remember { mutableStateListOf<StrokePath>() }
    val textAnnotations = remember { mutableStateListOf<TextAnnotation>() }

    // In-progress drawing
    var currentPoints by remember { mutableStateOf<List<Offset>>(emptyList()) }
    var shapeStart by remember { mutableStateOf<Offset?>(null) }
    var shapeCurrent by remember { mutableStateOf<Offset?>(null) }

    // Text Overlay prompt state
    var showTextInputDialog by remember { mutableStateOf(false) }
    var pendingTextPosition by remember { mutableStateOf(Offset(100f, 100f)) }
    var textInputString by remember { mutableStateOf("DEFECT: DAMAGED") }

    val presetDefectTags = listOf(
        "DEFECT: DAMAGED",
        "HAZARD: MOULD GROWTH",
        "LEAK DETECTED",
        "MISSING SEAT / LID",
        "URINAL ODOUR BREACH",
        "SOAP DISPENSER EMPTY",
        "URGENT REPAIR REQ."
    )

    val colorPalette = listOf(
        Color(0xFFDC2626) to "Red (Defect)",
        Color(0xFFF59E0B) to "Amber (Warning)",
        Color(0xFFFACC15) to "Yellow (Caution)",
        Color(0xFF10B981) to "Green (Good)",
        Color(0xFFFFFFFF) to "White (Contrast)",
        Color(0xFF3B82F6) to "Blue (Info)"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            shape = RoundedCornerShape(16.dp),
            color = WorkforceDarkBg,
            border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceCardBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                // Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = WorkforceOrange.copy(alpha = 0.2f),
                            modifier = Modifier.size(34.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = null,
                                    tint = WorkforceOrange,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "PHOTO MARKUP & ANNOTATION",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = WorkforceOrange,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            )
                            Text(
                                text = questionTitle.take(38) + if (questionTitle.length > 38) "..." else "",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = WorkforceTextPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }

                    Row {
                        IconButton(
                            onClick = {
                                if (strokes.isNotEmpty()) strokes.removeAt(strokes.lastIndex)
                                else if (textAnnotations.isNotEmpty()) textAnnotations.removeAt(textAnnotations.lastIndex)
                            },
                            enabled = strokes.isNotEmpty() || textAnnotations.isNotEmpty(),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Undo,
                                contentDescription = "Undo",
                                tint = if (strokes.isNotEmpty() || textAnnotations.isNotEmpty()) WorkforceTextPrimary else WorkforceTextMuted
                            )
                        }

                        IconButton(
                            onClick = {
                                strokes.clear()
                                textAnnotations.clear()
                            },
                            enabled = strokes.isNotEmpty() || textAnnotations.isNotEmpty(),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear All",
                                tint = if (strokes.isNotEmpty() || textAnnotations.isNotEmpty()) WorkforceRed else WorkforceTextMuted
                            )
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Cancel", tint = WorkforceTextSecondary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Tool & Palette Toolbar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Tool selectors
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf(
                            MarkupTool.ARROW to Icons.Default.ArrowOutward,
                            MarkupTool.RECTANGLE to Icons.Default.CropSquare,
                            MarkupTool.CIRCLE to Icons.Default.RadioButtonUnchecked,
                            MarkupTool.FREEHAND to Icons.Default.Edit,
                            MarkupTool.TEXT to Icons.Default.Title
                        ).forEach { (tool, icon) ->
                            val isSelected = activeTool == tool
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) WorkforceOrange else WorkforceSurface,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) WorkforceOrange else WorkforceCardBorder
                                ),
                                modifier = Modifier
                                    .size(38.dp)
                                    .clickable {
                                        activeTool = tool
                                        if (tool == MarkupTool.TEXT) {
                                            showTextInputDialog = true
                                        }
                                    }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = tool.displayName,
                                        tint = if (isSelected) Color.White else WorkforceTextSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Color palette chips
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        colorPalette.forEach { (color, _) ->
                            val isSelected = currentColor == color
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .clickable { currentColor = color }
                                    .then(
                                        if (isSelected) Modifier.border(2.dp, Color.White, CircleShape)
                                        else Modifier.border(1.dp, Color.Gray.copy(alpha = 0.5f), CircleShape)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = if (color == Color.White || color == Color(0xFFFACC15)) Color.Black else Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Interactive Drawing Canvas
                BoxWithConstraints(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black)
                        .border(1.dp, WorkforceCardBorder, RoundedCornerShape(12.dp))
                ) {
                    val canvasWidth = constraints.maxWidth.toFloat()
                    val canvasHeight = constraints.maxHeight.toFloat()

                    androidx.compose.foundation.Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(activeTool) {
                                detectDragGestures(
                                    onDragStart = { offset ->
                                        when (activeTool) {
                                            MarkupTool.FREEHAND -> {
                                                currentPoints = listOf(offset)
                                            }
                                            MarkupTool.ARROW, MarkupTool.RECTANGLE, MarkupTool.CIRCLE -> {
                                                shapeStart = offset
                                                shapeCurrent = offset
                                            }
                                            MarkupTool.TEXT -> {
                                                pendingTextPosition = offset
                                                showTextInputDialog = true
                                            }
                                        }
                                    },
                                    onDrag = { change, _ ->
                                        change.consume()
                                        when (activeTool) {
                                            MarkupTool.FREEHAND -> {
                                                currentPoints = currentPoints + change.position
                                            }
                                            MarkupTool.ARROW, MarkupTool.RECTANGLE, MarkupTool.CIRCLE -> {
                                                shapeCurrent = change.position
                                            }
                                            MarkupTool.TEXT -> {
                                                // No drag for text trigger
                                            }
                                        }
                                    },
                                    onDragEnd = {
                                        when (activeTool) {
                                            MarkupTool.FREEHAND -> {
                                                if (currentPoints.size > 1) {
                                                    strokes.add(
                                                        StrokePath(
                                                            tool = MarkupTool.FREEHAND,
                                                            points = currentPoints,
                                                            color = currentColor,
                                                            strokeWidth = currentStrokeWidth
                                                        )
                                                    )
                                                }
                                                currentPoints = emptyList()
                                            }
                                            MarkupTool.ARROW, MarkupTool.RECTANGLE, MarkupTool.CIRCLE -> {
                                                val start = shapeStart
                                                val end = shapeCurrent
                                                if (start != null && end != null) {
                                                    strokes.add(
                                                        StrokePath(
                                                            tool = activeTool,
                                                            points = listOf(start, end),
                                                            color = currentColor,
                                                            strokeWidth = currentStrokeWidth,
                                                            startPoint = start,
                                                            endPoint = end
                                                        )
                                                    )
                                                }
                                                shapeStart = null
                                                shapeCurrent = null
                                            }
                                            MarkupTool.TEXT -> {
                                                // Handled in dialog confirm
                                            }
                                        }
                                    }
                                )
                            }
                    ) {
                        // Background placeholder / simulated camera image if local file does not exist
                        drawRect(
                            color = Color(0xFF181C26),
                            size = size
                        )

                        // Subtle grid lines to simulate inspection drafting texture
                        val gridStep = 40.dp.toPx()
                        for (x in 0..(size.width / gridStep).toInt()) {
                            drawLine(
                                color = Color.White.copy(alpha = 0.04f),
                                start = Offset(x * gridStep, 0f),
                                end = Offset(x * gridStep, size.height),
                                strokeWidth = 1f
                            )
                        }
                        for (y in 0..(size.height / gridStep).toInt()) {
                            drawLine(
                                color = Color.White.copy(alpha = 0.04f),
                                start = Offset(0f, y * gridStep),
                                end = Offset(size.width, y * gridStep),
                                strokeWidth = 1f
                            )
                        }

                        // Completed strokes
                        strokes.forEach { stroke ->
                            when (stroke.tool) {
                                MarkupTool.FREEHAND -> {
                                    if (stroke.points.size > 1) {
                                        val path = Path().apply {
                                            moveTo(stroke.points[0].x, stroke.points[0].y)
                                            for (i in 1 until stroke.points.size) {
                                                lineTo(stroke.points[i].x, stroke.points[i].y)
                                            }
                                        }
                                        drawPath(
                                            path = path,
                                            color = stroke.color,
                                            style = Stroke(
                                                width = stroke.strokeWidth,
                                                cap = StrokeCap.Round,
                                                join = StrokeJoin.Round
                                            )
                                        )
                                    }
                                }
                                MarkupTool.ARROW -> {
                                    drawArrow(
                                        start = stroke.startPoint,
                                        end = stroke.endPoint,
                                        color = stroke.color,
                                        strokeWidth = stroke.strokeWidth
                                    )
                                }
                                MarkupTool.RECTANGLE -> {
                                    val left = minOf(stroke.startPoint.x, stroke.endPoint.x)
                                    val top = minOf(stroke.startPoint.y, stroke.endPoint.y)
                                    val right = maxOf(stroke.startPoint.x, stroke.endPoint.x)
                                    val bottom = maxOf(stroke.startPoint.y, stroke.endPoint.y)
                                    drawRect(
                                        color = stroke.color,
                                        topLeft = Offset(left, top),
                                        size = Size(right - left, bottom - top),
                                        style = Stroke(width = stroke.strokeWidth)
                                    )
                                }
                                MarkupTool.CIRCLE -> {
                                    val left = minOf(stroke.startPoint.x, stroke.endPoint.x)
                                    val top = minOf(stroke.startPoint.y, stroke.endPoint.y)
                                    val right = maxOf(stroke.startPoint.x, stroke.endPoint.x)
                                    val bottom = maxOf(stroke.startPoint.y, stroke.endPoint.y)
                                    val center = Offset((left + right) / 2f, (top + bottom) / 2f)
                                    val radius = maxOf(right - left, bottom - top) / 2f
                                    drawCircle(
                                        color = stroke.color,
                                        radius = radius,
                                        center = center,
                                        style = Stroke(width = stroke.strokeWidth)
                                    )
                                }
                                else -> {}
                            }
                        }

                        // In-progress shape/path preview
                        if (currentPoints.size > 1 && activeTool == MarkupTool.FREEHAND) {
                            val path = Path().apply {
                                moveTo(currentPoints[0].x, currentPoints[0].y)
                                for (i in 1 until currentPoints.size) {
                                    lineTo(currentPoints[i].x, currentPoints[i].y)
                                }
                            }
                            drawPath(
                                path = path,
                                color = currentColor,
                                style = Stroke(
                                    width = currentStrokeWidth,
                                    cap = StrokeCap.Round,
                                    join = StrokeJoin.Round
                                )
                            )
                        }

                        val start = shapeStart
                        val curr = shapeCurrent
                        if (start != null && curr != null) {
                            when (activeTool) {
                                MarkupTool.ARROW -> {
                                    drawArrow(start, curr, currentColor, currentStrokeWidth)
                                }
                                MarkupTool.RECTANGLE -> {
                                    val left = minOf(start.x, curr.x)
                                    val top = minOf(start.y, curr.y)
                                    val right = maxOf(start.x, curr.x)
                                    val bottom = maxOf(start.y, curr.y)
                                    drawRect(
                                        color = currentColor,
                                        topLeft = Offset(left, top),
                                        size = Size(right - left, bottom - top),
                                        style = Stroke(width = currentStrokeWidth)
                                    )
                                }
                                MarkupTool.CIRCLE -> {
                                    val left = minOf(start.x, curr.x)
                                    val top = minOf(start.y, curr.y)
                                    val right = maxOf(start.x, curr.x)
                                    val bottom = maxOf(start.y, curr.y)
                                    val center = Offset((left + right) / 2f, (top + bottom) / 2f)
                                    val radius = maxOf(right - left, bottom - top) / 2f
                                    drawCircle(
                                        color = currentColor,
                                        radius = radius,
                                        center = center,
                                        style = Stroke(width = currentStrokeWidth)
                                    )
                                }
                                else -> {}
                            }
                        }

                        // Render Text Annotations
                        textAnnotations.forEach { annotation ->
                            val textPaint = AndroidPaint().apply {
                                color = annotation.color.toArgb()
                                textSize = 32f
                                isFakeBoldText = true
                                isAntiAlias = true
                            }
                            val textWidth = textPaint.measureText(annotation.text)
                            val textHeight = 36f

                            // Draw badge background
                            val bgPaint = AndroidPaint().apply {
                                color = android.graphics.Color.argb(210, 15, 23, 42)
                                style = AndroidPaint.Style.FILL
                            }
                            val borderPaint = AndroidPaint().apply {
                                color = annotation.color.toArgb()
                                style = AndroidPaint.Style.STROKE
                                strokeWidth = 3f
                            }

                            val rect = AndroidRectF(
                                annotation.position.x - 12f,
                                annotation.position.y - textHeight - 6f,
                                annotation.position.x + textWidth + 16f,
                                annotation.position.y + 12f
                            )
                            drawContext.canvas.nativeCanvas.drawRoundRect(rect, 8f, 8f, bgPaint)
                            drawContext.canvas.nativeCanvas.drawRoundRect(rect, 8f, 8f, borderPaint)

                            drawContext.canvas.nativeCanvas.drawText(
                                annotation.text,
                                annotation.position.x,
                                annotation.position.y,
                                textPaint
                            )
                        }
                    }

                    // Watermark / Guide in corner
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color.Black.copy(alpha = 0.65f),
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "WorkForce Evidence Markup Engine • ${strokes.size + textAnnotations.size} annotations",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = WorkforceTextMuted,
                                fontSize = 10.sp
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Bottom Action Ribbon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = WorkforceTextSecondary)
                    ) {
                        Text("Use Raw Photo")
                    }

                    Button(
                        onClick = {
                            val savedPath = saveAnnotatedBitmapToDisk(
                                context = context,
                                initialPhotoPath = initialPhotoPath,
                                strokes = strokes,
                                textAnnotations = textAnnotations
                            )
                            onSaveAnnotatedPhoto(savedPath)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = WorkforceRed),
                        modifier = Modifier.testTag("btn_save_annotated_photo")
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Save & Attach to Report",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    // Modal to type custom text or pick defect tags
    if (showTextInputDialog) {
        AlertDialog(
            onDismissRequest = { showTextInputDialog = false },
            title = {
                Text(
                    text = "Add Inspection Defect Label",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = WorkforceTextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            text = {
                Column {
                    Text(
                        text = "Pick a common statutory defect tag or enter specific observation notes:",
                        style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextSecondary)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Preset tag chips
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        presetDefectTags.forEach { tag ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (textInputString == tag) WorkforceOrange else WorkforceSurfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (textInputString == tag) WorkforceOrange else WorkforceCardBorder
                                ),
                                modifier = Modifier.clickable { textInputString = tag }
                            ) {
                                Text(
                                    text = tag,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = if (textInputString == tag) Color.White else WorkforceTextSecondary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = textInputString,
                        onValueChange = { textInputString = it },
                        label = { Text("Label text") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = WorkforceOrange,
                            unfocusedBorderColor = WorkforceCardBorder,
                            focusedTextColor = WorkforceTextPrimary,
                            unfocusedTextColor = WorkforceTextPrimary
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (textInputString.isNotBlank()) {
                            textAnnotations.add(
                                TextAnnotation(
                                    text = textInputString.trim(),
                                    position = pendingTextPosition,
                                    color = currentColor
                                )
                            )
                        }
                        showTextInputDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WorkforceOrange)
                ) {
                    Text("Add Overlay")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTextInputDialog = false }) {
                    Text("Cancel", color = WorkforceTextSecondary)
                }
            },
            containerColor = WorkforceSurface
        )
    }
}

/**
 * Helper to draw directional arrow with arrowhead
 */
private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawArrow(
    start: Offset,
    end: Offset,
    color: Color,
    strokeWidth: Float
) {
    drawLine(
        color = color,
        start = start,
        end = end,
        strokeWidth = strokeWidth,
        cap = StrokeCap.Round
    )

    // Calculate arrowhead
    val angle = atan2((end.y - start.y).toDouble(), (end.x - start.x).toDouble())
    val arrowHeadLength = 24f
    val arrowAngle = Math.PI / 6

    val x1 = end.x - arrowHeadLength * cos(angle - arrowAngle).toFloat()
    val y1 = end.y - arrowHeadLength * sin(angle - arrowAngle).toFloat()
    val x2 = end.x - arrowHeadLength * cos(angle + arrowAngle).toFloat()
    val y2 = end.y - arrowHeadLength * sin(angle + arrowAngle).toFloat()

    val path = Path().apply {
        moveTo(end.x, end.y)
        lineTo(x1, y1)
        lineTo(x2, y2)
        close()
    }
    drawPath(path = path, color = color)
}

/**
 * Burn markup vector strokes and text labels onto the persistent Bitmap image and save to disk
 */
private fun saveAnnotatedBitmapToDisk(
    context: Context,
    initialPhotoPath: String,
    strokes: List<StrokePath>,
    textAnnotations: List<TextAnnotation>
): String {
    try {
        val baseFile = File(initialPhotoPath)
        val originalBitmap = if (baseFile.exists()) {
            BitmapFactory.decodeFile(baseFile.absolutePath)
        } else {
            null
        }

        val width = originalBitmap?.width ?: 1280
        val height = originalBitmap?.height ?: 960

        val mutableBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = AndroidCanvas(mutableBitmap)

        // Draw original photo or dark background
        if (originalBitmap != null) {
            canvas.drawBitmap(originalBitmap, 0f, 0f, null)
        } else {
            val bgPaint = AndroidPaint().apply {
                color = android.graphics.Color.rgb(20, 24, 33)
                style = AndroidPaint.Style.FILL
            }
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

            val textPaint = AndroidPaint().apply {
                color = android.graphics.Color.WHITE
                textSize = 36f
                isAntiAlias = true
            }
            canvas.drawText("WORKFORCE HEALTH SA EVIDENCE PHOTO", 60f, 100f, textPaint)
        }

        // Draw scale-adjusted strokes
        val paint = AndroidPaint().apply {
            isAntiAlias = true
            style = AndroidPaint.Style.STROKE
            strokeCap = AndroidPaint.Cap.ROUND
            strokeJoin = AndroidPaint.Join.ROUND
        }

        strokes.forEach { stroke ->
            paint.color = stroke.color.toArgb()
            paint.strokeWidth = stroke.strokeWidth * 1.5f

            when (stroke.tool) {
                MarkupTool.FREEHAND -> {
                    if (stroke.points.size > 1) {
                        val path = android.graphics.Path().apply {
                            moveTo(stroke.points[0].x, stroke.points[0].y)
                            for (i in 1 until stroke.points.size) {
                                lineTo(stroke.points[i].x, stroke.points[i].y)
                            }
                        }
                        canvas.drawPath(path, paint)
                    }
                }
                MarkupTool.ARROW -> {
                    canvas.drawLine(stroke.startPoint.x, stroke.startPoint.y, stroke.endPoint.x, stroke.endPoint.y, paint)
                    // Arrowhead
                    val angle = atan2((stroke.endPoint.y - stroke.startPoint.y).toDouble(), (stroke.endPoint.x - stroke.startPoint.x).toDouble())
                    val arrowHeadLength = 32f
                    val arrowAngle = Math.PI / 6
                    val x1 = stroke.endPoint.x - arrowHeadLength * cos(angle - arrowAngle).toFloat()
                    val y1 = stroke.endPoint.y - arrowHeadLength * sin(angle - arrowAngle).toFloat()
                    val x2 = stroke.endPoint.x - arrowHeadLength * cos(angle + arrowAngle).toFloat()
                    val y2 = stroke.endPoint.y - arrowHeadLength * sin(angle + arrowAngle).toFloat()

                    val headPaint = AndroidPaint().apply {
                        color = stroke.color.toArgb()
                        style = AndroidPaint.Style.FILL
                        isAntiAlias = true
                    }
                    val headPath = android.graphics.Path().apply {
                        moveTo(stroke.endPoint.x, stroke.endPoint.y)
                        lineTo(x1, y1)
                        lineTo(x2, y2)
                        close()
                    }
                    canvas.drawPath(headPath, headPaint)
                }
                MarkupTool.RECTANGLE -> {
                    val left = minOf(stroke.startPoint.x, stroke.endPoint.x)
                    val top = minOf(stroke.startPoint.y, stroke.endPoint.y)
                    val right = maxOf(stroke.startPoint.x, stroke.endPoint.x)
                    val bottom = maxOf(stroke.startPoint.y, stroke.endPoint.y)
                    canvas.drawRect(left, top, right, bottom, paint)
                }
                MarkupTool.CIRCLE -> {
                    val left = minOf(stroke.startPoint.x, stroke.endPoint.x)
                    val top = minOf(stroke.startPoint.y, stroke.endPoint.y)
                    val right = maxOf(stroke.startPoint.x, stroke.endPoint.x)
                    val bottom = maxOf(stroke.startPoint.y, stroke.endPoint.y)
                    val cx = (left + right) / 2f
                    val cy = (top + bottom) / 2f
                    val radius = maxOf(right - left, bottom - top) / 2f
                    canvas.drawCircle(cx, cy, radius, paint)
                }
                else -> {}
            }
        }

        // Draw Text Overlays
        val labelBgPaint = AndroidPaint().apply {
            color = android.graphics.Color.argb(220, 15, 23, 42)
            style = AndroidPaint.Style.FILL
        }
        val labelBorderPaint = AndroidPaint().apply {
            style = AndroidPaint.Style.STROKE
            strokeWidth = 3f
        }
        val textPaint = AndroidPaint().apply {
            textSize = 34f
            isFakeBoldText = true
            isAntiAlias = true
        }

        textAnnotations.forEach { annotation ->
            textPaint.color = annotation.color.toArgb()
            labelBorderPaint.color = annotation.color.toArgb()

            val textWidth = textPaint.measureText(annotation.text)
            val rect = AndroidRectF(
                annotation.position.x - 12f,
                annotation.position.y - 38f,
                annotation.position.x + textWidth + 16f,
                annotation.position.y + 14f
            )
            canvas.drawRoundRect(rect, 8f, 8f, labelBgPaint)
            canvas.drawRoundRect(rect, 8f, 8f, labelBorderPaint)
            canvas.drawText(annotation.text, annotation.position.x, annotation.position.y, textPaint)
        }

        // Save annotated image to file
        val outputDir = File(context.filesDir, "marked_photos").apply { mkdirs() }
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val outFile = File(outputDir, "MARKUP_$timeStamp.jpg")
        FileOutputStream(outFile).use { fos ->
            mutableBitmap.compress(Bitmap.CompressFormat.JPEG, 92, fos)
        }
        return outFile.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        return initialPhotoPath
    }
}
