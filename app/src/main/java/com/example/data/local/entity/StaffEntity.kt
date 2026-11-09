package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.StaffAvailability
import com.example.data.model.StaffMember
import com.example.data.model.StaffRole

@Entity(tableName = "staff_profiles")
data class StaffEntity(
    @PrimaryKey val id: String,
    val fullName: String,
    val employeeNumber: String,
    val email: String,
    val phone: String,
    val jobTitle: String,
    val department: String = "Occupational Hygiene & Safety",
    val role: String,
    val qualifications: String = "BSc Environmental Health, SAIOH Registration",
    val ohRegistrationNumber: String = "",
    val assignedCustomerIds: String = "",
    val assignedSiteIds: String = "",
    val employmentStatus: String = "Permanent Full-time",
    val availabilityStatus: String = "AVAILABLE",
    val workingHours: String = "07:00 - 16:30 (Mon-Fri)",
    val leaveDates: String = "None scheduled",
    val profileAvatarUrl: String? = null,
    val isActive: Boolean = true,
    val completedTasksCount: Int = 0,
    val outstandingTasksCount: Int = 0,
    val syncStatus: String = "SYNCED",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun toDomain(): StaffMember {
        val parsedRole = StaffRole.fromString(role)
        val parsedAvailability = try {
            StaffAvailability.valueOf(availabilityStatus)
        } catch (_: Exception) {
            StaffAvailability.AVAILABLE
        }
        val qualList = qualifications.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        val custIds = assignedCustomerIds.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        val siteIds = assignedSiteIds.split(",").map { it.trim() }.filter { it.isNotEmpty() }

        return StaffMember(
            id = id,
            fullName = fullName,
            employeeNumber = employeeNumber,
            email = email,
            phone = phone,
            jobTitle = jobTitle,
            department = department,
            role = parsedRole,
            qualifications = if (qualList.isEmpty()) listOf("BSc Environmental Health") else qualList,
            ohRegistrationNumber = ohRegistrationNumber,
            assignedCustomerIds = custIds,
            assignedSiteIds = siteIds,
            employmentStatus = employmentStatus,
            availabilityStatus = parsedAvailability,
            workingHours = workingHours,
            leaveDates = leaveDates,
            profileAvatarUrl = profileAvatarUrl,
            isActive = isActive,
            completedTasksCount = completedTasksCount,
            outstandingTasksCount = outstandingTasksCount
        )
    }

    companion object {
        fun fromDomain(domain: StaffMember, syncStatus: String = "SYNCED"): StaffEntity {
            return StaffEntity(
                id = domain.id,
                fullName = domain.fullName,
                employeeNumber = domain.employeeNumber,
                email = domain.email.trim().lowercase(),
                phone = domain.phone.trim(),
                jobTitle = domain.jobTitle,
                department = domain.department,
                role = domain.role.name,
                qualifications = domain.qualifications.joinToString(", "),
                ohRegistrationNumber = domain.ohRegistrationNumber.trim(),
                assignedCustomerIds = domain.assignedCustomerIds.joinToString(","),
                assignedSiteIds = domain.assignedSiteIds.joinToString(","),
                employmentStatus = domain.employmentStatus,
                availabilityStatus = domain.availabilityStatus.name,
                workingHours = domain.workingHours,
                leaveDates = domain.leaveDates,
                profileAvatarUrl = domain.profileAvatarUrl,
                isActive = domain.isActive,
                completedTasksCount = domain.completedTasksCount,
                outstandingTasksCount = domain.outstandingTasksCount,
                syncStatus = syncStatus,
                updatedAt = System.currentTimeMillis()
            )
        }
    }
}
