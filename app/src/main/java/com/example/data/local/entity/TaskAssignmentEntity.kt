package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.AssessmentTask
import com.example.data.model.TaskPriority
import com.example.data.model.TaskStatus

@Entity(tableName = "task_assignments")
data class TaskAssignmentEntity(
    @PrimaryKey val id: String,
    val referenceNumber: String,
    val title: String,
    val description: String,
    val assignedStaffId: String,
    val assignedStaffName: String,
    val customerOrgId: String,
    val customerOrgName: String,
    val mineSiteId: String,
    val mineSiteName: String,
    val assessmentType: String,
    val facilityLocation: String,
    val priority: String = "NORMAL",
    val assignmentDate: String,
    val scheduledDate: String,
    val startTime: String = "08:00",
    val expectedCompletionDate: String,
    val dueDate: String,
    val instructions: String = "",
    val status: String = "ASSIGNED",
    val completionNotes: String = "",
    val linkedAssessmentId: String? = null,
    val syncStatus: String = "SYNCED",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun toDomain(): AssessmentTask {
        val parsedPriority = try {
            TaskPriority.valueOf(priority)
        } catch (_: Exception) {
            TaskPriority.NORMAL
        }
        val parsedStatus = try {
            TaskStatus.valueOf(status)
        } catch (_: Exception) {
            TaskStatus.ASSIGNED
        }

        return AssessmentTask(
            id = id,
            referenceNumber = referenceNumber,
            title = title,
            description = description,
            assignedStaffId = assignedStaffId,
            assignedStaffName = assignedStaffName,
            customerOrgId = customerOrgId,
            customerOrgName = customerOrgName,
            mineSiteId = mineSiteId,
            mineSiteName = mineSiteName,
            assessmentType = assessmentType,
            facilityLocation = facilityLocation,
            priority = parsedPriority,
            assignmentDate = assignmentDate,
            scheduledDate = scheduledDate,
            startTime = startTime,
            expectedCompletionDate = expectedCompletionDate,
            dueDate = dueDate,
            instructions = instructions,
            status = parsedStatus,
            completionNotes = completionNotes,
            linkedAssessmentId = linkedAssessmentId
        )
    }

    companion object {
        fun fromDomain(domain: AssessmentTask, syncStatus: String = "SYNCED"): TaskAssignmentEntity {
            return TaskAssignmentEntity(
                id = domain.id,
                referenceNumber = domain.referenceNumber,
                title = domain.title,
                description = domain.description,
                assignedStaffId = domain.assignedStaffId,
                assignedStaffName = domain.assignedStaffName,
                customerOrgId = domain.customerOrgId,
                customerOrgName = domain.customerOrgName,
                mineSiteId = domain.mineSiteId,
                mineSiteName = domain.mineSiteName,
                assessmentType = domain.assessmentType,
                facilityLocation = domain.facilityLocation,
                priority = domain.priority.name,
                assignmentDate = domain.assignmentDate,
                scheduledDate = domain.scheduledDate,
                startTime = domain.startTime,
                expectedCompletionDate = domain.expectedCompletionDate,
                dueDate = domain.dueDate,
                instructions = domain.instructions,
                status = domain.status.name,
                completionNotes = domain.completionNotes,
                linkedAssessmentId = domain.linkedAssessmentId,
                syncStatus = syncStatus,
                updatedAt = System.currentTimeMillis()
            )
        }
    }
}
