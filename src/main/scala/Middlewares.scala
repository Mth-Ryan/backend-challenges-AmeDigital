import cats.*
import cats.data.OptionT
import cats.effect.*

import org.http4s.*
import org.http4s.server.middleware.{ErrorAction, ErrorHandling}

import org.slf4j.LoggerFactory

object Middlewares {
  extension (routes: HttpRoutes[IO]) {
    def withErrorLoggingMiddleware: HttpRoutes[IO] = {
      val logger = LoggerFactory.getLogger(getClass)
    
      def errorHandler(t: Throwable, msg: => String): OptionT[IO, Unit] = OptionT.liftF(
        IO { logger.error(msg, t) }
      )

      ErrorHandling.Recover.total(
        ErrorAction.log(
          routes,
          messageFailureLogAction = errorHandler,
          serviceErrorLogAction = errorHandler,
        )
      )
    }

  }
}
