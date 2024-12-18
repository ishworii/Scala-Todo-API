package com.todo_ik.apiProject.db

import com.typesafe.config.ConfigFactory
import doobie.hikari.HikariTransactor
import cats.effect.{IO,Resource}
import doobie.util.ExecutionContexts

object DatabaseConfig{
    private val config = ConfigFactory.load()
    private  val dbConfig = config.getConfig("db")

    private val dbUrl : String = dbConfig.getString("url")
    private val dbUser : String = dbConfig.getString("user")
    private val dbPassword : String = dbConfig.getString("password")
    private val dbDriver : String = dbConfig.getString("driver")
    private val poolSize : Int = dbConfig.getInt("poolSize")

    def transactor : Resource[IO,HikariTransactor[IO]] = {
        for{
            ce <- ExecutionContexts.fixedThreadPool[IO](poolSize)
            xa <- HikariTransactor.newHikariTransactor[IO](
                dbDriver,
                dbUrl,
                dbUser,
                dbPassword,
                ce
            )
        } yield xa
    }
}