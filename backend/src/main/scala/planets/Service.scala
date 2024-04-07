package planets

import cats.effect.*

trait Service {
  def get(id: Int): IO[Option[Dtos.PlanetOutput]]
  def getAll(): IO[List[Dtos.PlanetOutput]]
  def create(input: Dtos.PlanetInput): IO[Dtos.PlanetOutput]
  def update(id: Int, input: Dtos.PlanetInput): IO[Dtos.PlanetOutput]
  def delete(id: Int): IO[Unit]
}

object PlanetsService extends Service {
  val repository = PlanetsRepository(Database.transactor)

  def get(id: Int): IO[Option[Dtos.PlanetOutput]] =
    repository.findById(id)
      .map(_.map(Mapper.outputFromModel(_)))

  def getByName(name: String): IO[Option[Dtos.PlanetOutput]] =
    repository.findByName(name)
      .map(_.map(Mapper.outputFromModel(_)))

  def getAll(): IO[List[Dtos.PlanetOutput]] =
    repository.findAll(QueryOptions(None, None))
      .map(_.map(Mapper.outputFromModel(_)))

  def create(input: Dtos.PlanetInput): IO[Dtos.PlanetOutput] =
    repository.create(Mapper.modelFromInput(input))
      .map(Mapper.outputFromModel(_))

  def update(id: Int, input: Dtos.PlanetInput): IO[Dtos.PlanetOutput]=
    repository.update(id, Mapper.modelFromInput(input))
      .map(Mapper.outputFromModel(_))

  def delete(id: Int): IO[Unit] = repository.delete(id)
}
