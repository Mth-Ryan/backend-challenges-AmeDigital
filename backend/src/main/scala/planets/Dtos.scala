package planets

object Dtos {
  case class FilmOutput(episode: Int, title: String, realeaseDate: String)
  
  case class PlanetInput(name: String, climate: List[String], terrain: List[String])
  
  case class PlanetOutput(id: Int, name: String, climate: List[String], terrain: List[String]) {
    def withFilms(films: List[FilmOutput]): PlanetWithFilmsOutput =
      PlanetWithFilmsOutput(id, name, climate, terrain, films)
  }

  case class PlanetWithFilmsOutput(
    id: Int,
    name: String,
    climate: List[String],
    terrain: List[String],
    films: List[FilmOutput]
  )
}
