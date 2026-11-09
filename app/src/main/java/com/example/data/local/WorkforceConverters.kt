package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.ActionPriority
import com.example.data.model.ActionStatus
import com.example.data.model.AssessmentStatus
import com.example.data.model.ChecklistType
import com.example.data.model.CleaningType
import com.example.data.model.ComplianceResponse
import com.example.data.model.EquipmentStatus
import com.example.data.model.SectionId
import com.example.data.model.SyncStatus
import com.example.data.model.UserRole

class WorkforceConverters {

    @TypeConverter
    fun fromAssessmentStatus(status: AssessmentStatus?): String = status?.name ?: AssessmentStatus.DRAFT.name

    @TypeConverter
    fun toAssessmentStatus(value: String?): AssessmentStatus =
        value?.let { runCatching { AssessmentStatus.valueOf(it) }.getOrDefault(AssessmentStatus.DRAFT) }
            ?: AssessmentStatus.DRAFT

    @TypeConverter
    fun fromSyncStatus(status: SyncStatus?): String = status?.name ?: SyncStatus.OFFLINE.name

    @TypeConverter
    fun toSyncStatus(value: String?): SyncStatus =
        value?.let { runCatching { SyncStatus.valueOf(it) }.getOrDefault(SyncStatus.OFFLINE) }
            ?: SyncStatus.OFFLINE

    @TypeConverter
    fun fromChecklistType(type: ChecklistType?): String = type?.name ?: ChecklistType.MALE_ABLUTION.name

    @TypeConverter
    fun toChecklistType(value: String?): ChecklistType =
        value?.let { runCatching { ChecklistType.valueOf(it) }.getOrDefault(ChecklistType.MALE_ABLUTION) }
            ?: ChecklistType.MALE_ABLUTION

    @TypeConverter
    fun fromComplianceResponse(response: ComplianceResponse?): String =
        response?.name ?: ComplianceResponse.NOT_ASSESSED.name

    @TypeConverter
    fun toComplianceResponse(value: String?): ComplianceResponse =
        value?.let { runCatching { ComplianceResponse.valueOf(it) }.getOrDefault(ComplianceResponse.NOT_ASSESSED) }
            ?: ComplianceResponse.NOT_ASSESSED

    @TypeConverter
    fun fromSectionId(section: SectionId?): String = section?.name ?: SectionId.SECTION_A.name

    @TypeConverter
    fun toSectionId(value: String?): SectionId =
        value?.let { runCatching { SectionId.valueOf(it) }.getOrDefault(SectionId.SECTION_A) }
            ?: SectionId.SECTION_A

    @TypeConverter
    fun fromCleaningType(type: CleaningType?): String = type?.name ?: CleaningType.DEEP_CLEANING.name

    @TypeConverter
    fun toCleaningType(value: String?): CleaningType =
        value?.let { runCatching { CleaningType.valueOf(it) }.getOrDefault(CleaningType.DEEP_CLEANING) }
            ?: CleaningType.DEEP_CLEANING

    @TypeConverter
    fun fromActionPriority(priority: ActionPriority?): String = priority?.name ?: ActionPriority.HIGH.name

    @TypeConverter
    fun toActionPriority(value: String?): ActionPriority =
        value?.let { runCatching { ActionPriority.valueOf(it) }.getOrDefault(ActionPriority.HIGH) }
            ?: ActionPriority.HIGH

    @TypeConverter
    fun fromActionStatus(status: ActionStatus?): String = status?.name ?: ActionStatus.OPEN.name

    @TypeConverter
    fun toActionStatus(value: String?): ActionStatus =
        value?.let { runCatching { ActionStatus.valueOf(it) }.getOrDefault(ActionStatus.OPEN) }
            ?: ActionStatus.OPEN

    @TypeConverter
    fun fromEquipmentStatus(status: EquipmentStatus?): String = status?.name ?: EquipmentStatus.CALIBRATED.name

    @TypeConverter
    fun toEquipmentStatus(value: String?): EquipmentStatus =
        value?.let { runCatching { EquipmentStatus.valueOf(it) }.getOrDefault(EquipmentStatus.CALIBRATED) }
            ?: EquipmentStatus.CALIBRATED

    @TypeConverter
    fun fromUserRole(role: UserRole?): String = role?.name ?: UserRole.FIELD_ASSESSOR.name

    @TypeConverter
    fun toUserRole(value: String?): UserRole =
        value?.let { runCatching { UserRole.valueOf(it) }.getOrDefault(UserRole.FIELD_ASSESSOR) }
            ?: UserRole.FIELD_ASSESSOR
}
