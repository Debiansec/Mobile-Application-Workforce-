package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.ChecklistQuestion
import com.example.data.model.ComplianceResponse
import com.example.data.model.SectionId

@Entity(tableName = "checklist_answers")
data class ChecklistAnswerEntity(
    @PrimaryKey val id: String,
    val assessmentId: String,
    val sectionId: SectionId,
    val questionNumber: Int,
    val questionText: String,
    val response: ComplianceResponse,
    val remarks: String,
    val photoUri: String?,
    val photoTimestamp: String?,
    val photoCaption: String?,
    val geoLatitude: Double?,
    val geoLongitude: Double?,
    val geoAccuracy: Float? = null,
    val geoTimestamp: String? = null,
    val remarkInputMode: String? = null,
    val isMandatory: Boolean,
    val allowsNA: Boolean,
    val hasRemedialAction: Boolean,
    val correctiveActionNotes: String
) {
    fun toDomain(): ChecklistQuestion = ChecklistQuestion(
        id = id,
        assessmentId = assessmentId,
        sectionId = sectionId,
        questionNumber = questionNumber,
        questionText = questionText,
        response = response,
        remarks = remarks,
        photoUri = photoUri,
        photoTimestamp = photoTimestamp,
        photoCaption = photoCaption,
        geoLatitude = geoLatitude,
        geoLongitude = geoLongitude,
        geoAccuracy = geoAccuracy,
        geoTimestamp = geoTimestamp,
        remarkInputMode = remarkInputMode,
        isMandatory = isMandatory,
        allowsNA = allowsNA,
        hasRemedialAction = hasRemedialAction,
        correctiveActionNotes = correctiveActionNotes
    )

    companion object {
        fun fromDomain(q: ChecklistQuestion): ChecklistAnswerEntity = ChecklistAnswerEntity(
            id = q.id,
            assessmentId = q.assessmentId,
            sectionId = q.sectionId,
            questionNumber = q.questionNumber,
            questionText = q.questionText,
            response = q.response,
            remarks = q.remarks,
            photoUri = q.photoUri,
            photoTimestamp = q.photoTimestamp,
            photoCaption = q.photoCaption,
            geoLatitude = q.geoLatitude,
            geoLongitude = q.geoLongitude,
            geoAccuracy = q.geoAccuracy,
            geoTimestamp = q.geoTimestamp,
            remarkInputMode = q.remarkInputMode,
            isMandatory = q.isMandatory,
            allowsNA = q.allowsNA,
            hasRemedialAction = q.hasRemedialAction,
            correctiveActionNotes = q.correctiveActionNotes
        )
    }
}
