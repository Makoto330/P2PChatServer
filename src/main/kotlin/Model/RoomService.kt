package com.example.Model

import com.example.modules.RegisterRoomRequest
import com.example.modules.RoomResponse
import com.example.modules.Rooms
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory

class RoomService {
    private val logger = LoggerFactory.getLogger(RoomService::class.java)

    // 1. 部屋の一覧取得 (最新順)
    fun getAllRooms(): List<RoomResponse> {
        return transaction {
            Rooms.selectAll()
                .orderBy(Rooms.createdAt to SortOrder.DESC)
                .map { row ->
                    RoomResponse(
                        id = row[Rooms.id].value,
                        hostName = row[Rooms.hostName],
                        hostIp = row[Rooms.hostIp],
                        port = row[Rooms.port]
                    )
                }
        }
    }

    // 2. 部屋の作成・登録
    fun createRoom(request: RegisterRoomRequest, clientIp: String): RoomResponse {
        val ipToSave = request.hostIp ?: clientIp

        return transaction {
            val roomId = Rooms.insertAndGetId {
                it[hostName] = request.hostName
                it[hostIp] = ipToSave
                it[port] = request.port
            }

            logger.info("Room created! ID: ${roomId.value}, Host: ${request.hostName}, IP: $ipToSave:${request.port}")

            RoomResponse(
                id = roomId.value,
                hostName = request.hostName,
                hostIp = ipToSave,
                port = request.port
            )
        }
    }

    // 3. 部屋の削除（退室時）
    fun deleteRoom(roomId: Int): Boolean {
        return transaction {
            val deletedRows = Rooms.deleteWhere { Rooms.id eq roomId }
            deletedRows > 0
        }
    }
}