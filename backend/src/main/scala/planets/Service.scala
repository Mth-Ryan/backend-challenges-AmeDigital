package planets

import cats.effect.*

trait Service {
  def get(id: Int): IO[Option[Dtos.PlanetOutput]]
  def getAll(): IO[List[Dtos.PlanetOutput]]
}

object PlanetsService extends Service {
  val repository = PlanetsRepository(Database.transactor)

  def get(id: Int): IO[Option[Dtos.PlanetOutput]] =
    repository.findById(id)
      .map(_.map(Mapper.outputFromModel(_)))

  def getAll(): IO[List[Dtos.PlanetOutput]] =
    repository.findAll(QueryOptions(None, None))
      .map(_.map(Mapper.outputFromModel(_)))
}
