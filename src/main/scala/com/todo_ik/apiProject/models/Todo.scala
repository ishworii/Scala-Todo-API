package com.todo_ik.apiProject.models

import java.time.LocalDateTime

enum TodoStatus:
  case Pending,InProgress,Completed,Cancelled


case class Todo(
               id : Option[Long],
               title : String,
               description : Option[String] = None,
               status : TodoStatus = TodoStatus.Pending,
               priority : Int = 3,
               createdAt : LocalDateTime = LocalDateTime.now(),
               updatedAt : Option[LocalDateTime] = None
               ):
    def isValid: Boolean =
        title.trim.nonEmpty &&
            title.length <= 100 &&
            priority >= 1 &&
            priority <= 5
