package com.example.data.model

enum class ComplianceResponse(val label: String) {
    YES("Yes"),
    NO("No"),
    NOT_ASSESSED("Not Assessed"),
    NOT_APPLICABLE("N/A")
}

enum class SectionId(val title: String, val subtitle: String) {
    SECTION_A("SECTION A — BASINS", "Basin hygiene, soap dispensers, taps, signage, yeast/mould"),
    SECTION_B("SECTION B — SHOWERS", "Cleanliness, water taps, showerheads, curtains/doors, privacy"),
    SECTION_C("SECTION C — TOILETS", "Toilets cleanliness, bowls, lids, seats, flush mechanism, paper"),
    SECTION_D("SECTION D — URINALS", "Urinals cleanliness, flushing, dry floors, odours, mould signs"),
    SECTION_E("SECTION E — LOCKERS", "Employee locker allocations, marking, door locking, cleanliness"),
    SECTION_F("SECTION F — GENERAL", "Gender signs, hygiene education, deep sanitising, chemicals & SDS"),
    SECTION_G("SECTION G — OTHER", "Custom and additional compliance inspection items")
}

data class ChecklistQuestion(
    val id: String,
    val assessmentId: String,
    val sectionId: SectionId,
    val questionNumber: Int,
    val questionText: String,
    val response: ComplianceResponse = ComplianceResponse.NOT_ASSESSED,
    val remarks: String = "",
    val photoUri: String? = null,
    val photoTimestamp: String? = null,
    val photoCaption: String? = null,
    val geoLatitude: Double? = null,
    val geoLongitude: Double? = null,
    val geoAccuracy: Float? = null,
    val geoTimestamp: String? = null,
    val remarkInputMode: String? = null,
    val isMandatory: Boolean = true,
    val isRemarksCompulsory: Boolean = true,
    val allowsNA: Boolean = false,
    val hasRemedialAction: Boolean = false,
    val correctiveActionNotes: String = "",
    val hasPhotoMarkup: Boolean = false,
    val photoMarkupPath: String? = null
) {
    val isRemarksSatisfied: Boolean get() = remarks.isNotBlank()

    val isSpecialYesDefect: Boolean
        get() = isSpecialYesDefectQuestion(sectionId, questionNumber)

    val isDefectOrNonCompliant: Boolean
        get() = isDefectResponse(sectionId, questionNumber, response)
}

/**
 * Checks whether the question is one of the specific criteria where observing the condition
 * (answering YES to moulds, yeast, mildew, or odours) constitutes a defect / hazard finding
 * requiring immediate remedial action:
 * - Section A Question 5: "Are there any signs of molds / yeast / mildew around or inside the basin?"
 * - Section C Question 7: "Are there any signs of molds / yeast / mildew?"
 * - Section D Question 4: "Are there any bad odours in the urinal area?"
 * - Section F Question 14: "Are there any bad odours in the facility?"
 */
fun isSpecialYesDefectQuestion(sectionId: SectionId, questionNumber: Int): Boolean {
    return (sectionId == SectionId.SECTION_A && questionNumber == 5) ||
           (sectionId == SectionId.SECTION_C && questionNumber == 7) ||
           (sectionId == SectionId.SECTION_D && questionNumber == 4) ||
           (sectionId == SectionId.SECTION_F && questionNumber == 14)
}

/**
 * Determines if a response is a non-conformance / hazard finding requiring Remedial Action:
 * - For Section A Q5, Section C Q7, Section D Q4, and Section F Q14, answering YES triggers Remedial Action.
 * - For standard compliance questions, answering NO triggers Remedial Action.
 */
fun isDefectResponse(sectionId: SectionId, questionNumber: Int, response: ComplianceResponse): Boolean {
    val triggersOnYes = isSpecialYesDefectQuestion(sectionId, questionNumber)
    return if (triggersOnYes) {
        response == ComplianceResponse.YES
    } else {
        response == ComplianceResponse.NO
    }
}


