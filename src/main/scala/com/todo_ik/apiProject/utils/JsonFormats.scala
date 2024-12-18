package com.todo_ik.apiProject.utils

import spray.json.{DefaultJsonProtocol, DeserializationException, JsString, JsValue, RootJsonFormat}
import com.todo_ik.apiProject.models.{Todo, TodoStatus}
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object JsonFormats extends DefaultJsonProtocol {

    // Define the formatter for LocalDateTime
    private val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    // Custom JsonFormat for LocalDateTime
    implicit val localDateTimeFormat: RootJsonFormat[LocalDateTime] = new RootJsonFormat[LocalDateTime] {

        /** Serialize LocalDateTime to JSON */
        override def write(dateTime: LocalDateTime): JsValue =
            JsString(dateTime.format(dateTimeFormatter))

        /** Deserialize JSON to LocalDateTime */
        override def read(json: JsValue): LocalDateTime = json match {
            case JsString(value: String) =>
                try {
                    LocalDateTime.parse(value, dateTimeFormatter)
                } catch {
                    case _: Exception =>
                        throw new DeserializationException(s"Invalid LocalDateTime format: $value")
                }
            case _ => throw new DeserializationException("LocalDateTime should be a string")
        }
    }

    // Custom JsonFormat for TodoStatus
    implicit val todoStatusFormat: RootJsonFormat[TodoStatus] = new RootJsonFormat[TodoStatus] {

        /** Serialize TodoStatus to JSON */
        override def write(status: TodoStatus): JsValue =
            JsString(status.toString)

        /** Deserialize JSON to TodoStatus */
        override def read(json: JsValue): TodoStatus = json match {
            case JsString(value: String) =>
                TodoStatus.values.find(_.toString.equalsIgnoreCase(value))
                    .getOrElse(throw new DeserializationException(s"Invalid TodoStatus: $value"))
            case _ => throw new DeserializationException("TodoStatus should be a string")
        }
    }

    // JsonFormat for Todo case class
    implicit val todoFormat: RootJsonFormat[Todo] = jsonFormat7(com.todo_ik.apiProject.models.Todo.apply)

    implicit val todoListFormat: RootJsonFormat[List[Todo]] = listFormat(todoFormat)
}
