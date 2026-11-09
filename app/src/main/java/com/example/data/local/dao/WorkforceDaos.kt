package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.AssessmentEntity
import com.example.data.local.entity.ChecklistAnswerEntity
import com.example.data.local.entity.ChemicalEntity
import com.example.data.local.entity.CleaningProcedureStatusEntity
import com.example.data.local.entity.DigitalSignatureEntity
import com.example.data.local.entity.RemedialActionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AssessmentDao {
    @Query("SELECT * FROM assessments ORDER BY updatedTimestamp DESC")
    fun getAllAssessments(): Flow<List<AssessmentEntity>>

    @Query("SELECT * FROM assessments WHERE id = :id LIMIT 1")
    suspend fun getAssessmentById(id: String): AssessmentEntity?

    @Query("SELECT * FROM assessments WHERE status = 'DRAFT' OR status = 'IN_PROGRESS' ORDER BY updatedTimestamp DESC")
    fun getDraftAssessments(): Flow<List<AssessmentEntity>>

    @Query("SELECT * FROM assessments WHERE status = 'SUBMITTED_FOR_REVIEW' ORDER BY updatedTimestamp DESC")
    fun getAssessmentsAwaitingReview(): Flow<List<AssessmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(assessment: AssessmentEntity)

    @Update
    suspend fun update(assessment: AssessmentEntity)

    @Query("DELETE FROM assessments WHERE id = :id")
    suspend fun deleteById(id: String)
}

@Dao
interface ChecklistAnswerDao {
    @Query("SELECT * FROM checklist_answers WHERE assessmentId = :assessmentId ORDER BY sectionId ASC, questionNumber ASC")
    fun getAnswersForAssessment(assessmentId: String): Flow<List<ChecklistAnswerEntity>>

    @Query("SELECT * FROM checklist_answers WHERE assessmentId = :assessmentId ORDER BY sectionId ASC, questionNumber ASC")
    suspend fun getAnswersListSync(assessmentId: String): List<ChecklistAnswerEntity>

    @Query("SELECT * FROM checklist_answers WHERE id = :id LIMIT 1")
    suspend fun getAnswerById(id: String): ChecklistAnswerEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(answer: ChecklistAnswerEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(answers: List<ChecklistAnswerEntity>)

    @Query("DELETE FROM checklist_answers WHERE assessmentId = :assessmentId")
    suspend fun deleteForAssessment(assessmentId: String)
}

@Dao
interface ChemicalDao {
    @Query("SELECT * FROM chemicals WHERE assessmentId = :assessmentId")
    fun getChemicalsForAssessment(assessmentId: String): Flow<List<ChemicalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(chemical: ChemicalEntity)

    @Query("DELETE FROM chemicals WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT * FROM cleaning_procedure_status WHERE assessmentId = :assessmentId")
    fun getProcedureStatuses(assessmentId: String): Flow<List<CleaningProcedureStatusEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProcedureStatus(status: CleaningProcedureStatusEntity)
}

@Dao
interface RemedialActionDao {
    @Query("SELECT * FROM remedial_actions ORDER BY createdTimestamp DESC")
    fun getAllActions(): Flow<List<RemedialActionEntity>>

    @Query("SELECT * FROM remedial_actions WHERE assessmentId = :assessmentId ORDER BY createdTimestamp DESC")
    fun getActionsForAssessment(assessmentId: String): Flow<List<RemedialActionEntity>>

    @Query("SELECT * FROM remedial_actions WHERE assessmentId = :assessmentId ORDER BY createdTimestamp DESC")
    suspend fun getActionsForAssessmentSync(assessmentId: String): List<RemedialActionEntity>

    @Query("SELECT * FROM remedial_actions WHERE status != 'RESOLVED' AND status != 'VERIFIED' ORDER BY createdTimestamp DESC")
    fun getOutstandingActions(): Flow<List<RemedialActionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(action: RemedialActionEntity)

    @Query("UPDATE remedial_actions SET status = :status, closureEvidence = :evidence, closureDate = :closureDate WHERE id = :id")
    suspend fun updateStatus(id: String, status: String, evidence: String, closureDate: String)
}

@Dao
interface DigitalSignatureDao {
    @Query("SELECT * FROM digital_signatures WHERE assessmentId = :assessmentId")
    fun getSignaturesForAssessment(assessmentId: String): Flow<List<DigitalSignatureEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(signature: DigitalSignatureEntity)
}
