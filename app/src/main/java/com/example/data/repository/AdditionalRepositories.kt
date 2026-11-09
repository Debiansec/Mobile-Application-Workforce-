package com.example.data.repository

import com.example.data.local.WorkforceDatabase
import com.example.data.local.entity.ChemicalEntity
import com.example.data.local.entity.CleaningProcedureStatusEntity
import com.example.data.local.entity.DigitalSignatureEntity
import com.example.data.local.entity.RemedialActionEntity
import com.example.data.model.ChemicalRecord
import com.example.data.model.CleaningProcedureStatus
import com.example.data.model.CleaningType
import com.example.data.model.DigitalSignature
import com.example.data.model.RemedialAction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChemicalRepository(private val db: WorkforceDatabase) {
    private val dao = db.chemicalDao()

    fun getChemicals(assessmentId: String): Flow<List<ChemicalRecord>> =
        dao.getChemicalsForAssessment(assessmentId).map { list -> list.map { it.toDomain() } }

    suspend fun addChemical(chemical: ChemicalRecord) {
        dao.insert(ChemicalEntity.fromDomain(chemical))
    }

    suspend fun deleteChemical(id: String) {
        dao.deleteById(id)
    }

    fun getProcedureStatuses(assessmentId: String): Flow<List<CleaningProcedureStatus>> =
        dao.getProcedureStatuses(assessmentId).map { list -> list.map { it.toDomain() } }

    suspend fun saveProcedureStatus(status: CleaningProcedureStatus) {
        dao.insertProcedureStatus(CleaningProcedureStatusEntity.fromDomain(status))
    }
}

class RemedialActionRepository(private val db: WorkforceDatabase) {
    private val dao = db.remedialActionDao()

    fun getAllActions(): Flow<List<RemedialAction>> =
        dao.getAllActions().map { list -> list.map { it.toDomain() } }

    fun getOutstandingActions(): Flow<List<RemedialAction>> =
        dao.getOutstandingActions().map { list -> list.map { it.toDomain() } }

    fun getActionsForAssessment(assessmentId: String): Flow<List<RemedialAction>> =
        dao.getActionsForAssessment(assessmentId).map { list -> list.map { it.toDomain() } }

    suspend fun getActionsForAssessmentSync(assessmentId: String): List<RemedialAction> =
        dao.getActionsForAssessmentSync(assessmentId).map { it.toDomain() }

    suspend fun createAction(action: RemedialAction) {
        dao.insertOrUpdate(RemedialActionEntity.fromDomain(action))
    }

    suspend fun closeAction(actionId: String, closureEvidence: String) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        dao.updateStatus(
            id = actionId,
            status = "RESOLVED",
            evidence = closureEvidence,
            closureDate = dateFormat.format(Date())
        )
    }
}

class SignatureRepository(private val db: WorkforceDatabase) {
    private val dao = db.digitalSignatureDao()

    fun getSignatures(assessmentId: String): Flow<List<DigitalSignature>> =
        dao.getSignaturesForAssessment(assessmentId).map { list -> list.map { it.toDomain() } }

    suspend fun saveSignature(signature: DigitalSignature) {
        dao.insert(DigitalSignatureEntity.fromDomain(signature))
    }
}
