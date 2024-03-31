import cats.*
import cats.effect.*
import com.comcast.ip4s.*
import io.circe.*
import io.circe.syntax.*
import io.circe.literal.*
import io.circe.generic.auto.*
import org.http4s.*
import org.http4s.dsl.*
import org.http4s.circe.*
import org.http4s.implicits.*
import org.http4s.ember.server.EmberServerBuilder

object App extends IOApp {
  case class Planet(id: Int, name: String, climate: String, terrain: String)
  
  private def planetsRoutes[F[_]: Monad]: HttpRoutes[F] = {
    val dsl = Http4sDsl[F]
    import dsl._

    HttpRoutes.of[F] {
      case GET -> Root => 
        Ok(json"""{"hello": "world!"}""")

      case GET -> Root / "planets" / IntVar(id) =>
        Ok(Planet(id, "Tatooine", "arid", "desert").asJson)
    }
  }

  def app[F[_]: Monad]: HttpApp[F] = planetsRoutes.orNotFound

  override def run(args: List[String]): IO[ExitCode] = {
    EmberServerBuilder.default[IO]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"9000")
      .withHttpApp(app)
      .build
      .useForever
  }
}
