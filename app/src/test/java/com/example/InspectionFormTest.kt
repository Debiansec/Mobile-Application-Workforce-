package com.example

import com.example.data.model.ComplianceResponse
import com.example.ui.components.getDefaultInspectionQuestions
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class InspectionFormTest {

    @Test
    fun testDefaultInspectionQuestionsLoad() {
        val questions = getDefaultInspectionQuestions("test_assessment_1")
        assertEquals(12, questions.size)
        // All default questions start with YES (100% compliant)
        assertTrue(questions.all { it.response == ComplianceResponse.YES })
    }

    @Test
    fun testAuditScoringCalculation() {
        val questions = getDefaultInspectionQuestions("test_assessment_2").toMutableList()
        // Mark 2 questions as NO
        questions[0] = questions[0].copy(response = ComplianceResponse.NO)
        questions[1] = questions[1].copy(response = ComplianceResponse.NO)

        val passCount = questions.count { it.response == ComplianceResponse.YES }
        val failCount = questions.count { it.response == ComplianceResponse.NO }
        val totalEvaluated = passCount + failCount

        val score = (passCount.toDouble() / totalEvaluated.toDouble()) * 100.0

        assertEquals(10, passCount)
        assertEquals(2, failCount)
        // 10 / 12 * 100 = 83.33% (Passes DMR 80% threshold)
        assertTrue(score >= 80.0)
    }
}
