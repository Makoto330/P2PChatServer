package com.example.Model

import at.favre.lib.crypto.bcrypt.BCrypt
import com.example.modules.AuthRequest
import com.example.modules.UserResponse
import com.example.modules.Users
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory

class UserService {
    private val logger = LoggerFactory.getLogger(UserService::class.java)

    fun register(request: AuthRequest): UserResponse? {
        val hashedPassword = BCrypt.withDefaults().hashToString(12, request.password.toCharArray())

        return transaction {
            val existingUser = Users.selectAll().where{ Users.email eq request.email }.singleOrNull()
            if (existingUser != null) return@transaction null

            val userId = Users.insertAndGetId {
                it[email] = request.email
                it[passwordHash] = hashedPassword
                it[name] = request.name
            }

            UserResponse(id = userId.value, email = request.email, name = request.name)
        }
    }

    fun login(request: AuthRequest): UserResponse? {
        // 💡 2. 受信したリクエストの中身をログ出力
        println("================ [LOGIN ATTEMPT] ================")
        println("Received email: '${request.email}'")
        println("Received password length: ${request.password.length}")

        return transaction {
            // DB検索
            val userRow = Users.select { Users.email eq request.email }.singleOrNull()

            if (userRow == null) {
                // 💡 ユーザーが見つからなかった場合のログ
                println("User NOT found in database for email: ${request.email}")
                return@transaction null
            }

            // DBから取得したハッシュ値
            val storedHash = userRow[Users.passwordHash]
            println("User found! ID: ${userRow[Users.id].value}")
            println("Stored Hash: $storedHash")

            // パスワード照合
            val isVerified = try {
                // verifyerを使う場合
                BCrypt.verifyer().verify(request.password.toCharArray(), storedHash).verified
            } catch (e: Exception) {
                // 💡 パスワード検証自体で例外が出た場合、詳細を出す
                logger.error("BCrypt verification failed with exception!", e)
                false
            }

            logger.info("Password match result: $isVerified")

            if (isVerified) {
                UserResponse(
                    id = userRow[Users.id].value,
                    email = userRow[Users.email],
                    name = userRow[Users.name]
                )
            } else {
                logger.warn("Password mismatch for email: ${request.email}")
                null
            }
        }
    }

}