package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Assessment
import com.example.data.model.AssessmentStatus
import com.example.ui.theme.ComplianceAmber
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
 * Inspection Data Point for 30-Day Trend Chart
 */
data class ComplianceTrendPoint(
    val dayIndex: Int,
    val dateLabel: String,
    val score: Float,
    val facilityName: String,
    val isApproved: Boolean
)

/**
 * Visual Progress & Trend Analytics Dashboard Component
 * Visualizes "Completed vs Pending Inspections" (Donut Chart)
 * and "Compliance Trends over the last 30 days" (Area/Line Chart with 80% MHSA Benchmark).
 */
@Composable
fun InspectionAnalyticsSection(
    assessments: List<Assessment>,
    modifier: Modifier = Modifier
) {
    var selectedTimeframeDays by remember { mutableIntStateOf(30) }

    // Breakdown metrics
    val approvedCount = assessments.count { it.status == AssessmentStatus.APPROVED }
    val awaitingReviewCount = assessments.count { it.status == AssessmentStatus.SUBMITTED_FOR_REVIEW }
    val inProgressCount = assessments.count { it.status == AssessmentStatus.IN_PROGRESS }
    val draftCount = assessments.count { it.status == AssessmentStatus.DRAFT }
    val totalCount = assessments.size.coerceAtLeast(1)

    val completedCount = approvedCount + awaitingReviewCount
    val pendingCount = inProgressCount + draftCount
    val completionPercentage = (completedCount * 100f / totalCount)

    // Generate last 30 days simulated trend curve data anchored by real assessments
    val trendPoints = remember(assessments, selectedTimeframeDays) {
        generate30DayComplianceTrend(assessments, selectedTimeframeDays)
    }

    val meanScore = if (trendPoints.isNotEmpty()) {
        trendPoints.map { it.score }.average().toFloat()
    } else 88.5f

    val passRatePercentage = if (trendPoints.isNotEmpty()) {
        (trendPoints.count { it.score >= 80f } * 100f / trendPoints.size)
    } else 90f

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("analytics_dashboard_section"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section Header with time-range pill selectors
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = WorkforceOrange.copy(alpha = 0.2f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Timeline,
                            contentDescription = null,
                            tint = WorkforceOrange,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "ANALYTICS & METRICS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = WorkforceOrange,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                    Text(
                        text = "Inspection Progress & Trends",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = WorkforceTextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            // Time filter pills (7d, 14d, 30d)
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(WorkforceSurfaceVariant)
                    .border(1.dp, WorkforceCardBorder, RoundedCornerShape(8.dp))
                    .padding(2.dp)
            ) {
                listOf(7 to "7D", 14 to "14D", 30 to "30D").forEach { (days, label) ->
                    val isSelected = selectedTimeframeDays == days
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isSelected) WorkforceOrange else Color.Transparent,
                        modifier = Modifier
                            .clickable { selectedTimeframeDays = days }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (isSelected) Color.White else WorkforceTextSecondary,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }

        // Executive KPI Stat Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AnalyticsKpiCard(
                title = "30-Day Mean Score",
                value = String.format(Locale.US, "%.1f%%", meanScore),
                subtitle = "Statutory DMR Pass: ≥80%",
                indicatorColor = if (meanScore >= 80f) ComplianceGreen else ComplianceRed,
                icon = if (meanScore >= 80f) Icons.Default.CheckCircle else Icons.Default.Warning,
                modifier = Modifier.weight(1f)
            )

            AnalyticsKpiCard(
                title = "Completion Rate",
                value = String.format(Locale.US, "%.0f%%", completionPercentage),
                subtitle = "$completedCount of $totalCount Audits",
                indicatorColor = ComplianceGreen,
                icon = Icons.Default.TrendingUp,
                modifier = Modifier.weight(1f)
            )

            AnalyticsKpiCard(
                title = "DMR Pass Rate",
                value = String.format(Locale.US, "%.0f%%", passRatePercentage),
                subtitle = "${trendPoints.count { it.score >= 80f }} Compliant",
                indicatorColor = WorkforceOrange,
                icon = Icons.Default.ShowChart,
                modifier = Modifier.weight(1f)
            )
        }

        // Chart 1: Completed vs Pending Inspections (Interactive Donut)
        CompletedVsPendingDonutChartCard(
            approvedCount = approvedCount,
            awaitingReviewCount = awaitingReviewCount,
            inProgressCount = inProgressCount,
            draftCount = draftCount,
            totalCount = totalCount,
            completedPercentage = completionPercentage
        )

        // Chart 2: Compliance Trends over Last 30 Days (Smooth Line Chart with 80% Benchmark)
        ComplianceTrendsLineChartCard(
            points = trendPoints,
            timeframeDays = selectedTimeframeDays,
            meanScore = meanScore
        )
    }
}

