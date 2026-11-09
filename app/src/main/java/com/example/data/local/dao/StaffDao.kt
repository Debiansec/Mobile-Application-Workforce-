package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.StaffEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StaffDao {

    @Query("SELECT * FROM staff_profiles ORDER BY fullName ASC")
    fun getAllStaff(): Flow<List<StaffEntity>>

    @Query("SELECT * FROM staff_profiles WHERE isActive = 1 ORDER BY fullName ASC")
    fun getActiveStaff(): Flow<List<StaffEntity>>

    @Query("SELECT * FROM staff_profiles WHERE id = :id LIMIT 1")
    suspend fun getStaffById(id: String): StaffEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStaff(staff: StaffEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllStaff(staffList: List<StaffEntity>)

    @Update
    suspend fun updateStaff(staff: StaffEntity)

    @Query("UPDATE staff_profiles SET isActive = 0, updatedAt = :updatedAt, syncStatus = 'PENDING_SYNC' WHERE id = :id")
    suspend fun deactivateStaff(id: String, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE staff_profiles SET isActive = 1, updatedAt = :updatedAt, syncStatus = 'PENDING_SYNC' WHERE id = :id")
    suspend fun activateStaff(id: String, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE staff_profiles SET availabilityStatus = :availability, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateAvailability(id: String, availability: String, updatedAt: Long = System.currentTimeMillis())

    @Query("DELETE FROM staff_profiles WHERE id = :id")
    suspend fun deleteStaff(id: String)

    @Query("SELECT COUNT(*) FROM staff_profiles")
    suspend fun countStaff(): Int
}
