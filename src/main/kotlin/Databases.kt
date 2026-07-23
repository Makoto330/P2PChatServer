package com.example

import com.example.modules.Rooms
import com.example.modules.Stations
import com.example.modules.Users
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.Application
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureDatabases() {
    var centosIp = "192.168.150.28"

    val config = HikariConfig().apply{
        driverClassName = "org.postgresql.Driver"
        jdbcUrl = "jdbc:postgresql://$centosIp:5432/bikedb"
        username = "postgres"
        password = "password"
        maximumPoolSize = 3
        isAutoCommit = false
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        validate()
    }
    val dataSource = HikariDataSource(config)
    Database.connect(dataSource)

    transaction {
        addLogger(StdOutSqlLogger)
        SchemaUtils.create(Stations, Users, Rooms)
    }
    println("データベースへの接続に成功しました")
}