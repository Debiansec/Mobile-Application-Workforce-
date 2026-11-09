package com.example.data.local

import com.example.data.model.ChecklistQuestion
import com.example.data.model.ChecklistType
import com.example.data.model.ComplianceResponse
import com.example.data.model.SectionId

/**
 * Authoritative checklist seed data matching the official WorkForce Health SA
 * Assessment Work Instruction / First Mine Report for Ablution Facilities (WFH-FMT-AF-AWI-FMR-26-001).
 *
 * Configurable for the four distinct facility worksheets:
 * 1. Monthly toilet and basin checklist Male / Female
 * 2. Monthly ablution facilities checklist Unisex
 * 3. Monthly ablution facilities checklist Male
 * 4. Monthly ablution facilities checklist Female
 */
object ChecklistSeedData {

    val basinsQuestions = listOf(
        "Are the basins clean?",
        "Are the soap dispensers filled with liquid soap?",
        "Do both water taps closing properly?",
        "Is there a sign / poster above each basin illustrating the correct way to wash your hands properly?",
        "Are there any signs of molds / yeast / mildew around or inside the basin?"
    )

    val toiletsQuestions = listOf(
        "Are the toilets clean and the floors dry?",
        "Are all toilet water bowls fitted with lids?",
        "Are all toilets fitted with seats?",
        "Are all toilets flushing properly?",
        "Is there enough toilet paper in all the toilets?",
        "Does the toilet door close properly?",
        "Are there any signs of molds / yeast / mildew?"
    )

    val urinalsQuestions = listOf(
        "Are all urinals clean?",
        "Do all the urinals flush properly?",
        "Are the floors around the urinals clean and dry?",
        "Are there any bad odours in the urinal area?",
        "Are there any signs of moulds / yeast / mildew?"
    )

    val showersQuestions = listOf(
        "Are all the showers clean?",
        "Do both water taps close properly?",
        "Are proper shower heads fitted on all the showers?",
        "Are the shower curtains / doors in good condition and closes properly providing the necessary privacy?",
        "Is there an informative poster explaining bacterial infections?"
    )

    val sanitaryBinsQuestions = listOf(
        "Are all sanitary bins clean?",
        "Do all the sanitary bins have bin liners?",
        "Are there any bad odours emanating from the sanitary bins?",
        "Are sanitary bins cleaned regularly – state frequency under remarks column?",
        "Are there any signs of molds / yeast / mildew?"
    )

    val lockersQuestions = listOf(
        "Are all employees issued with their own lockers?",
        "Are all lockers marked with their respective owner’s names and / or coy numbers?",
        "Do the doors of the lockers close properly?",
        "Can the doors of the lockers lock?",
        "Is the locker area clean?"
    )

    val generalQuestions = listOf(
        "Is there a sign on the door indicating the gender?",
        "Are there signs / posters above every basin illustrating the correct way to wash your hands properly?",
        "Are there signs / posters at the showers educating employees about the dangers of fungal infections?",
        "Is there a rubbish bin inside the facility?",
        "Is there a bin liner in the rubbish bin?",
        "Is the facility deeply cleaned / sanitised – state frequency under remarks column?",
        "Has a certificate been issued by the deep cleaning / sanitising company?",
        "Is there a cleaning schedule and register in the toilet / change house – state frequency under remarks column?",
        "Is the register signed off after cleaning and up to date?",
        "Is there a complaint / compliment book in the facility?",
        "Is the facility clean and tidy?",
        "Is personal hygiene in the facility up to standard?",
        "Do the doors close automatically and properly?",
        "Are there any bad odours in the facility?",
        "Are employees trained on the importance of personal hygiene at work – state frequency under remarks column?",
        "Has a ventilation assessment been conducted in the facility – reference date and report number in the remarks column?",
        "Has an illumination assessment been conducted in the facility – reference date and reports number in the remarks column?",
        "Review all complaints / compliments in the complaint book and log in the remarks column – if none, state this fact.",
        "List the approximate total of employees using the facility in the remarks column.",
        "Provide full details of the deep cleaning methods (procedure) and chemicals (with safety data sheets = SDS) used by the person / organisation conducting deep cleaning – log in the remarks column.",
        "Are windows in the facility of the frozen type to protect privacy in the facility – give full details in the remarks column.",
        "Obtain full list of chemicals used for daily / weekly / monthly cleaning, excluding deep cleaning, as well as copies of safety data sheets for these chemicals and the procedure for cleaning.",
        "Are the facilities disabled person friendly?"
    )

