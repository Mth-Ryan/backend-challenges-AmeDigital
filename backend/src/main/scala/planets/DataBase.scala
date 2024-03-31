package planets

import cats.effect.*

import doobie.*
import doobie.implicits.*

import doobie.postgres.*
import doobie.postgres.implicits.*
import doobie.util.transactor.Transactor.*
import doobie.hikari.*
import doobie.util.ExecutionContexts


object Database {
  val database = "ame_challenge"
  val user     = "docker"
  val password = "docker"
  
  val transactor: Resource[IO, HikariTransactor[IO]] = for {
    ce <- ExecutionContexts.fixedThreadPool[IO](8)
    xa <- HikariTransactor.newHikariTransactor[IO](
      "org.postgresql.Driver",
      s"jdbc:postgresql:$database",
      user,
      password,
      ce
    )
  } yield xa
}
