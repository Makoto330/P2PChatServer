package com.example.modules

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

// DB の rooms テーブル定義
object Rooms : IntIdTable("rooms") {
    val hostName = varchar("host_name", 100)
    val hostIp = varchar("host_ip", 45)
    val port = integer("port").default(9090)
    val createdAt = datetime("created_at").default(LocalDateTime.now())
}

// リクエスト用 DTO
@Serializable
data class RegisterRoomRequest(
    val hostName: String,
    val port: Int = 9090,
    val hostIp: String? = null // null の場合はサーバー側で送信元 IP を補正
)

// レスポンス用 DTO
@Serializable
data class RoomResponse(
    val id: Int,
    val hostName: String,
    val hostIp: String,
    val port: Int
)