package com.example.data.model

enum class AssessmentStatus(val label: String) {
    DRAFT("Draft"),
    IN_PROGRESS("In Progress"),
    SUBMITTED_FOR_REVIEW("Submitted for Review"),
    RETURNED_FOR_CORRECTION("Returned for Correction"),
    APPROVED("Approved"),
    ARCHIVED("Archived")
}

enum class SyncStatus(val label: String) {
    SYNCED("Cloud Synced"),
    PENDING_SYNC("Pending Sync"),
    SYNCING("Syncing..."),
    OFFLINE("Offline Cache"),
    FAILED("Sync Failed")
}

enum class ChecklistType(val displayName: String) {
    MALE_ABLUTION("Monthly ablution facilities checklist *Male"),
    FEMALE_ABLUTION("Monthly ablution facilities checklist *Female"),
    UNISEX_ABLUTION("Monthly ablution facilities checklist *Unisex"),
    TOILET_BASIN("Monthly toilet and basin checklist *Male / Female"),
    ILLUMINATION_DAY_NIGHT("Artificial Illumination — Day / Night"),
    NOISE_AREA_SOURCE("Area and Source Noise"),
    POTABLE_WATER_PART_A("Potable Water — Part A"),
    POTABLE_WATER_PART_B("Potable Water — Part B"),
    THERMAL_STRESS_WBGT("Thermal Stress Assessment")
}

data class EnvironmentalConditions(
    val startShiftTime: String = "07:30",
    val startWetBulb: String = "21.4",
    val startDryBulb: String = "24.8",
    val startGlobe: String = "25.2",
    val startWbgt: String = "22.5",
    val startHumidity: String = "68",
    val startWindSpeed: String = "1.2",
    val startCondition: String = "Clear, mild",

    val midShiftTime: String = "11:30",
    val midWetBulb: String = "23.8",
    val midDryBulb: String = "28.5",
    val midGlobe: String = "29.1",
    val midWbgt: String = "25.4",
    val midHumidity: String = "62",
    val midWindSpeed: String = "1.8",
    val midCondition: String = "Sunny, warm",

    val endShiftTime: String = "15:30",
    val endWetBulb: String = "22.9",
    val endDryBulb: String = "27.2",
    val endGlobe: String = "27.6",
    val endWbgt: String = "24.3",
    val endHumidity: String = "64",
    val endWindSpeed: String = "1.5",
    val endCondition: String = "Scattered clouds",

    val wbgtEquipment: String = "WBGT Logger model AZ 87786",
    val anemometerEquipment: String = "Kestrell pocket weather tracker model 4000"
)

data class Assessment(
    val id: String,
    val referenceNumber: String,
    val customerId: String,
    val customerName: String,
    val siteId: String,
    val siteName: String,
    val facilityId: String,
    val facilityName: String,
    val checklistType: ChecklistType,
    val assessorId: String,
    val assessorName: String,
    val assessorRole: String,
    val assessmentDate: String,
    val assessmentTime: String,
    val docNumber: String = "WFH-FMT-AF-AWI-FMR-26-001 – Part 1",
    val propertyDescription: String = "Portion 10 of the farm Goudmyn 337KT, STEELPOORT, Limpopo Province",
    val dmeMineCode: String = "10383",
    val mineManager: String = "G. van der Merwe",
    val mineManagerEmail: String = "mine.manager@gracelica.co.za",
    val mineManagerMobile: String = "+27 (0)13 230 4000",
    val siteOccupationalHygienist: String = "Mr N B Letlape",
    val siteOccupationalHygienistEmail: String = "ChiefOH@workforcehealthsa.co.za",
    val siteOccupationalHygienistMobile: String = "+27 (0)83 456 2673",
    val occupationalMedicalPractitioner: String = "Dr K. Mokoena (OMP 9921)",
    val appointedInstructorName: String = "R. Steyn (ROH 0482)",
    val appointedInstructorSignature: String = "Appointed Occupational Hygienist (Assessment instructor)",
    val appointedAssessorName: String = "Mr R R M Taukobong",
    val appointedAssessorSignature: String = "Appointed Occupational Hygiene Assistant (Assessor)",
    val previousVisitDate: String = "2025-05-27",
    val previousRemedialActionsNotes: String = "None, first visit for 2026.",
    val hygienistSpecialInstructionDate: String = "2026-02-03",
    val hygienistSpecialInstructions: String = "None.",
    val environmentalConditions: EnvironmentalConditions = EnvironmentalConditions(),
    val fieldOfficerObservations: String = "Ablution facilities generally maintained in good hygienic state. Deep cleaning procedures and SDS files verified on site.",
    val status: AssessmentStatus = AssessmentStatus.DRAFT,
    val syncStatus: SyncStatus = SyncStatus.OFFLINE,
    val complianceScore: Int = 0,
    val totalQuestions: Int = 0,
    val answeredCount: Int = 0,
    val nonCompliantCount: Int = 0,
    val reviewerComments: String = "",
    val reviewedBy: String = "",
    val reviewTimestamp: String = "",
    val createdTimestamp: Long = System.currentTimeMillis(),
    val updatedTimestamp: Long = System.currentTimeMillis()
)
