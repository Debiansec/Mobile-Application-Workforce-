package com.example.data.model

enum class UserRole(val displayName: String, val badgeColorHex: Long) {
    FIELD_ASSESSOR("Occupational Hygiene Assistant", 0xFF0284C7),
    OCCUPATIONAL_HYGIENIST("Occupational Hygienist", 0xFF10B981),
    MINE_MANAGEMENT("Mine Management / Client Rep", 0xFFF59E0B),
    SYSTEM_ADMIN("System Administrator", 0xFF8B5CF6);

    companion object {
        fun fromString(value: String): UserRole {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) }
                ?: FIELD_ASSESSOR
        }
    }
}

data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: UserRole,
    val registrationNumber: String = "",
    val signatureData: String? = null,
    val password: String = "Password123!",
    val phone: String = "+27 11 892 4000",
    val department: String = "Occupational Hygiene & Safety",
    val isActive: Boolean = true
)
