package com.example.modules

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object Stations : IntIdTable("stations") {
    val name = varchar("name", 100)
    val latitude = double("latitude")
    val longitude = double("longitude")
    val capacity = float("capacity")
    val createdAt = datetime("created_at").default(LocalDateTime.now())
}

@Serializable
data class StationDto(
    val id: Int,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val capacity: Double,
    val createdAt: String
)