package planets

import cats.effect.*

trait Service {
  def get(id: Int): IO[Option[Dtos.PlanetWithFilmsOutput]]
  def getAll(): IO[List[Dtos.PlanetOutput]]
  def create(input: Dtos.PlanetInput): IO[Dtos.PlanetWithFilmsOutput]
  def update(id: Int, input: Dtos.PlanetInput): IO[Dtos.PlanetWithFilmsOutput]
  def delete(id: Int): IO[Unit]
}

object PlanetsService extends Service {
  val repository = PlanetsRepository(Database.transactor)

  def get(id: Int): IO[Option[Dtos.PlanetWithFilmsOutput]] = for {
    model <- repository.findById(id)
    films <- SWAPIService.getPlanetFilms(id).orElse(IO.pure(List[Dtos.FilmOutput]()))
  } yield model.map(Mapper.outputFromModel(_).withFilms(films))

  def getByName(name: String): IO[Option[Dtos.PlanetWithFilmsOutput]] = for {
    model <- repository.findByName(name)
    id    <- IO { model.map(_.id).getOrElse(0) }
    films <- SWAPIService.getPlanetFilms(id).orElse(IO.pure(List[Dtos.FilmOutput]()))
  } yield model.map(Mapper.outputFromModel(_).withFilms(films))

  def getAll(): IO[List[Dtos.PlanetOutput]] =
    repository.findAll(QueryOptions(None, None))
      .map(_.map(Mapper.outputFromModel(_)))

  def create(input: Dtos.PlanetInput): IO[Dtos.PlanetWithFilmsOutput] = for {
    model <- repository.create(Mapper.modelFromInput(input))
    films <- SWAPIService.getPlanetFilms(model.id).orElse(IO.pure(List[Dtos.FilmOutput]()))
  } yield Mapper.outputFromModel(model).withFilms(films)

  def update(id: Int, input: Dtos.PlanetInput): IO[Dtos.PlanetWithFilmsOutput]= for {
    model <- repository.update(id, Mapper.modelFromInput(input))
    films <- SWAPIService.getPlanetFilms(model.id).orElse(IO.pure(List[Dtos.FilmOutput]()))
  } yield Mapper.outputFromModel(model).withFilms(films)
  
  def delete(id: Int): IO[Unit] = repository.delete(id)
}
