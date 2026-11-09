package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.ActionPriority
import com.example.data.model.ActionStatus
import com.example.data.model.DigitalSignature
import com.example.data.model.RemedialAction

@Entity(tableName = "remedial_actions")
data class RemedialActionEntity(
    @PrimaryKey val id: String,
    val actionReference: String,
    val assessmentId: String,
    val questionId: String,
    val questionNumber: Int,
    val sectionTitle: String,
    val findingDescription: String,
    val recommendedAction: String,
    val responsiblePersonOrDept: String,
    val targetDate: String,
    val priority: ActionPriority,
    val status: ActionStatus,
    val photoUri: String?,
    val followUpObservation: String = "",
    val closureEvidence: String = "",
    val closureDate: String = "",
    val verifiedBy: String = "",
    val closureSignature: String = "",
    val createdTimestamp: Long
) {
    fun toDomain(): RemedialAction = RemedialAction(
        id = id,
        actionReference = actionReference,
        assessmentId = assessmentId,
        questionId = questionId,
        questionNumber = questionNumber,
        sectionTitle = sectionTitle,
        findingDescription = findingDescription,
        recommendedAction = recommendedAction,
        responsiblePersonOrDept = responsiblePersonOrDept,
        targetDate = targetDate,
        priority = priority,
        status = status,
        photoUri = photoUri,
        followUpObservation = followUpObservation,
        closureEvidence = closureEvidence,
        closureDate = closureDate,
        verifiedBy = verifiedBy,
        closureSignature = closureSignature,
        createdTimestamp = createdTimestamp
    )

    companion object {
        fun fromDomain(a: RemedialAction): RemedialActionEntity = RemedialActionEntity(
            id = a.id,
            actionReference = a.actionReference,
            assessmentId = a.assessmentId,
            questionId = a.questionId,
            questionNumber = a.questionNumber,
            sectionTitle = a.sectionTitle,
            findingDescription = a.findingDescription,
            recommendedAction = a.recommendedAction,
            responsiblePersonOrDept = a.responsiblePersonOrDept,
            targetDate = a.targetDate,
            priority = a.priority,
            status = a.status,
            photoUri = a.photoUri,
            followUpObservation = a.followUpObservation,
            closureEvidence = a.closureEvidence,
            closureDate = a.closureDate,
            verifiedBy = a.verifiedBy,
            closureSignature = a.closureSignature,
            createdTimestamp = a.createdTimestamp
        )
    }
}

@Entity(tableName = "digital_signatures")
data class DigitalSignatureEntity(
    @PrimaryKey val id: String,
    val assessmentId: String,
    val signerRole: String,
    val signerName: String,
    val signerIdNumber: String,
    val signerDesignation: String,
    val signaturePath: String?,
    val signedDate: String,
    val signedTime: String,
    val declarationAccepted: Boolean
) {
    fun toDomain(): DigitalSignature = DigitalSignature(
        id = id,
        assessmentId = assessmentId,
        signerRole = signerRole,
        signerName = signerName,
        signerIdNumber = signerIdNumber,
        signerDesignation = signerDesignation,
        signaturePath = signaturePath,
        signedDate = signedDate,
        signedTime = signedTime,
        declarationAccepted = declarationAccepted
    )

    companion object {
        fun fromDomain(s: DigitalSignature): DigitalSignatureEntity = DigitalSignatureEntity(
            id = s.id,
            assessmentId = s.assessmentId,
            signerRole = s.signerRole,
            signerName = s.signerName,
            signerIdNumber = s.signerIdNumber,
            signerDesignation = s.signerDesignation,
            signaturePath = s.signaturePath,
            signedDate = s.signedDate,
            signedTime = s.signedTime,
            declarationAccepted = s.declarationAccepted
        )
    }
}
