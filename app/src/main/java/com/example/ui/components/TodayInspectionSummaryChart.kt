package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Assessment
import com.example.data.model.AssessmentStatus
import com.example.data.model.SyncStatus
import com.example.ui.theme.ComplianceAmber
import com.example.ui.theme.ComplianceGreen
import com.example.ui.theme.WorkforceCardBorder
import com.example.ui.theme.WorkforceOrange
import com.example.ui.theme.WorkforceRed
import com.example.ui.theme.WorkforceSurface
import com.example.ui.theme.WorkforceSurfaceVariant
import com.example.ui.theme.WorkforceTextMuted
import com.example.ui.theme.WorkforceTextPrimary
import com.example.ui.theme.WorkforceTextSecondary
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val SyncedCyan = Color(0xFF0284C7)
private val SyncedCyanLight = Color(0xFF38BDF8)

/**
 * Summary Component for the dashboard displaying the total number of inspections
 * Completed, Pending, and Synced today using a rich data visualization chart.
 */
@Composable
fun TodayInspectionSummaryChart(
    assessments: List<Assessment>,
    modifier: Modifier = Modifier,
    onViewPendingClick: () -> Unit = {},
    onViewCompletedClick: () -> Unit = {}
) {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    val todayStartMillis = calendar.timeInMillis

    // Filter audits from today, or fallback to active cycle assessments
    val todayAssessments = assessments.filter {
        it.createdTimestamp >= todayStartMillis || it.updatedTimestamp >= todayStartMillis
    }.ifEmpty { assessments }

    val completedToday = todayAssessments.count {
        it.status == AssessmentStatus.APPROVED || it.status == AssessmentStatus.SUBMITTED_FOR_REVIEW
    }
    val pendingToday = todayAssessments.count {
        it.status == AssessmentStatus.IN_PROGRESS || it.status == AssessmentStatus.DRAFT
    }
    val syncedToday = todayAssessments.count {
        it.syncStatus == SyncStatus.SYNCED
    }

    val totalCount = todayAssessments.size
    val safeTotal = if (totalCount == 0) 1 else totalCount

    val completedPercent = ((completedToday.toFloat() / safeTotal) * 100).toInt()
    val pendingPercent = ((pendingToday.toFloat() / safeTotal) * 100).toInt()
    val syncedPercent = ((syncedToday.toFloat() / safeTotal) * 100).toInt()

    // Animation progress for smooth entrance
    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(completedToday, pendingToday, syncedToday) {
        animProgress.snapTo(0f)
        animProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing)
        )
    }

    val todayFormatted = remember {
        SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault()).format(Date())
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("today_inspection_summary_component"),
        colors = CardDefaults.cardColors(containerColor = WorkforceSurface),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceCardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Title, Today Badge & Live Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(WorkforceRed.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Insights,
                            contentDescription = null,
                            tint = WorkforceRed,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "TODAY'S INSPECTION SUMMARY",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.8.sp,
                                color = WorkforceTextPrimary
                            )
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = WorkforceTextMuted,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = todayFormatted,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = WorkforceTextMuted,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = ComplianceGreen.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ComplianceGreen.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(ComplianceGreen)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "LIVE TELEMETRY",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = ComplianceGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Main Data Visualization: Donut Breakdown Chart + Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Interactive Multi-Segment Donut Chart
                Box(
                    modifier = Modifier
                        .size(136.dp)
                        .testTag("donut_data_visualization_chart"),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(130.dp)) {
                        val strokeWidth = 14.dp.toPx()
                        val arcRadius = (size.minDimension - strokeWidth) / 2
                        val centerOffset = Offset(size.width / 2, size.height / 2)
                        val arcSize = Size(arcRadius * 2, arcRadius * 2)
                        val topLeft = Offset(centerOffset.x - arcRadius, centerOffset.y - arcRadius)

                        // Background Track
                        drawCircle(
                            color = WorkforceSurfaceVariant,
                            radius = arcRadius,
                            center = centerOffset,
                            style = Stroke(width = strokeWidth)
                        )

                        if (totalCount > 0) {
                            val factor = animProgress.value
                            val completedSweep = (completedToday.toFloat() / safeTotal) * 360f * factor
                            val pendingSweep = (pendingToday.toFloat() / safeTotal) * 360f * factor
                            val syncedSweep = (syncedToday.toFloat() / safeTotal) * 360f * factor

                            var startAngle = -90f

                            // Segment 1: Completed Today (Green)
                            if (completedSweep > 0f) {
                                drawArc(
                                    brush = Brush.sweepGradient(
                                        listOf(ComplianceGreen, Color(0xFF22C55E))
                                    ),
                                    startAngle = startAngle,
                                    sweepAngle = completedSweep.coerceAtMost(360f),
                                    useCenter = false,
                                    topLeft = topLeft,
                                    size = arcSize,
                                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                )
                                startAngle += completedSweep
                            }

                            // Segment 2: Pending Today (Amber / Orange)
                            if (pendingSweep > 0f) {
                                drawArc(
                                    brush = Brush.sweepGradient(
                                        listOf(WorkforceOrange, ComplianceAmber)
                                    ),
                                    startAngle = startAngle,
                                    sweepAngle = pendingSweep.coerceAtMost(360f),
                                    useCenter = false,
                                    topLeft = topLeft,
                                    size = arcSize,
                                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                )
                                startAngle += pendingSweep
                            }

                            // Segment 3: Synced Today Inner Accent Ring
                            val innerStroke = 4.dp.toPx()
                            val innerRadius = arcRadius - strokeWidth * 0.9f
                            val innerTopLeft = Offset(centerOffset.x - innerRadius, centerOffset.y - innerRadius)
                            val innerSize = Size(innerRadius * 2, innerRadius * 2)

                            drawArc(
                                color = SyncedCyanLight,
                                startAngle = -90f,
                                sweepAngle = syncedSweep.coerceAtMost(360f),
                                useCenter = false,
                                topLeft = innerTopLeft,
                                size = innerSize,
                                style = Stroke(width = innerStroke, cap = StrokeCap.Round)
                            )
                        }
                    }

                    // Center Metric Label
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "$totalCount",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = WorkforceTextPrimary,
                                fontSize = 28.sp,
                                lineHeight = 28.sp
                            )
                        )
                        Text(
                            text = "TOTAL AUDITS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = WorkforceTextMuted,
                                fontSize = 9.sp,
                                letterSpacing = 0.5.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                // Breakdown Indicators
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ChartBreakdownLegendRow(
                        title = "Completed Today",
                        count = completedToday,
                        percentage = completedPercent,
                        color = ComplianceGreen,
                        icon = Icons.Default.CheckCircle,
                        testTag = "summary_legend_completed"
                    )

                    ChartBreakdownLegendRow(
                        title = "Pending Today",
                        count = pendingToday,
                        percentage = pendingPercent,
                        color = WorkforceOrange,
                        icon = Icons.Default.PendingActions,
                        testTag = "summary_legend_pending"
                    )

                    ChartBreakdownLegendRow(
                        title = "Synced Today",
                        count = syncedToday,
                        percentage = syncedPercent,
                        color = SyncedCyanLight,
                        icon = Icons.Default.CloudDone,
                        testTag = "summary_legend_synced"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Comparative Proportional Bar Chart
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Today's Work Velocity Distribution",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = WorkforceTextSecondary,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Text(
                        text = "$completedPercent% Complete",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = ComplianceGreen,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                // Stacked horizontal bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(WorkforceSurfaceVariant)
                ) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        val factor = animProgress.value
                        if (completedToday > 0) {
                            Box(
                                modifier = Modifier
                                    .weight((completedToday.toFloat() * factor).coerceAtLeast(0.01f))
                                    .height(10.dp)
                                    .background(ComplianceGreen)
                            )
                        }
                        if (pendingToday > 0) {
                            Box(
                                modifier = Modifier
                                    .weight((pendingToday.toFloat() * factor).coerceAtLeast(0.01f))
                                    .height(10.dp)
                                    .background(WorkforceOrange)
                            )
                        }
                        if (totalCount == 0) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(10.dp)
                                    .background(WorkforceSurfaceVariant)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Three Quick-Action Operational Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TodaySummaryMetricCard(
                    title = "Completed",
                    count = completedToday.toString(),
                    percentage = "$completedPercent%",
                    accentColor = ComplianceGreen,
                    icon = Icons.Default.CheckCircle,
                    modifier = Modifier.weight(1f),
                    testTag = "today_metric_completed"
                )

                TodaySummaryMetricCard(
                    title = "Pending",
                    count = pendingToday.toString(),
                    percentage = "$pendingPercent%",
                    accentColor = WorkforceOrange,
                    icon = Icons.Default.PendingActions,
                    modifier = Modifier.weight(1f),
                    testTag = "today_metric_pending"
                )

                TodaySummaryMetricCard(
                    title = "Synced",
                    count = syncedToday.toString(),
                    percentage = "$syncedPercent%",
                    accentColor = SyncedCyanLight,
                    icon = Icons.Default.CloudDone,
                    modifier = Modifier.weight(1f),
                    testTag = "today_metric_synced"
                )
            }
        }
    }
}

@Composable
private fun ChartBreakdownLegendRow(
    title: String,
    count: Int,
    percentage: Int,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    testTag: String
) {
    Surface(
        color = WorkforceSurfaceVariant,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.25f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 7.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(color)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = WorkforceTextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$count",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Black,
                        color = color,
                        fontSize = 13.sp
                    )
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "($percentage%)",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = WorkforceTextMuted,
                        fontSize = 10.sp
                    )
                )
            }
        }
    }
}

@Composable
private fun TodaySummaryMetricCard(
    title: String,
    count: String,
    percentage: String,
    accentColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    testTag: String
) {
    Surface(
        modifier = modifier.testTag(testTag),
        color = WorkforceSurfaceVariant,
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = count,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = WorkforceTextPrimary,
                    fontSize = 18.sp
                )
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = WorkforceTextSecondary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                ),
                maxLines = 1
            )
            Surface(
                color = accentColor.copy(alpha = 0.15f),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Text(
                    text = percentage,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = accentColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp
                    )
                )
            }
        }
    }
}