    fun createQuestionsForType(
        type: ChecklistType,
        assessmentId: String
    ): List<ChecklistQuestion> {
        val list = mutableListOf<ChecklistQuestion>()

        fun addSectionItems(sectionId: SectionId, prefix: String, items: List<String>) {
            items.forEachIndexed { index, text ->
                list.add(
                    ChecklistQuestion(
                        id = "${prefix}_${index + 1}_$assessmentId",
                        assessmentId = assessmentId,
                        sectionId = sectionId,
                        questionNumber = index + 1,
                        questionText = text,
                        response = ComplianceResponse.NOT_ASSESSED
                    )
                )
            }
        }

        when (type) {
            ChecklistType.TOILET_BASIN -> {
                addSectionItems(SectionId.SECTION_A, "Q_BASIN", basinsQuestions)
                addSectionItems(SectionId.SECTION_C, "Q_TOILET", toiletsQuestions)
                addSectionItems(SectionId.SECTION_D, "Q_URINAL", urinalsQuestions)
                addSectionItems(SectionId.SECTION_B, "Q_SANBIN", sanitaryBinsQuestions.take(4))
                addSectionItems(SectionId.SECTION_F, "Q_GEN", generalQuestions)
            }
            ChecklistType.UNISEX_ABLUTION -> {
                addSectionItems(SectionId.SECTION_A, "Q_BASIN", basinsQuestions)
                addSectionItems(SectionId.SECTION_B, "Q_SHOWER", showersQuestions)
                addSectionItems(SectionId.SECTION_C, "Q_TOILET", toiletsQuestions)
                addSectionItems(SectionId.SECTION_D, "Q_URINAL", urinalsQuestions)
                addSectionItems(SectionId.SECTION_E, "Q_LOCKER", lockersQuestions)
                addSectionItems(SectionId.SECTION_F, "Q_GEN", generalQuestions)
            }
            ChecklistType.MALE_ABLUTION -> {
                addSectionItems(SectionId.SECTION_A, "Q_BASIN", basinsQuestions)
                addSectionItems(SectionId.SECTION_B, "Q_SHOWER", showersQuestions)
                addSectionItems(SectionId.SECTION_C, "Q_TOILET", toiletsQuestions)
                addSectionItems(SectionId.SECTION_D, "Q_URINAL", urinalsQuestions)
                addSectionItems(SectionId.SECTION_E, "Q_LOCKER", lockersQuestions)
                addSectionItems(SectionId.SECTION_F, "Q_GEN", generalQuestions)
            }
            ChecklistType.FEMALE_ABLUTION -> {
                addSectionItems(SectionId.SECTION_A, "Q_BASIN", basinsQuestions)
                addSectionItems(SectionId.SECTION_B, "Q_SHOWER", showersQuestions)
                addSectionItems(SectionId.SECTION_C, "Q_TOILET", toiletsQuestions)
                addSectionItems(SectionId.SECTION_D, "Q_SANBIN", sanitaryBinsQuestions)
                addSectionItems(SectionId.SECTION_E, "Q_LOCKER", lockersQuestions)
                addSectionItems(SectionId.SECTION_F, "Q_GEN", generalQuestions)
            }
            else -> {
                return createMaleAblutionQuestions(assessmentId)
            }
        }

        // Add section G custom/walkways item
        list.add(
            ChecklistQuestion(
                id = "Q_G_1_$assessmentId",
                assessmentId = assessmentId,
                sectionId = SectionId.SECTION_G,
                questionNumber = 1,
                questionText = "Are change house external walkways and entryways free of slipping or tripping hazards?",
                response = ComplianceResponse.NOT_ASSESSED,
                allowsNA = true
            )
        )

        return list
    }

    fun createMaleAblutionQuestions(assessmentId: String): List<ChecklistQuestion> {
        return createQuestionsForType(ChecklistType.MALE_ABLUTION, assessmentId)
    }
}
