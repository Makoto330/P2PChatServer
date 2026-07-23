package com.example

import com.example.Model.RoomService
import com.example.Model.UserService
import com.example.modules.AuthRequest
import com.example.modules.LoginResponse
import com.example.modules.RegisterRoomRequest
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.plugins.origin
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing

fun Application.configureUserRouting() {
    val userService = UserService()
    val roomService = RoomService() // 👈 RoomService をインスタンス化

    routing {
        // --- 認証機能 ---
        post("/register") {
            val request = call.receive<AuthRequest>()
            val user = userService.register(request)

            if (user != null) {
                call.respond(HttpStatusCode.Created, user)
            } else {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "このメールアドレスは既に登録されています"))
            }
        }

        post("/login") {
            val request = call.receive<AuthRequest>()
            val user = userService.login(request)

            if (user != null) {
                call.respond(HttpStatusCode.OK, LoginResponse(message = "ログイン成功", user = user))
            } else {
                call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "メールアドレスまたはパスワードが正しくありません"))
            }
        }

        // --- 🚲 P2P シグナリング機能 ---

        // 部屋一覧取得
        get("/rooms") {
            val result = roomService.getAllRooms()
            call.respond(HttpStatusCode.OK, result)
        }

        // 部屋作成
        post("/rooms") {
            val request = call.receive<RegisterRoomRequest>()
            val clientIp = call.request.origin.remoteHost
            val newRoom = roomService.createRoom(request, clientIp)

            call.respond(HttpStatusCode.Created, newRoom)
        }

        // 部屋削除（退室時用）
        delete("/rooms/{id}") {
            val roomId = call.parameters["id"]?.toIntOrNull()
            if (roomId == null) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "無効な部屋IDです"))
                return@delete
            }

            val success = roomService.deleteRoom(roomId)
            if (success) {
                call.respond(HttpStatusCode.OK, mapOf("message" to "部屋を削除しました"))
            } else {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to "該当する部屋が見つかりません"))
            }
        }
    }
}