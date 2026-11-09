package com.example.data.model

enum class CleaningType(val label: String) {
    DEEP_CLEANING("Deep-Cleaning Chemicals (Q20)"),
    ROUTINE_CLEANING("Daily / Routine Cleaning Chemicals (Q22)")
}

data class ChemicalRecord(
    val id: String,
    val assessmentId: String,
    val cleaningType: CleaningType,
    val chemicalName: String,
    val manufacturerOrSupplier: String = "",
    val sdsReturned: Boolean = true,
    val sdsDocumentRef: String = "",
    val usageNotes: String = ""
)

data class CleaningProcedureStatus(
    val assessmentId: String,
    val cleaningType: CleaningType,
    val copyOfProcedureObtained: Boolean = false,
    val copyOfProcedureReturned: Boolean = false,
    val contractorOrServiceOrg: String = "",
    val remarks: String = ""
)
