package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.ChemicalRecord
import com.example.data.model.CleaningProcedureStatus
import com.example.data.model.CleaningType

@Entity(tableName = "chemicals")
data class ChemicalEntity(
    @PrimaryKey val id: String,
    val assessmentId: String,
    val cleaningType: CleaningType,
    val chemicalName: String,
    val manufacturerOrSupplier: String,
    val sdsReturned: Boolean,
    val sdsDocumentRef: String,
    val usageNotes: String
) {
    fun toDomain(): ChemicalRecord = ChemicalRecord(
        id = id,
        assessmentId = assessmentId,
        cleaningType = cleaningType,
        chemicalName = chemicalName,
        manufacturerOrSupplier = manufacturerOrSupplier,
        sdsReturned = sdsReturned,
        sdsDocumentRef = sdsDocumentRef,
        usageNotes = usageNotes
    )

    companion object {
        fun fromDomain(c: ChemicalRecord): ChemicalEntity = ChemicalEntity(
            id = c.id,
            assessmentId = c.assessmentId,
            cleaningType = c.cleaningType,
            chemicalName = c.chemicalName,
            manufacturerOrSupplier = c.manufacturerOrSupplier,
            sdsReturned = c.sdsReturned,
            sdsDocumentRef = c.sdsDocumentRef,
            usageNotes = c.usageNotes
        )
    }
}

@Entity(tableName = "cleaning_procedure_status")
data class CleaningProcedureStatusEntity(
    @PrimaryKey val compositeKey: String, // assessmentId + "_" + cleaningType.name
    val assessmentId: String,
    val cleaningType: CleaningType,
    val copyOfProcedureObtained: Boolean,
    val copyOfProcedureReturned: Boolean,
    val contractorOrServiceOrg: String,
    val remarks: String
) {
    fun toDomain(): CleaningProcedureStatus = CleaningProcedureStatus(
        assessmentId = assessmentId,
        cleaningType = cleaningType,
        copyOfProcedureObtained = copyOfProcedureObtained,
        copyOfProcedureReturned = copyOfProcedureReturned,
        contractorOrServiceOrg = contractorOrServiceOrg,
        remarks = remarks
    )

    companion object {
        fun fromDomain(p: CleaningProcedureStatus): CleaningProcedureStatusEntity =
            CleaningProcedureStatusEntity(
                compositeKey = "${p.assessmentId}_${p.cleaningType.name}",
                assessmentId = p.assessmentId,
                cleaningType = p.cleaningType,
                copyOfProcedureObtained = p.copyOfProcedureObtained,
                copyOfProcedureReturned = p.copyOfProcedureReturned,
                contractorOrServiceOrg = p.contractorOrServiceOrg,
                remarks = p.remarks
            )
    }
}
