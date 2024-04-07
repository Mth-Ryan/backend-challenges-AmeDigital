package planets

import cats.effect.*

import doobie.*
import doobie.implicits.*
import doobie.postgres.*
import doobie.postgres.implicits.*
import doobie.util.transactor.Transactor.*
import doobie.hikari.*
import doobie.util.ExecutionContexts
import doobie.util.log.*

import org.slf4j.LoggerFactory

object Database {
  val database = "ame_challenge"
  val user     = "docker"
  val password = "docker"

  val printSqlLogHandler: LogHandler[IO] = new LogHandler[IO] {
    val logger = LoggerFactory.getLogger(getClass)
    
    def run(logEvent: LogEvent): IO[Unit] = IO {
      logEvent match {
        case Success(sql, args, _, exec, _) =>
          logger.info(s"Successfull Database query: $sql, args: $args, exec: $exec")
        case ExecFailure(sql, args, _, exec, err) =>
          logger.info(s"Failed to execute Database query: $sql, args: $args, exec: $exec, due: $err")
        case ProcessingFailure(sql, args, _, exec, _, err) =>
          logger.info(s"Failed to process Database query: $sql, args: $args, exec: $exec, due: $err")
      }
    }
  }
  
  val transactor: Resource[IO, HikariTransactor[IO]] = for {
    ce <- ExecutionContexts.fixedThreadPool[IO](8)
    xa <- HikariTransactor.newHikariTransactor[IO](
      "org.postgresql.Driver",
      s"jdbc:postgresql:$database",
      user,
      password,
      ce,
      logHandler = Some(printSqlLogHandler)
    )
  } yield xa
}