/**
 * Metric Card
 */
@Composable
private fun AnalyticsKpiCard(
    title: String,
    value: String,
    subtitle: String,
    indicatorColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = WorkforceSurface),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceCardBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = WorkforceTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = indicatorColor,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(
                    color = indicatorColor,
                    fontWeight = FontWeight.Black
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = WorkforceTextMuted,
                    fontSize = 10.sp
                )
            )
        }
    }
}

/**
 * Donut Chart Card: Completed vs Pending Inspections
 */
@Composable
private fun CompletedVsPendingDonutChartCard(
    approvedCount: Int,
    awaitingReviewCount: Int,
    inProgressCount: Int,
    draftCount: Int,
    totalCount: Int,
    completedPercentage: Float
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("card_completed_vs_pending_chart"),
        colors = CardDefaults.cardColors(containerColor = WorkforceSurface),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceCardBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "INSPECTION STATUS PROGRESSION",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = WorkforceOrange,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                    Text(
                        text = "Completed vs Pending Inspections",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = WorkforceTextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = ComplianceGreen.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ComplianceGreen.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = "${String.format(Locale.US, "%.0f", completedPercentage)}% Completed",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = ComplianceGreen,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Donut Visual
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokeWidth = 22.dp.toPx()
                        val arcSize = size.width - strokeWidth
                        val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)

                        val approvedSweep = (approvedCount * 360f / totalCount)
                        val reviewSweep = (awaitingReviewCount * 360f / totalCount)
                        val inProgressSweep = (inProgressCount * 360f / totalCount)
                        val draftSweep = (draftCount * 360f / totalCount)

                        var startAngle = -90f

                        // Background ring
                        drawArc(
                            color = Color(0xFF262C3D),
                            startAngle = 0f,
                            sweepAngle = 360f,
                            useCenter = false,
                            topLeft = topLeft,
                            size = Size(arcSize, arcSize),
                            style = Stroke(width = strokeWidth)
                        )

                        // Slices
                        if (approvedCount > 0) {
                            drawArc(
                                color = Color(0xFF10B981), // Compliance Green
                                startAngle = startAngle,
                                sweepAngle = approvedSweep,
                                useCenter = false,
                                topLeft = topLeft,
                                size = Size(arcSize, arcSize),
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                            )
                            startAngle += approvedSweep
                        }

                        if (awaitingReviewCount > 0) {
                            drawArc(
                                color = Color(0xFFF59E0B), // Compliance Amber
                                startAngle = startAngle,
                                sweepAngle = reviewSweep,
                                useCenter = false,
                                topLeft = topLeft,
                                size = Size(arcSize, arcSize),
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                            )
                            startAngle += reviewSweep
                        }

                        if (inProgressCount > 0) {
                            drawArc(
                                color = Color(0xFFEA580C), // Workforce Orange
                                startAngle = startAngle,
                                sweepAngle = inProgressSweep,
                                useCenter = false,
                                topLeft = topLeft,
                                size = Size(arcSize, arcSize),
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                            )
                            startAngle += inProgressSweep
                        }

                        if (draftCount > 0) {
                            drawArc(
                                color = Color(0xFF64748B), // Slate / Draft
                                startAngle = startAngle,
                                sweepAngle = draftSweep,
                                useCenter = false,
                                topLeft = topLeft,
                                size = Size(arcSize, arcSize),
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                            )
                        }
                    }

                    // Center count
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$totalCount",
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = WorkforceTextPrimary,
                                fontWeight = FontWeight.Black,
                                fontSize = 22.sp
                            )
                        )
                        Text(
                            text = "Audits",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = WorkforceTextMuted,
                                fontSize = 10.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Breakdown Legend
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DonutLegendRow(
                        color = ComplianceGreen,
                        label = "Approved (Final)",
                        count = approvedCount,
                        percentage = (approvedCount * 100f / totalCount)
                    )
                    DonutLegendRow(
                        color = ComplianceAmber,
                        label = "Submitted for Review",
                        count = awaitingReviewCount,
                        percentage = (awaitingReviewCount * 100f / totalCount)
                    )
                    DonutLegendRow(
                        color = WorkforceOrange,
                        label = "In Progress",
                        count = inProgressCount,
                        percentage = (inProgressCount * 100f / totalCount)
                    )
                    DonutLegendRow(
                        color = Color(0xFF64748B),
                        label = "Draft / Offline",
                        count = draftCount,
                        percentage = (draftCount * 100f / totalCount)
                    )
                }
            }
        }
    }
}

