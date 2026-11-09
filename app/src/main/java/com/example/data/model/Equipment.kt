package com.example.data.model

data class DigitalSignature(
    val id: String,
    val assessmentId: String,
    val signerRole: String, // "Field Assessor" or "Client / Mine Representative"
    val signerName: String,
    val signerIdNumber: String = "",
    val signerDesignation: String = "",
    val signaturePath: String? = null, // Cached vector stroke path or Base64 PNG
    val signedDate: String,
    val signedTime: String,
    val declarationAccepted: Boolean = true
)

enum class EquipmentStatus(val label: String, val colorHex: Long) {
    CALIBRATED("Calibrated & Valid", 0xFF10B981),
    CALIBRATION_DUE("Calibration Due Soon", 0xFFF59E0B),
    EXPIRED("Calibration Expired", 0xFFEF4444)
}

data class Equipment(
    val id: String,
    val name: String,
    val manufacturer: String,
    val model: String,
    val serialNumber: String,
    val assetTag: String,
    val lastCalibrationDate: String,
    val calibrationDueDate: String,
    val status: EquipmentStatus,
    val certificateRef: String = ""
)

data class LabSample(
    val id: String,
    val assessmentId: String,
    val sampleReference: String,
    val sampleType: String, // "Potable Water", "Swab - Fungal/Yeast", "Airborne Dust"
    val collectionDate: String,
    val collectionTime: String,
    val sampledLocation: String,
    val chainOfCustodyStatus: String,
    val labName: String,
    val resultSummary: String = "Pending Lab Analysis",
    val isApprovedByHygienist: Boolean = false
)
