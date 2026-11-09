package com.example.data.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class StaffRole(val displayName: String, val badgeColorHex: Long) {
    OCCUPATIONAL_HYGIENE_ASSISTANT("Occupational Hygiene Assistant", 0xFF0284C7),
    OCCUPATIONAL_HYGIENIST("Occupational Hygienist", 0xFF10B981),
    SENIOR_OCCUPATIONAL_HYGIENIST("Senior Occupational Hygienist", 0xFF059669),
    MINE_MANAGEMENT("Mine Management / Client Rep", 0xFFF59E0B),
    OPERATIONS_MANAGER("Operations Manager", 0xFFEA580C),
    SYSTEM_ADMIN("System Administrator", 0xFF8B5CF6);

    companion object {
        fun fromString(value: String): StaffRole {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) }
                ?: OCCUPATIONAL_HYGIENE_ASSISTANT
        }
    }
}

enum class StaffAvailability(val label: String, val colorHex: Long) {
    AVAILABLE("Available for Fieldwork", 0xFF16A34A),
    ON_ASSIGNMENT("On Site Assignment", 0xFF0284C7),
    TRAVELLING("In Transit / Active Trip", 0xFFF97316),
    ON_LEAVE("On Statutory Leave", 0xFF64748B),
    UNAVAILABLE("Unavailable / Maintenance", 0xFFDC2626)
}

data class StaffMember(
    val id: String,
    val fullName: String,
    val employeeNumber: String,
    val email: String,
    val phone: String,
    val jobTitle: String,
    val department: String = "Occupational Hygiene & Safety",
    val role: StaffRole,
    val qualifications: List<String> = listOf("BSc Environmental Health", "SAIOH Registration"),
    val ohRegistrationNumber: String = "",
    val assignedCustomerIds: List<String> = emptyList(),
    val assignedSiteIds: List<String> = emptyList(),
    val employmentStatus: String = "Permanent Full-time",
    val availabilityStatus: StaffAvailability = StaffAvailability.AVAILABLE,
    val workingHours: String = "07:00 - 16:30 (Mon-Fri)",
    val leaveDates: String = "None scheduled",
    val profileAvatarUrl: String? = null,
    val isActive: Boolean = true,
    val completedTasksCount: Int = 0,
    val outstandingTasksCount: Int = 0
)

enum class TaskPriority(val label: String, val colorHex: Long) {
    URGENT("Urgent", 0xFFDC2626),
    HIGH("High", 0xFFEA580C),
    NORMAL("Normal", 0xFF0284C7),
    LOW("Low", 0xFF64748B)
}

enum class TaskStatus(val label: String, val colorHex: Long) {
    NOT_ASSIGNED("Not Assigned", 0xFF64748B),
    ASSIGNED("Assigned", 0xFF0284C7),
    ACCEPTED("Accepted", 0xFF38BDF8),
    SCHEDULED("Scheduled", 0xFFF59E0B),
    CONFIRMED("Confirmed", 0xFF10B981),
    IN_PROGRESS("In Progress", 0xFFF97316),
    PENDING_REVIEW("Pending Review", 0xFF8B5CF6),
    RETURNED("Returned for Correction", 0xFFEF4444),
    COMPLETED("Completed", 0xFF16A34A),
    OVERDUE("Overdue", 0xFFDC2626),
    CANCELLED("Cancelled", 0xFF475569)
}

data class AssessmentTask(
    val id: String,
    val referenceNumber: String,
    val title: String,
    val description: String,
    val assignedStaffId: String,
    val assignedStaffName: String,
    val customerOrgId: String,
    val customerOrgName: String,
    val mineSiteId: String,
    val mineSiteName: String,
    val assessmentType: String,
    val facilityLocation: String,
    val priority: TaskPriority = TaskPriority.NORMAL,
    val assignmentDate: String,
    val scheduledDate: String,
    val startTime: String = "08:00",
    val expectedCompletionDate: String,
    val dueDate: String,
    val instructions: String = "",
    val status: TaskStatus = TaskStatus.ASSIGNED,
    val completionNotes: String = "",
    val linkedAssessmentId: String? = null
)

enum class BookingStatus(val label: String, val colorHex: Long) {
    NOT_BOOKED("Not Booked", 0xFF64748B),
    AVAILABLE("Slot Available", 0xFF10B981),
    PENDING_CONFIRMATION("Pending Confirmation", 0xFFF59E0B),
    BOOKED("Booked", 0xFF0284C7),
    CONFIRMED("Confirmed by Mine", 0xFF16A34A),
    RESCHEDULED("Rescheduled", 0xFFEA580C),
    COMPLETED("Completed", 0xFF059669),
    CANCELLED("Cancelled", 0xFFDC2626)
}

data class AssessmentBooking(
    val id: String,
    val customerId: String,
    val customerName: String,
    val siteId: String,
    val siteName: String,
    val assessmentType: String,
    val facilityLocation: String,
    val assignedAssessorId: String,
    val assignedAssessorName: String,
    val additionalTeamMembers: String = "",
    val bookingDate: String, // YYYY-MM-DD
    val arrivalTime: String = "08:00",
    val departureTime: String = "15:00",
    val estimatedDurationHours: Double = 6.0,
    val appointmentContact: String = "",
    val contactTelephone: String = "",
    val specialInstructions: String = "",
    val bookingNotes: String = "",
    val status: BookingStatus = BookingStatus.BOOKED
)

