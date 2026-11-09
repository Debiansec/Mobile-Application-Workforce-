package com.example

import com.example.data.local.ChecklistSeedData
import com.example.data.model.ActionPriority
import com.example.data.model.ActionStatus
import com.example.data.model.ComplianceResponse
import com.example.data.model.RemedialAction
import com.example.data.model.SectionId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WorkforceAssessmentTest {

    @Test
    fun testMaleAblutionChecklistHasAllSectionsAndQuestions() {
        val questions = ChecklistSeedData.createMaleAblutionQuestions("test_assessment_101")
        
        // Verifying Section counts
        val basins = questions.filter { it.sectionId == SectionId.SECTION_A }
        val showers = questions.filter { it.sectionId == SectionId.SECTION_B }
        val toilets = questions.filter { it.sectionId == SectionId.SECTION_C }
        val urinals = questions.filter { it.sectionId == SectionId.SECTION_D }
        val lockers = questions.filter { it.sectionId == SectionId.SECTION_E }
        val general = questions.filter { it.sectionId == SectionId.SECTION_F }
        val other = questions.filter { it.sectionId == SectionId.SECTION_G }

        assertEquals("Section A (Basins) must have 5 questions", 5, basins.size)
        assertEquals("Section B (Showers) must have 5 questions", 5, showers.size)
        assertEquals("Section C (Toilets) must have 7 questions", 7, toilets.size)
        assertEquals("Section D (Urinals) must have 5 questions", 5, urinals.size)
        assertEquals("Section E (Lockers) must have 5 questions", 5, lockers.size)
        assertEquals("Section F (General) must have 23 questions", 23, general.size)
        assertTrue("Section G (Other) must have at least 1 customizable question", other.isNotEmpty())
    }

    @Test
    fun testComplianceScoreCalculation() {
        val questions = ChecklistSeedData.createMaleAblutionQuestions("test_assessment_102")
        // Answer 10 with YES, 2 with NO, rest unassessed
        val answered = questions.take(12).mapIndexed { idx, q ->
            if (idx < 10) q.copy(response = ComplianceResponse.YES)
            else q.copy(response = ComplianceResponse.NO)
        }

        val compliant = answered.count { it.response == ComplianceResponse.YES }
        val nonCompliant = answered.count { it.response == ComplianceResponse.NO }
        val eligible = compliant + nonCompliant
        val score = ((compliant.toDouble() / eligible.toDouble()) * 100).toInt()

        assertEquals("Score should be 83% for 10 YES out of 12", 83, score)
    }

    @Test
    fun testMandatoryQuestionSubmissionValidation() {
        val questions = ChecklistSeedData.createMaleAblutionQuestions("test_assessment_103")
        // Initially all are NOT_ASSESSED
        val unansweredMandatory = questions.filter { it.isMandatory && it.response == ComplianceResponse.NOT_ASSESSED }
        assertTrue("Incomplete questionnaire must block submission", unansweredMandatory.isNotEmpty())

        // Fully answer all
        val fullyAnswered = questions.map { it.copy(response = ComplianceResponse.YES) }
        val remaining = fullyAnswered.filter { it.isMandatory && it.response == ComplianceResponse.NOT_ASSESSED }
        assertTrue("Fully answered questionnaire passes validation", remaining.isEmpty())
    }

    @Test
    fun testRemedialActionLifecycle() {
        val action = RemedialAction(
            id = "rem_test_01",
            actionReference = "REM-2026-991",
            assessmentId = "test_assessment_104",
            questionId = "Q_B_5_test_assessment_104",
            questionNumber = 5,
            sectionTitle = "SECTION B — SHOWERS",
            findingDescription = "Black mildew observed on grout",
            recommendedAction = "Apply fungicide and reseal silicone",
            responsiblePersonOrDept = "Plant Maintenance",
            targetDate = "2026-11-10",
            priority = ActionPriority.CRITICAL,
            status = ActionStatus.OPEN
        )

        assertEquals(ActionStatus.OPEN, action.status)
        assertEquals(ActionPriority.CRITICAL, action.priority)

        // Close action
        val closedAction = action.copy(
            status = ActionStatus.RESOLVED,
            closureEvidence = "Deep sanitised & re-grouted with anti-mould sealant",
            closureDate = "2026-11-02"
        )
        assertEquals(ActionStatus.RESOLVED, closedAction.status)
        assertTrue(closedAction.closureEvidence.isNotEmpty())
    }

    @Test
    fun testUserRoleAuthorizationAndProfileModification() {
        val initialUser = com.example.data.model.User(
            id = "user_test_99",
            name = "Sipho Dlamini",
            email = "s.dlamini@workforcehealth.co.za",
            role = com.example.data.model.UserRole.FIELD_ASSESSOR,
            registrationNumber = "SAIOH OH-Asst 2049",
            password = "Password123!",
            phone = "+27 82 123 4567",
            department = "Field Hygiene"
        )

        assertEquals("Sipho Dlamini", initialUser.name)
        assertEquals(com.example.data.model.UserRole.FIELD_ASSESSOR, initialUser.role)
        assertTrue(initialUser.isActive)

        // Modify role to OCCUPATIONAL_HYGIENIST and update legal registration
        val promotedUser = initialUser.copy(
            role = com.example.data.model.UserRole.OCCUPATIONAL_HYGIENIST,
            registrationNumber = "SAIOH ROH 0712",
            department = "Appointed Occupational Hygienist Assessment Lead",
            password = "NewStrongPassword2026!"
        )

        assertEquals(com.example.data.model.UserRole.OCCUPATIONAL_HYGIENIST, promotedUser.role)
        assertEquals("SAIOH ROH 0712", promotedUser.registrationNumber)
        assertEquals("NewStrongPassword2026!", promotedUser.password)
        assertEquals("s.dlamini@workforcehealth.co.za", promotedUser.email)
    }

    @Test
    fun testCustomerAndMineSitePropertiesTemplateStructure() {
        val customers = com.example.data.local.CustomerSeedData.customers
        val sites = com.example.data.local.CustomerSeedData.sites

        val afrimat = customers.find { it.id == "cust_afrimat" }
        org.junit.Assert.assertNotNull("Afrimat CM North must be present in customer seed data", afrimat)
        assertEquals("Afrimat CM North (Pty) Ltd", afrimat?.name)
        assertEquals("2002 / 025895 / 07", afrimat?.registrationNumber)
        assertEquals("4910205428", afrimat?.vatNumber)
        assertEquals("Afrimat Ltd", afrimat?.controlGroupName)
        assertEquals("Mr A J Van Heerden", afrimat?.ceoName)
        assertEquals("Mr C Reyneke", afrimat?.employerRepName)

        val kuipersbult = sites.find { it.id == "site_afrimat_kuipersbult" }
        org.junit.Assert.assertNotNull("Kuipersbult Quarry must be present in site seed data", kuipersbult)
        assertEquals("10023", kuipersbult?.mineCode)
        assertEquals("-23.70760° S, 27.57939° E", kuipersbult?.gpsCoordinates)
        assertEquals("LP30/5/1/2/2/10023MR", kuipersbult?.miningRightNumber)
        assertEquals("01/A42J/AGI/4378", kuipersbult?.waterLicenseNumber)
        assertEquals("Mr P K Lekgeu", kuipersbult?.mineManager)
        assertEquals("N B Letlape (SAIOH ROH)", kuipersbult?.siteOccupationalHygienist)
    }

    @Test
    fun testStandardHealthSafetyCriteriaScoring() {
        val criteria = com.example.ui.screens.getStandardHealthSafetyCriteria()
        assertEquals(16, criteria.size)

        // Simulate radio button responses: 14 Compliant (YES), 2 Non-Compliant (NO)
        val answered = criteria.mapIndexed { idx, item ->
            if (idx < 14) item.copy(response = ComplianceResponse.YES)
            else item.copy(
                response = ComplianceResponse.NO,
                remarks = "Non-compliant observation noted",
                correctiveAction = "Immediate repair required"
            )
        }

        val compliant = answered.count { it.response == ComplianceResponse.YES }
        val nonCompliant = answered.count { it.response == ComplianceResponse.NO }
        val scorePercent = (compliant.toDouble() / (compliant + nonCompliant).toDouble()) * 100.0

        assertEquals(14, compliant)
        assertEquals(2, nonCompliant)
        assertEquals(87.5, scorePercent, 0.01)
        assertTrue("87.5% satisfies the 80% MHSA pass threshold", scorePercent >= 80.0)
    }

    @Test
    fun testCompulsoryRemarksOrDictationValidationOnQuestions() {
        val questions = ChecklistSeedData.createMaleAblutionQuestions("test_assessment_105")
        
        // Initially remarks are empty
        val missingRemarks = questions.filter { it.remarks.isBlank() }
        assertEquals("Initially all questions should have blank remarks", questions.size, missingRemarks.size)

        // Fulfill remarks via typing or voice dictation
        val fulfilled = questions.mapIndexed { idx, q ->
            if (idx % 2 == 0) {
                q.copy(remarks = "Compliant as inspected", remarkInputMode = "TYPED")
            } else {
                q.copy(remarks = "Verified clean & functional via voice", remarkInputMode = "VOICE")
            }
        }

        val remainingMissing = fulfilled.filter { it.remarks.isBlank() }
        assertEquals("All questions now have compulsory remarks", 0, remainingMissing.size)
        assertTrue("Every question has non-blank remark", fulfilled.all { it.remarks.isNotBlank() })
    }
}


