package planets

object Mapper {
  def outputFromModel(model: Models.Planet): Dtos.PlanetOutput = Dtos.PlanetOutput(
    id      = model.id,
    name    = model.name,
    climate = model.climate.split(",").map(_.trim).toList,
    terrain = model.terrain.split(",").map(_.trim).toList,
  )

  def modelFromInput(input: Dtos.PlanetInput): Models.Planet = Models.Planet(
    id      = -1,
    name    = input.name,
    climate = input.climate.map(_.trim).mkString(","),
    terrain = input.terrain.map(_.trim).mkString(","),
  )
}
