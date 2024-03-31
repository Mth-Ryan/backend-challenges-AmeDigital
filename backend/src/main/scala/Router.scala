import cats.*
import cats.implicits.*
import cats.effect.*
import io.circe.*
import io.circe.syntax.*
import io.circe.literal.*
import io.circe.generic.auto.*
import org.http4s.*
import org.http4s.dsl.*
import org.http4s.circe.*
import org.http4s.implicits.*

object Router {
  case class Planet(id: Int, name: String, climate: String, terrain: String)
  
  private def rootRoutes[F[_]: Monad]: HttpRoutes[F] = {
    val dsl = Http4sDsl[F]
    import dsl._
  
    HttpRoutes.of[F] {
      case GET -> Root => 
        Ok(json"""{"hello": "world!"}""")
    }
  }
  
  private def planetsRoutes[F[_]: Monad]: HttpRoutes[F] = {
    val dsl = Http4sDsl[F]
    import dsl._
  
    HttpRoutes.of[F] {
      case GET -> Root => 
        Ok(json"""{"hello": "world!"}""")

      case GET -> Root / "planets" =>
        Ok(List(Planet(1, "Tatooine", "arid", "desert")).asJson)
    
      case GET -> Root / "planets" / IntVar(id) =>
        Ok(Planet(id, "Tatooine", "arid", "desert").asJson)

      case POST -> Root / "planets" =>
        Ok(Planet(1, "Tatooine", "arid", "desert").asJson)

      case PUT -> Root / "planets" / IntVar(id) =>
        Ok(Planet(1, "Tatooine", "arid", "desert").asJson)
    
      case DELETE -> Root / "planets" / IntVar(id) =>
        Ok()
    }
  }

  def app[F[_]: Monad]: HttpApp[F] = {
    import cats.syntax.semigroupk.*
    (rootRoutes <+> planetsRoutes).orNotFound
  }
}
