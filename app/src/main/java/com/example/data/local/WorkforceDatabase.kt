package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.local.dao.AssessmentDao
import com.example.data.local.dao.ChecklistAnswerDao
import com.example.data.local.dao.ChemicalDao
import com.example.data.local.dao.DigitalSignatureDao
import com.example.data.local.dao.RemedialActionDao
import com.example.data.local.dao.StaffDao
import com.example.data.local.dao.TaskAssignmentDao
import com.example.data.local.dao.UserDao
import com.example.data.local.entity.AssessmentEntity
import com.example.data.local.entity.ChecklistAnswerEntity
import com.example.data.local.entity.ChemicalEntity
import com.example.data.local.entity.CleaningProcedureStatusEntity
import com.example.data.local.entity.DigitalSignatureEntity
import com.example.data.local.entity.RemedialActionEntity
import com.example.data.local.entity.StaffEntity
import com.example.data.local.entity.TaskAssignmentEntity
import com.example.data.local.entity.UserEntity

@Database(
    entities = [
        AssessmentEntity::class,
        ChecklistAnswerEntity::class,
        ChemicalEntity::class,
        CleaningProcedureStatusEntity::class,
        RemedialActionEntity::class,
        DigitalSignatureEntity::class,
        UserEntity::class,
        StaffEntity::class,
        TaskAssignmentEntity::class
    ],
    version = 5,
    exportSchema = false
)
@TypeConverters(WorkforceConverters::class)
abstract class WorkforceDatabase : RoomDatabase() {

    abstract fun assessmentDao(): AssessmentDao
    abstract fun checklistAnswerDao(): ChecklistAnswerDao
    abstract fun chemicalDao(): ChemicalDao
    abstract fun remedialActionDao(): RemedialActionDao
    abstract fun digitalSignatureDao(): DigitalSignatureDao
    abstract fun userDao(): UserDao
    abstract fun staffDao(): StaffDao
    abstract fun taskAssignmentDao(): TaskAssignmentDao

    companion object {
        @Volatile
        private var INSTANCE: WorkforceDatabase? = null

        fun getDatabase(context: Context): WorkforceDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WorkforceDatabase::class.java,
                    "workforce_health_sa.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
