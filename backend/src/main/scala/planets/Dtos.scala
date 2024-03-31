package planets

object Dtos {
  case class PlanetInput(name: String, climate: List[String], terrain: List[String])
  
  case class PlanetOutput(id: Int, name: String, climate: List[String], terrain: List[String])
}
