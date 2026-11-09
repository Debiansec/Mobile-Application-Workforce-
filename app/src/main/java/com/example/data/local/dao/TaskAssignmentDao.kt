package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.TaskAssignmentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskAssignmentDao {

    @Query("SELECT * FROM task_assignments ORDER BY scheduledDate DESC, priority DESC")
    fun getAllTasks(): Flow<List<TaskAssignmentEntity>>

    @Query("SELECT * FROM task_assignments WHERE assignedStaffId = :staffId ORDER BY scheduledDate DESC")
    fun getTasksForStaff(staffId: String): Flow<List<TaskAssignmentEntity>>

    @Query("SELECT * FROM task_assignments WHERE status != 'COMPLETED' AND status != 'CANCELLED' ORDER BY scheduledDate ASC")
    fun getActiveTasks(): Flow<List<TaskAssignmentEntity>>

    @Query("SELECT * FROM task_assignments WHERE id = :id LIMIT 1")
    suspend fun getTaskById(id: String): TaskAssignmentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskAssignmentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTasks(tasks: List<TaskAssignmentEntity>)

    @Update
    suspend fun updateTask(task: TaskAssignmentEntity)

    @Query("UPDATE task_assignments SET status = :status, completionNotes = :notes, updatedAt = :updatedAt, syncStatus = 'PENDING_SYNC' WHERE id = :id")
    suspend fun updateTaskStatus(id: String, status: String, notes: String, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE task_assignments SET assignedStaffId = :newStaffId, assignedStaffName = :newStaffName, instructions = :instructions, status = 'ASSIGNED', updatedAt = :updatedAt, syncStatus = 'PENDING_SYNC' WHERE id = :id")
    suspend fun reassignTask(id: String, newStaffId: String, newStaffName: String, instructions: String, updatedAt: Long = System.currentTimeMillis())

    @Query("DELETE FROM task_assignments WHERE id = :id")
    suspend fun deleteTask(id: String)

    @Query("SELECT COUNT(*) FROM task_assignments")
    suspend fun countTasks(): Int
}