data class CustomerMineAllocation(
    val id: String,
    val assessorId: String,
    val assessorName: String,
    val customerId: String,
    val customerName: String,
    val siteId: String,
    val siteName: String,
    val allocatedDate: String,
    val allocatedBy: String = "Operations Manager",
    val notes: String = ""
)

enum class FieldServiceCategory(val label: String, val code: String) {
    ABLUTION_HYGIENE("Ablution Facilities Sanitation & Hygiene", "FS-ABL"),
    OCCUPATIONAL_HYGIENE("Statutory Occupational Hygiene Survey", "FS-OHS"),
    WORKPLACE_INSPECTION("Workplace & Change House Inspection", "FS-WKP"),
    NOISE_SURVEY("Noise Exposure & Sound Level Survey", "FS-NSE"),
    AIRBORNE_DUST("Airborne Dust & Particulate Monitoring", "FS-DST"),
    VENTILATION_SURVEY("Ventilation & Airflow Measurement", "FS-VNT"),
    LIGHTING_SURVEY("Artificial & Natural Illuminance Survey", "FS-LGT"),
    THERMAL_ENVIRONMENT("Thermal Environment & WBGT Heat Stress", "FS-THM"),
    BIOLOGICAL_MONITORING("Biological Monitoring & Potable Water", "FS-BIO"),
    FOLLOW_UP_VERIFICATION("Corrective Action Remedial Verification", "FS-VER")
}

data class FieldServiceRecord(
    val id: String,
    val customerId: String,
    val siteId: String,
    val customerName: String,
    val siteName: String,
    val category: FieldServiceCategory,
    val title: String,
    val assignedAssessorId: String,
    val assignedAssessorName: String,
    val scheduledDate: String,
    val status: String = "Scheduled",
    val notes: String = ""
)

enum class TripStatus(val label: String, val colorHex: Long) {
    SCHEDULED("Trip Scheduled", 0xFF0284C7),
    IN_PROGRESS("In Progress / Active Transit", 0xFFF97316),
    COMPLETED("Completed & Logged", 0xFF16A34A),
    CANCELLED("Cancelled", 0xFF64748B)
}

data class FieldTrip(
    val id: String,
    val assessorId: String,
    val assessorName: String,
    val customerName: String,
    val mineSiteName: String,
    val destinationAddress: String,
    val vehicleRegistration: String = "DF 42 LK GP",
    val startOdometer: Int = 142580,
    val endOdometer: Int? = null,
    val startLat: Double = -23.8211,
    val startLng: Double = 30.1633,
    val startAddress: String = "WFHSA Regional Hub, Polokwane",
    val endLat: Double? = null,
    val endLng: Double? = null,
    val endAddress: String? = null,
    val startDateTime: String,
    val endDateTime: String? = null,
    val totalDistanceKm: Double = 0.0,
    val totalDurationMinutes: Int = 0,
    val status: TripStatus = TripStatus.IN_PROGRESS,
    val tripNotes: String = "",
    val linkedAssessmentId: String? = null
)

data class LiveGpsLocation(
    val staffId: String,
    val staffName: String,
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float = 4.5f,
    val timestamp: Long = System.currentTimeMillis(),
    val address: String = "Tzaneen Mining District, Limpopo",
    val isTravelling: Boolean = false,
    val activeTripId: String? = null,
    val currentDestination: String? = null
)

enum class IncompleteReason(val label: String) {
    NOT_STARTED("Assessment Not Started"),
    INCOMPLETE_CHECKLIST("Checklist in Progress / Incomplete Responses"),
    MISSING_MANDATORY_REMARKS("Compulsory Remarks Not Captured"),
    MISSING_ASSESSOR_SIGNATURE("Missing Assessor Digital Sign-Off"),
    MISSING_EMPLOYEE_ACKNOWLEDGEMENT("Missing Employee Acknowledgement"),
    MISSING_REVIEWER_SIGNATURE("Missing Occupational Hygienist Review"),
    RETURNED_FOR_CORRECTIONS("Returned for Corrections"),
    OVERDUE_AUDIT("Audit Past Due Date"),
    UNBOOKED_APPOINTMENT("Unbooked Scheduled Visit")
}

data class IncompleteAssessmentItem(
    val assessmentId: String,
    val referenceNumber: String,
    val customerName: String,
    val siteName: String,
    val facilityName: String,
    val assignedAssessorName: String,
    val assignedDate: String,
    val scheduledDate: String,
    val dueDate: String,
    val daysOverdue: Int,
    val reasons: List<IncompleteReason>,
    val answeredCount: Int,
    val totalCount: Int,
    val score: Int
)

data class StaffNotification(
    val id: String,
    val recipientStaffId: String,
    val title: String,
    val message: String,
    val category: String, // "Task Assignment", "Booking", "Overdue", "Reminder"
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val actionTargetScreen: String? = null
) {
    val formattedTime: String
        get() = SimpleDateFormat("HH:mm • dd MMM", Locale.getDefault()).format(Date(timestamp))
}

data class AuditLogEvent(
    val id: String,
    val timestamp: Long = System.currentTimeMillis(),
    val operatorName: String,
    val action: String,
    val targetEntity: String,
    val details: String
) {
    val formattedDate: String
        get() = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(timestamp))
}
