package planets

import cats.effect.*
import doobie.*
import doobie.implicits.*
import doobie.util.transactor.Transactor

case class QueryOptions(page: Option[Int], pageSize: Option[Int])

trait PlanetsRepo {
  def findById(id: Int): IO[Models.Planet]
  def findByName(name: String): IO[Models.Planet]
  def findAll(query: QueryOptions): IO[List[Models.Planet]]
  def create(newModel: Models.Planet): IO[Models.Planet]
  def update(id: Int, newModel: Models.Planet): IO[Models.Planet]
  def delete(id: Int): IO[Unit]
}

class PlanetsRepository(transactor: Resource[IO, Transactor[IO]]) extends PlanetsRepo {
  object Queries {
    def findById(id: Int) = 
      sql"SELECT id, name, climate, terrain FROM planets WHERE id = $id"
        .query[Models.Planet]
        .unique

    def findByName(name: String) =
      sql"SELECT id, name, climate, terrain FROM planets WHERE name = $name"
        .query[Models.Planet]
        .unique

    def findAll(query: QueryOptions) = 
      sql"SELECT id, name, climate, terrain SELECT planets"
        .query[Models.Planet]
        .to[List]

    def insert(newModel: Models.Planet) =
      sql"""
        INSERT INTO planets (name, climate, terrain)
        VALUES ($newModel.name, $newModel.climate, $newModel.terrain)
      """.update.withUniqueGeneratedKeys[Int]("id")

    def update(id: Int, newModel: Models.Planet) =
      sql"""
        UPDATE planets 
        SET name = $newModel.name, climate = $newModel.climate, terrain = $newModel.terrain
        WHERE id = $id
      """.update.run

    def delete(id: Int) =
      sql"DELETE FROM planets WHERE id = $id"
        .update
        .run
  }
  
  def findById(id: Int): IO[Models.Planet] = this.transactor.use { xa =>
    Queries.findById(id).transact(xa)
  }

  def findByName(name: String): IO[Models.Planet] = this.transactor.use { xa =>
    Queries.findByName(name).transact(xa)
  }

  def findAll(query: QueryOptions): IO[List[Models.Planet]] = this.transactor.use { xa =>
    Queries.findAll(query).transact(xa)
  }

  def create(newModel: Models.Planet): IO[Models.Planet] = {
    val query = for {
      id     <- Queries.insert(newModel)
      planet <- Queries.findById(id)
    } yield planet

    this.transactor.use { xa =>
      query.transact(xa)
    }
  }
    
  def update(id: Int, newModel: Models.Planet): IO[Models.Planet] = {
    val query = for {
      _      <- Queries.update(id, newModel)
      planet <- Queries.findById(id)
    } yield planet

    this.transactor.use { xa =>
      query.transact(xa)
    }
  }
  
  def delete(id: Int): IO[Unit] = this.transactor.use { xa =>
    Queries.delete(id).transact(xa).map(_ => ())
  }
}
