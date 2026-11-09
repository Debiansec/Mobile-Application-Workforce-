package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.Assessment
import com.example.data.model.AssessmentStatus
import com.example.data.model.ChecklistType
import com.example.data.model.SyncStatus

@Entity(tableName = "assessments")
data class AssessmentEntity(
    @PrimaryKey val id: String,
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
    val previousVisitDate: String,
    val previousRemedialActionsNotes: String,
    val hygienistSpecialInstructions: String,
    val status: AssessmentStatus,
    val syncStatus: SyncStatus,
    val complianceScore: Int,
    val totalQuestions: Int,
    val answeredCount: Int,
    val nonCompliantCount: Int,
    val reviewerComments: String,
    val reviewedBy: String,
    val reviewTimestamp: String,
    val createdTimestamp: Long,
    val updatedTimestamp: Long
) {
    fun toDomain(): Assessment = Assessment(
        id = id,
        referenceNumber = referenceNumber,
        customerId = customerId,
        customerName = customerName,
        siteId = siteId,
        siteName = siteName,
        facilityId = facilityId,
        facilityName = facilityName,
        checklistType = checklistType,
        assessorId = assessorId,
        assessorName = assessorName,
        assessorRole = assessorRole,
        assessmentDate = assessmentDate,
        assessmentTime = assessmentTime,
        previousVisitDate = previousVisitDate,
        previousRemedialActionsNotes = previousRemedialActionsNotes,
        hygienistSpecialInstructions = hygienistSpecialInstructions,
        status = status,
        syncStatus = syncStatus,
        complianceScore = complianceScore,
        totalQuestions = totalQuestions,
        answeredCount = answeredCount,
        nonCompliantCount = nonCompliantCount,
        reviewerComments = reviewerComments,
        reviewedBy = reviewedBy,
        reviewTimestamp = reviewTimestamp,
        createdTimestamp = createdTimestamp,
        updatedTimestamp = updatedTimestamp
    )

    companion object {
        fun fromDomain(a: Assessment): AssessmentEntity = AssessmentEntity(
            id = a.id,
            referenceNumber = a.referenceNumber,
            customerId = a.customerId,
            customerName = a.customerName,
            siteId = a.siteId,
            siteName = a.siteName,
            facilityId = a.facilityId,
            facilityName = a.facilityName,
            checklistType = a.checklistType,
            assessorId = a.assessorId,
            assessorName = a.assessorName,
            assessorRole = a.assessorRole,
            assessmentDate = a.assessmentDate,
            assessmentTime = a.assessmentTime,
            previousVisitDate = a.previousVisitDate,
            previousRemedialActionsNotes = a.previousRemedialActionsNotes,
            hygienistSpecialInstructions = a.hygienistSpecialInstructions,
            status = a.status,
            syncStatus = a.syncStatus,
            complianceScore = a.complianceScore,
            totalQuestions = a.totalQuestions,
            answeredCount = a.answeredCount,
            nonCompliantCount = a.nonCompliantCount,
            reviewerComments = a.reviewerComments,
            reviewedBy = a.reviewedBy,
            reviewTimestamp = a.reviewTimestamp,
            createdTimestamp = a.createdTimestamp,
            updatedTimestamp = a.updatedTimestamp
        )
    }
}
