package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Assessment
import com.example.data.model.AssessmentStatus
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
import java.io.File

@Composable
fun ExportReportDialog(
    assessment: Assessment?,
    isGenerating: Boolean = false,
    savedFilePath: String? = null,
    onSaveToDownloads: () -> Unit,
    onShareExternal: () -> Unit,
    onOpenPdfViewer: () -> Unit,
    onPrintDocument: () -> Unit,
    onDismiss: () -> Unit
) {
    if (assessment == null) return

    val fileName = "${assessment.referenceNumber.replace('/', '_')}_Inspection_Report.pdf"

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .testTag("export_report_dialog"),
            shape = RoundedCornerShape(16.dp),
            color = WorkforceSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceCardBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header with PDF Icon and Dismiss
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(WorkforceRed.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PictureAsPdf,
                                contentDescription = null,
                                tint = WorkforceRed,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Export Inspection Report",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = WorkforceTextPrimary
                                )
                            )
                            Text(
                                text = "Structured Occupational Hygiene PDF",
                                style = MaterialTheme.typography.labelSmall.copy(color = WorkforceOrange)
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("btn_close_export_dialog")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = WorkforceTextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Document Metadata Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    color = WorkforceSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceCardBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = assessment.referenceNumber,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF38BDF8)
                                )
                            )
                            StatusBadge(status = assessment.status)
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = assessment.facilityName,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = WorkforceTextPrimary
                            )
                        )
                        Text(
                            text = "${assessment.customerName} • ${assessment.siteName}",
                            style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextMuted)
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = WorkforceCardBorder.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = ComplianceGreen,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Forensic GPS Stamped",
                                    style = MaterialTheme.typography.labelSmall.copy(color = ComplianceGreen)
                                )
                            }

                            Text(
                                text = "Score: ${assessment.complianceScore}%",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (assessment.complianceScore >= 80) ComplianceGreen else ComplianceRed
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Success Message Banner if already saved
                if (savedFilePath != null) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 14.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = ComplianceGreen.copy(alpha = 0.12f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ComplianceGreen.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = ComplianceGreen,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Saved locally to: $savedFilePath",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.White,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }

                // Action List
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Option 1: Save to Device Storage (Downloads)
                    ExportActionTile(
                        icon = Icons.Default.Download,
                        title = "Save to Device Storage",
                        subtitle = "Exports PDF directly to device Downloads / WorkForceHealthSA folder",
                        accentColor = WorkforceOrange,
                        isLoading = isGenerating,
                        onClick = onSaveToDownloads,
                        testTag = "btn_save_to_downloads"
                    )

                    // Option 2: Share via External Apps
                    ExportActionTile(
                        icon = Icons.Default.Share,
                        title = "Share via External Apps",
                        subtitle = "Send via WhatsApp, Gmail, Outlook, Google Drive, Bluetooth",
                        accentColor = Color(0xFF38BDF8),
                        isLoading = isGenerating,
                        onClick = onShareExternal,
                        testTag = "btn_share_external_app"
                    )

                    // Option 3: Open in PDF Viewer
                    ExportActionTile(
                        icon = Icons.Default.Visibility,
                        title = "Open in PDF Reader",
                        subtitle = "View in Google Drive PDF Viewer, Adobe Acrobat, or system viewer",
                        accentColor = ComplianceGreen,
                        isLoading = isGenerating,
                        onClick = onOpenPdfViewer,
                        testTag = "btn_open_pdf_viewer"
                    )

                    // Option 4: Print Document
                    ExportActionTile(
                        icon = Icons.Default.Print,
                        title = "Print Document",
                        subtitle = "Send to wireless printer or save as cloud print PDF",
                        accentColor = Color(0xFFA855F7),
                        isLoading = isGenerating,
                        onClick = onPrintDocument,
                        testTag = "btn_print_pdf"
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = WorkforceSurfaceVariant),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("btn_done_export")
                ) {
                    Text(
                        text = "Done",
                        color = WorkforceTextPrimary,
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

@Composable
private fun ExportActionTile(
    icon: ImageVector,
    title: String,
    subtitle: String,
    accentColor: Color,
    isLoading: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isLoading, onClick = onClick)
            .testTag(testTag),
        shape = RoundedCornerShape(10.dp),
        color = WorkforceSurfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceCardBorder)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(accentColor.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = WorkforceTextPrimary
                    )
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = WorkforceTextMuted,
                        fontSize = 11.sp
                    )
                )
            }
        }
    }
}
