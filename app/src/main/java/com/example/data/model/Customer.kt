package com.example.data.model

data class Customer(
    val id: String,
    val name: String,
    val companyCode: String,
    val contactPerson: String,
    val email: String,
    val phone: String,
    // Attached Template Legal Structure Fields (WFH-AFR-KUI-OD-26-001)
    val tradeName: String = name,
    val registrationNumber: String = "",
    val vatNumber: String = "",
    val physicalAddress: String = "",
    val postalAddress: String = "",
    val controlGroupName: String = "",
    val controlGroupRegNumber: String = "",
    val controlGroupVatNumber: String = "",
    val ceoName: String = "",
    val ceoContact: String = "",
    val ceoEmail: String = "",
    val employerRepName: String = "",
    val employerRepPhone: String = "",
    val employerRepEmail: String = "",
    val accountsContactName: String = "",
    val accountsContactPhone: String = "",
    val accountsContactEmail: String = ""
)

data class Site(
    val id: String,
    val customerId: String,
    val siteName: String,
    val propertyDescription: String,
    val location: String,
    val province: String,
    val mineCode: String,
    val mineManager: String,
    val mineManagerContact: String,
    val siteOccupationalHygienist: String,
    val occupationalMedicalPractitioner: String,
    // Attached Template Mining Operation Fields
    val gpsCoordinates: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val magisterialDistrict: String = "",
    val commoditiesProduced: String = "Various sized aggregate and ready-mix concrete",
    val miningRightNumber: String = "",
    val waterLicenseNumber: String = "",
    val hygieneAssistant: String = "",
    val safetyOfficer: String = "",
    val residentEngineer: String = "",
    val totalWorkforce: Int = 150
)

data class Facility(
    val id: String,
    val siteId: String,
    val name: String,
    val checklistType: ChecklistType,
    val buildingDescription: String,
    val estimatedUsers: Int = 120,
    val lastAssessmentDate: String = "2026-09-15"
)

