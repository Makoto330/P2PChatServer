package com.example.modules

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object Users: IntIdTable("users") {
    val email = varchar("email", 255).uniqueIndex()
    val passwordHash = varchar("passwordHash", 255)
    val name = varchar("name", 100)
    val createdAt = datetime("created_at").default(LocalDateTime.now())
}

@Serializable
data class AuthRequest(
    val email: String,
    val password: String,
    val name: String = ""
)
@Serializable
data class UserResponse(
    val id: Int,
    val email: String,
    val name: String,
)
@Serializable
data class  LoginResponse(
    val message: String,
    val user: UserResponse
)