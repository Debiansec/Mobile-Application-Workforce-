package com.example.data.repository

import com.example.data.local.WorkforceDatabase
import com.example.data.local.entity.UserEntity
import com.example.data.model.User
import com.example.data.model.UserRole
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserRepository(private val db: WorkforceDatabase) {
    private val dao = db.userDao()

    fun getAllUsers(): Flow<List<User>> {
        return dao.getAllUsers().map { list -> list.map { it.toDomain() } }
    }

    suspend fun getUserById(id: String): User? {
        return dao.getUserById(id)?.toDomain()
    }

    suspend fun getUserByEmail(email: String): User? {
        return dao.getUserByEmail(email.trim().lowercase())?.toDomain()
    }

    suspend fun authenticate(email: String, password: String): User? {
        val cleanEmail = email.trim().lowercase()
        val user = dao.getUserByEmail(cleanEmail)?.toDomain() ?: return null
        return if (user.password == password.trim() && user.isActive) {
            user
        } else {
            null
        }
    }

    suspend fun saveUser(user: User) {
        dao.insertOrUpdate(UserEntity.fromDomain(user))
    }

    suspend fun deleteUser(userId: String) {
        dao.deleteById(userId)
    }

    suspend fun seedInitialUsersIfEmpty(defaultUsers: List<User>) {
        if (dao.getUserCount() == 0) {
            dao.insertAll(defaultUsers.map { UserEntity.fromDomain(it) })
        }
    }
}
