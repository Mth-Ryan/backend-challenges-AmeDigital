package planets

import cats.effect.*
import doobie.*
import doobie.implicits.*
import doobie.util.transactor.Transactor

case class QueryOptions(page: Option[Int], pageSize: Option[Int])

trait PlanetsRepo {
  def findById(id: Int): IO[Option[Models.Planet]]
  def findByName(name: String): IO[Option[Models.Planet]]
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

    def findByName(name: String) =
      sql"SELECT id, name, climate, terrain FROM planets WHERE name = $name"
        .query[Models.Planet]

    def findAll(query: QueryOptions) = 
      sql"SELECT id, name, climate, terrain FROM planets"
        .query[Models.Planet]

    def insert(newModel: Models.Planet) =
      sql"""
        INSERT INTO planets (name, climate, terrain)
        VALUES (${newModel.name}, ${newModel.climate}, ${newModel.terrain})
      """.update

    def update(id: Int, newModel: Models.Planet) =
      sql"""
        UPDATE planets 
        SET name = ${newModel.name}, climate = ${newModel.climate}, terrain = ${newModel.terrain}
        WHERE id = $id
      """.update

    def delete(id: Int) =
      sql"DELETE FROM planets WHERE id = $id"
        .update
  }
  
  def findById(id: Int): IO[Option[Models.Planet]] = this.transactor.use { xa =>
    Queries.findById(id).option.transact(xa)
  }

  def findByName(name: String): IO[Option[Models.Planet]] = this.transactor.use { xa =>
    Queries.findByName(name).option.transact(xa)
  }

  def findAll(query: QueryOptions): IO[List[Models.Planet]] = this.transactor.use { xa =>
    Queries.findAll(query).to[List].transact(xa)
  }

  def create(newModel: Models.Planet): IO[Models.Planet] = {
    val query = for {
      id     <- Queries.insert(newModel).withUniqueGeneratedKeys[Int]("id")
      planet <- Queries.findById(id).unique
    } yield planet

    this.transactor.use { xa =>
      query.transact(xa)
    }
  }
    
  def update(id: Int, newModel: Models.Planet): IO[Models.Planet] = {
    val query = for {
      _      <- Queries.update(id, newModel).run
      planet <- Queries.findById(id).unique
    } yield planet

    this.transactor.use { xa =>
      query.transact(xa)
    }
  }
  
  def delete(id: Int): IO[Unit] = this.transactor.use { xa =>
    Queries.delete(id).run.transact(xa).map(_ => ())
  }
}
