import cats.*
import cats.effect.*

import io.circe.*
import io.circe.syntax.*
import io.circe.literal.*
import io.circe.generic.auto.*

import org.http4s.*
import org.http4s.dsl.io.*
import org.http4s.circe.*
import org.http4s.implicits.*

import planets.*

object Router {
  private def rootRoutes: HttpRoutes[IO] = {
    HttpRoutes.of[IO] {
      case GET -> Root => 
        Ok(json"""{"hello": "world!"}""")
    }
  }
  
  private def planetsRoutes: HttpRoutes[IO] = {
    given EntityDecoder[IO, Dtos.PlanetInput] = jsonOf[IO, Dtos.PlanetInput]
  
    HttpRoutes.of[IO] {
      case GET -> Root / "planets" =>
        Ok(List(Dtos.PlanetOutput(1, "Tatooine", List("arid"), List("desert"))).asJson)
    
      case GET -> Root / "planets" / IntVar(id) =>
        Ok(Dtos.PlanetOutput(1, "Tatooine", List("arid"), List("desert")).asJson)

      case request @ POST -> Root / "planets" => for {
        req <- request.as[Dtos.PlanetInput]
        res <- Ok(Dtos.PlanetOutput(1, req.name, req.climate, req.terrain).asJson)
      } yield res

      case request @ PUT -> Root / "planets" / IntVar(id) => for {
        req <- request.as[Dtos.PlanetInput]
        res <- Ok(Dtos.PlanetOutput(id, req.name, req.climate, req.terrain).asJson)
      } yield res
    
      case DELETE -> Root / "planets" / IntVar(id) =>
        Ok()
    }
  }

  def app: HttpApp[IO] = {
    import cats.syntax.semigroupk.*
    (rootRoutes <+> planetsRoutes).orNotFound
  }
}
