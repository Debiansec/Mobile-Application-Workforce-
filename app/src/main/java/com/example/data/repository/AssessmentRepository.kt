package com.example.data.repository

import com.example.data.local.ChecklistSeedData
import com.example.data.local.WorkforceDatabase
import com.example.data.local.entity.AssessmentEntity
import com.example.data.local.entity.ChecklistAnswerEntity
import com.example.data.model.Assessment
import com.example.data.model.AssessmentStatus
import com.example.data.model.ChecklistQuestion
import com.example.data.model.ComplianceResponse
import com.example.data.model.SyncStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AssessmentRepository(private val db: WorkforceDatabase) {

    private val assessmentDao = db.assessmentDao()
    private val checklistDao = db.checklistAnswerDao()

    fun getAllAssessments(): Flow<List<Assessment>> =
        assessmentDao.getAllAssessments().map { list -> list.map { it.toDomain() } }

    fun getDraftAssessments(): Flow<List<Assessment>> =
        assessmentDao.getDraftAssessments().map { list -> list.map { it.toDomain() } }

    fun getAssessmentsAwaitingReview(): Flow<List<Assessment>> =
        assessmentDao.getAssessmentsAwaitingReview().map { list -> list.map { it.toDomain() } }

    suspend fun getAssessmentById(id: String): Assessment? =
        assessmentDao.getAssessmentById(id)?.toDomain()

    suspend fun createAssessment(assessment: Assessment): String {
        assessmentDao.insertOrUpdate(AssessmentEntity.fromDomain(assessment))
        // Populate authoritative checklist questions for this assessment
        val questions = ChecklistSeedData.createQuestionsForType(assessment.checklistType, assessment.id)
        checklistDao.insertAll(questions.map { ChecklistAnswerEntity.fromDomain(it) })
        return assessment.id
    }

    suspend fun saveAssessment(assessment: Assessment) {
        assessmentDao.insertOrUpdate(AssessmentEntity.fromDomain(assessment.copy(updatedTimestamp = System.currentTimeMillis())))
    }

    fun getQuestionsForAssessment(assessmentId: String): Flow<List<ChecklistQuestion>> =
        checklistDao.getAnswersForAssessment(assessmentId).map { list -> list.map { it.toDomain() } }

    suspend fun updateQuestion(question: ChecklistQuestion) {
        checklistDao.insertOrUpdate(ChecklistAnswerEntity.fromDomain(question))
        // Recompute stats on the assessment
        recalculateAssessmentStats(question.assessmentId)
    }

    suspend fun recalculateAssessmentStats(assessmentId: String) {
        val questions = checklistDao.getAnswersListSync(assessmentId).map { it.toDomain() }
        val total = questions.size
        val answered = questions.count { it.response != ComplianceResponse.NOT_ASSESSED }
        val nonCompliant = questions.count { it.response != ComplianceResponse.NOT_ASSESSED && it.isDefectOrNonCompliant }
        val compliant = questions.count { it.response != ComplianceResponse.NOT_ASSESSED && !it.isDefectOrNonCompliant && it.response != ComplianceResponse.NOT_APPLICABLE }

        val eligibleForScoring = compliant + nonCompliant
        val score = if (eligibleForScoring > 0) {
            ((compliant.toDouble() / eligibleForScoring.toDouble()) * 100).toInt()
        } else {
            0
        }

        val existing = assessmentDao.getAssessmentById(assessmentId)
        if (existing != null) {
            val updated = existing.copy(
                totalQuestions = total,
                answeredCount = answered,
                nonCompliantCount = nonCompliant,
                complianceScore = score,
                status = if (existing.status == AssessmentStatus.DRAFT && answered > 0) AssessmentStatus.IN_PROGRESS else existing.status,
                syncStatus = SyncStatus.PENDING_SYNC,
                updatedTimestamp = System.currentTimeMillis()
            )
            assessmentDao.update(updated)
        }
    }

    suspend fun submitForReview(assessmentId: String): Boolean {
        val existing = assessmentDao.getAssessmentById(assessmentId) ?: return false
        val questions = checklistDao.getAnswersListSync(assessmentId).map { it.toDomain() }
        val unanswered = questions.filter { it.isMandatory && it.response == ComplianceResponse.NOT_ASSESSED }
        if (unanswered.isNotEmpty()) {
            return false // Validation: required questions must not remain unanswered
        }

        val updated = existing.copy(
            status = AssessmentStatus.SUBMITTED_FOR_REVIEW,
            syncStatus = SyncStatus.PENDING_SYNC,
            updatedTimestamp = System.currentTimeMillis()
        )
        assessmentDao.update(updated)
        return true
    }

    suspend fun approveAssessment(assessmentId: String, reviewerName: String, comments: String) {
        val existing = assessmentDao.getAssessmentById(assessmentId) ?: return
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val updated = existing.copy(
            status = AssessmentStatus.APPROVED,
            reviewedBy = reviewerName,
            reviewerComments = comments,
            reviewTimestamp = dateFormat.format(Date()),
            syncStatus = SyncStatus.PENDING_SYNC,
            updatedTimestamp = System.currentTimeMillis()
        )
        assessmentDao.update(updated)
    }

    suspend fun returnForCorrection(assessmentId: String, reviewerName: String, comments: String) {
        val existing = assessmentDao.getAssessmentById(assessmentId) ?: return
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val updated = existing.copy(
            status = AssessmentStatus.RETURNED_FOR_CORRECTION,
            reviewedBy = reviewerName,
            reviewerComments = comments,
            reviewTimestamp = dateFormat.format(Date()),
            syncStatus = SyncStatus.PENDING_SYNC,
            updatedTimestamp = System.currentTimeMillis()
        )
        assessmentDao.update(updated)
    }
}
