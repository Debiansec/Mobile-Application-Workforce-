package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.User
import com.example.data.model.UserRole

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val role: UserRole,
    val registrationNumber: String = "",
    val signatureData: String? = null,
    val password: String = "Password123!",
    val phone: String = "+27 11 892 4000",
    val department: String = "Occupational Hygiene & Safety",
    val isActive: Boolean = true
) {
    fun toDomain(): User = User(
        id = id,
        name = name,
        email = email,
        role = role,
        registrationNumber = registrationNumber,
        signatureData = signatureData,
        password = password,
        phone = phone,
        department = department,
        isActive = isActive
    )

    companion object {
        fun fromDomain(user: User): UserEntity = UserEntity(
            id = user.id,
            name = user.name,
            email = user.email.trim().lowercase(),
            role = user.role,
            registrationNumber = user.registrationNumber.trim(),
            signatureData = user.signatureData,
            password = user.password,
            phone = user.phone.trim(),
            department = user.department.trim(),
            isActive = user.isActive
        )
    }
}
