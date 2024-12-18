package com.todo_ik.apiProject.repositories

import doobie._
import doobie.implicits._
import cats.effect.IO
import com.todo_ik.apiProject.models.{Todo,TodoStatus}
import java.time.LocalDateTime
import java.sql.Timestamp
import doobie.implicits.javasql.TimestampMeta


class TodoRepository(xa : Transactor[IO]) {
    implicit val localDateTimeMeta: Meta[LocalDateTime] =
        Meta[Timestamp].imap(_.toLocalDateTime)(Timestamp.valueOf)

    implicit val todoStatusMetadata : Meta[TodoStatus] =
        Meta[String].imap(TodoStatus.valueOf)(_.toString)

    def create(todo : Todo) : IO[Int] =
        sql"""
             INSERT INTO todos(title,description,status,priority,created_at,updated_at)
             VALUES (${todo.title},${todo.description},${todo.status},${todo.priority},${todo.createdAt},${todo.updatedAt})
           """.update.withUniqueGeneratedKeys[Int]("id").transact(xa)

    def findById(id:Long) : IO[Option[Todo]] =
        sql"""
             SELECT id,title,description,status,priority,created_at,updated_at
             FROM todos
             WHERE id = $id
           """.query[Todo].option.transact(xa)

    def update(todo:Todo) :IO[Int] =
        sql"""
             UPDATE todos
             SET title = ${todo.title},
             description=${todo.description},
             status=${todo.status},
             priority=${todo.priority},
             updated_at=${todo.updatedAt.getOrElse(LocalDateTime.now())}
             WHERE id = ${todo.id.get}
           """.update.run.transact(xa)

    def delete(id : Long) : IO[Int] =
        sql"""
             DELETE FROM todos
             where id = $id
           """.update.run.transact(xa)

    def listAll() : IO[List[Todo]] =
        sql"""
             SELECT id,title,description,status,priority,created_at,updated_at
             FROM todos
           """.query[Todo].to[List].transact(xa)
}