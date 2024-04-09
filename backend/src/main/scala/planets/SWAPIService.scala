package planets

import scala.collection.mutable.HashMap

import cats.effect.*
import cats.syntax.all.*

import org.http4s.*
import org.http4s.implicits.*
import org.http4s.ember.client.*
import org.http4s.client.Client

import io.circe.generic.auto.*
import org.http4s.circe.*

import com.comcast.ip4s.*

object SWAPIService {
  private val client = EmberClientBuilder
    .default[IO]
    .build

  private val baseUri = uri"https://swapi.dev"

  case class PlanetResponse(films: List[String])
  case class FilmResponse(title: String, episode_id: Int, release_date: String)

  // Caching API results is one of the possible solutions
  // to SWAPI's inevitable n+1 problem. An alternative solution
  // to the films cache problem would be to load everything into memory,
  // since there are few entries.
  // These hashmaps could easily be exchanged for a more robust caching service
  // with a functional IO based interface. But let's keep it simple.
  private var planetsCache = HashMap[Int, PlanetResponse]()
  private var filmsCache = HashMap[String, FilmResponse]()
  
  private def getPlanetMetadata(planetId: Int): IO[PlanetResponse] = if planetsCache.contains(planetId)
    then IO.pure(planetsCache(planetId))
    else for {
      req    <- IO.pure(Request[IO](Method.GET, baseUri.withPath(s"/api/planets/$planetId/")))
      planet <- client.use(_.expect(req)(jsonOf[IO, PlanetResponse]))
      _      <- IO { planetsCache += (planetId -> planet) }
    } yield planet

  private def getFilmMetadata(strUri: String): IO[FilmResponse] = if filmsCache.contains(strUri) 
    then IO { filmsCache(strUri) }
    else for {
      uri  <- Uri.fromString(strUri).fold(pf => IO.raiseError(new Exception(pf.sanitized)), IO.pure)
      req  <- IO.pure(Request[IO](Method.GET, uri))
      film <- client.use(_.expect(req)(jsonOf[IO, FilmResponse]))
      _    <- IO { filmsCache += (strUri -> film) }
    } yield film
  
  def getPlanetFilms(planetId: Int): IO[List[FilmResponse]] = for {
    planet <- getPlanetMetadata(planetId)
    films  <- planet.films.parTraverse(getFilmMetadata(_))
  } yield films
}
