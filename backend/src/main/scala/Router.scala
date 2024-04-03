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
        PlanetsService.getAll().flatMap((x: List[Dtos.PlanetOutput]) => Ok(x.asJson))
    
      case GET -> Root / "planets" / IntVar(id) => PlanetsService.get(id).flatMap {
        case Some(planet) => Ok(planet.asJson)
        case None         => NotFound(json"""{ "message": "Planet not found" }""")
      }

      case request @ POST -> Root / "planets" => for {
        req <- request.as[Dtos.PlanetInput]
        pay <- PlanetsService.create(req)
        res <- Ok(pay.asJson)
      } yield res

      case request @ PUT -> Root / "planets" / IntVar(id) => for {
        req <- request.as[Dtos.PlanetInput]
        pay <- PlanetsService.update(id, req)
        res <- Ok(pay.asJson)
      } yield res
    
      case DELETE -> Root / "planets" / IntVar(id) => PlanetsService.delete(id).flatMap { _ =>
        Ok(json"""{ "id": $id }""")
      }
    }
  }

  def app: HttpApp[IO] = {
    import cats.syntax.semigroupk.*
    (rootRoutes <+> planetsRoutes).orNotFound
  }
}