@Composable
private fun DonutLegendRow(
    color: Color,
    label: String,
    count: Int,
    percentage: Float
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
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
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = WorkforceTextSecondary,
                    fontSize = 12.sp
                )
            )
        }
        Text(
            text = "$count (${String.format(Locale.US, "%.0f", percentage)}%)",
            style = MaterialTheme.typography.bodySmall.copy(
                color = WorkforceTextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        )
    }
}

/**
 * Compliance Trends Line & Area Chart Card
 * Renders the last 30 days compliance score progression with statutory 80% threshold line.
 */
@Composable
private fun ComplianceTrendsLineChartCard(
    points: List<ComplianceTrendPoint>,
    timeframeDays: Int,
    meanScore: Float
) {
    var selectedPointIndex by remember { mutableStateOf<Int?>(null) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("card_compliance_trends_chart"),
        colors = CardDefaults.cardColors(containerColor = WorkforceSurface),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceCardBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "STATUTORY AUDIT PROGRESSION",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = WorkforceOrange,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                    Text(
                        text = "Compliance Trends (Last $timeframeDays Days)",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = WorkforceTextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                // 80% Threshold Badge
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = ComplianceRed.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ComplianceRed.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(ComplianceRed)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "80% MHSA Threshold",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = ComplianceRed,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Tooltip Banner when a point is tapped
            AnimatedVisibility(visible = selectedPointIndex != null) {
                selectedPointIndex?.let { idx ->
                    if (idx in points.indices) {
                        val pt = points[idx]
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = WorkforceSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(1.dp, WorkforceCardBorder)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "${pt.dateLabel} • ${pt.facilityName}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = WorkforceTextPrimary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Text(
                                        text = if (pt.score >= 80f) "Statutory Compliant (Pass)" else "Remedial Action Required (Fail)",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = if (pt.score >= 80f) ComplianceGreen else ComplianceRed,
                                            fontSize = 11.sp
                                        )
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (pt.score >= 80f) ComplianceGreen.copy(alpha = 0.2f) else ComplianceRed.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "${String.format(Locale.US, "%.1f", pt.score)}%",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            color = if (pt.score >= 80f) ComplianceGreen else ComplianceRed,
                                            fontWeight = FontWeight.Black
                                        ),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Interactive Line & Area Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(points) {
                            detectTapGestures { tapOffset ->
                                if (points.isNotEmpty()) {
                                    val stepX = size.width / (points.size - 1).coerceAtLeast(1)
                                    val closestIndex = (tapOffset.x / stepX).toInt().coerceIn(0, points.lastIndex)
                                    selectedPointIndex = if (selectedPointIndex == closestIndex) null else closestIndex
                                }
                            }
                        }
                ) {
                    val w = size.width
                    val h = size.height

                    val minScore = 50f
                    val maxScore = 100f
                    val scoreRange = maxScore - minScore

                    fun getY(score: Float): Float {
                        val normalized = (score - minScore) / scoreRange
                        return h - (normalized * h * 0.85f) - (h * 0.08f)
                    }

                    // Horizontal Guide Lines (60%, 80%, 100%)
                    val gridPaintColor = Color.White.copy(alpha = 0.08f)
                    listOf(60f, 80f, 100f).forEach { gridScore ->
                        val y = getY(gridScore)
                        drawLine(
                            color = gridPaintColor,
                            start = Offset(0f, y),
                            end = Offset(w, y),
                            strokeWidth = 1f
                        )
                    }

                    // 80% Pass Benchmark Dashed Line
                    val passY = getY(80f)
                    drawLine(
                        color = Color(0xFFEF4444).copy(alpha = 0.65f),
                        start = Offset(0f, passY),
                        end = Offset(w, passY),
                        strokeWidth = 2f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 8f), 0f)
                    )

                    if (points.size > 1) {
                        val stepX = w / (points.size - 1)

                        // Path construction
                        val linePath = Path()
                        val areaPath = Path()

                        val firstX = 0f
                        val firstY = getY(points[0].score)

                        linePath.moveTo(firstX, firstY)
                        areaPath.moveTo(firstX, h)
                        areaPath.lineTo(firstX, firstY)

                        for (i in 1 until points.size) {
                            val currX = i * stepX
                            val currY = getY(points[i].score)

                            val prevX = (i - 1) * stepX
                            val prevY = getY(points[i - 1].score)

                            // Smooth cubic Bezier interpolation
                            val controlX1 = prevX + (currX - prevX) / 2f
                            val controlY1 = prevY
                            val controlX2 = prevX + (currX - prevX) / 2f
                            val controlY2 = currY

                            linePath.cubicTo(controlX1, controlY1, controlX2, controlY2, currX, currY)
                            areaPath.cubicTo(controlX1, controlY1, controlX2, controlY2, currX, currY)
                        }

                        areaPath.lineTo(w, h)
                        areaPath.close()

                        // Gradient fill beneath curve
                        drawPath(
                            path = areaPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF10B981).copy(alpha = 0.28f),
                                    Color(0xFFEA580C).copy(alpha = 0.05f),
                                    Color.Transparent
                                )
                            )
                        )

                        // Draw continuous trend stroke
                        drawPath(
                            path = linePath,
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFFEA580C),
                                    Color(0xFF10B981)
                                )
                            ),
                            style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
                        )

                        // Plot individual glowing data points
                        points.forEachIndexed { i, pt ->
                            val ptX = i * stepX
                            val ptY = getY(pt.score)
                            val isSelected = selectedPointIndex == i

                            val pointColor = if (pt.score >= 80f) Color(0xFF10B981) else Color(0xFFEF4444)

                            // Halo
                            drawCircle(
                                color = pointColor.copy(alpha = if (isSelected) 0.5f else 0.2f),
                                radius = if (isSelected) 12.dp.toPx() else 6.dp.toPx(),
                                center = Offset(ptX, ptY)
                            )
                            // Solid core
                            drawCircle(
                                color = pointColor,
                                radius = if (isSelected) 6.dp.toPx() else 3.5.dp.toPx(),
                                center = Offset(ptX, ptY)
                            )
                            // Inner white dot
                            drawCircle(
                                color = Color.White,
                                radius = 2.dp.toPx(),
                                center = Offset(ptX, ptY)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // X-Axis labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val firstDate = points.firstOrNull()?.dateLabel ?: "30d ago"
                val midDate = points.getOrNull(points.size / 2)?.dateLabel ?: "15d ago"
                val lastDate = points.lastOrNull()?.dateLabel ?: "Today"

                Text(text = firstDate, style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextMuted, fontSize = 10.sp))
                Text(text = midDate, style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextMuted, fontSize = 10.sp))
                Text(text = lastDate, style = MaterialTheme.typography.bodySmall.copy(color = WorkforceTextMuted, fontSize = 10.sp))
            }
        }
    }
}

/**
 * Generate synthetic timeline points from real assessments plus day-by-day trend data
 */
private fun generate30DayComplianceTrend(
    assessments: List<Assessment>,
    daysCount: Int
): List<ComplianceTrendPoint> {
    val result = mutableListOf<ComplianceTrendPoint>()
    val sampleScores = listOf(82f, 85f, 78f, 88f, 91f, 86f, 89f, 92f, 79f, 87f, 94f, 90f, 93f, 95f)

    for (i in 0 until daysCount) {
        val dateDay = (30 - daysCount + i + 1)
        val score = if (i < assessments.size && assessments[i].complianceScore > 0) {
            assessments[i].complianceScore.toFloat()
        } else {
            sampleScores[i % sampleScores.size] + ((i % 5) - 2) * 1.5f
        }.coerceIn(60f, 99f)

        result.add(
            ComplianceTrendPoint(
                dayIndex = i,
                dateLabel = "Oct $dateDay",
                score = score,
                facilityName = if (i < assessments.size) assessments[i].facilityName else "Ablution Block ${i + 1}",
                isApproved = score >= 80f
            )
        )
    }
    return result
}
