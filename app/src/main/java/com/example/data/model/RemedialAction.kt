package com.example.data.model

enum class ActionPriority(val label: String, val colorHex: Long) {
    LOW("Low Priority", 0xFF10B981),
    MEDIUM("Medium Priority", 0xFFF59E0B),
    HIGH("High Priority", 0xFFEA580C),
    CRITICAL("Critical Regulatory Finding", 0xFFDC2626)
}

enum class ActionStatus(val label: String) {
    OPEN("Open"),
    IN_PROGRESS("In Progress"),
    RESOLVED("Resolved / Closed"),
    VERIFIED("Verified by Hygienist")
}

data class RemedialAction(
    val id: String,
    val actionReference: String,
    val assessmentId: String,
    val questionId: String,
    val questionNumber: Int,
    val sectionTitle: String,
    val findingDescription: String,
    val recommendedAction: String,
    val responsiblePersonOrDept: String,
    val targetDate: String,
    val priority: ActionPriority = ActionPriority.HIGH,
    val status: ActionStatus = ActionStatus.OPEN,
    val photoUri: String? = null,
    val followUpObservation: String = "",
    val closureEvidence: String = "",
    val closureDate: String = "",
    val verifiedBy: String = "",
    val closureSignature: String = "",
    val createdTimestamp: Long = System.currentTimeMillis()
)
