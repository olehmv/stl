package scala.camp.model

case class User(id: Int,
                username: String,
                address: Option[String],
                email: String)
