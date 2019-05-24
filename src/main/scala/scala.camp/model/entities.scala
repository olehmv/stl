package scala.camp.model

case class User(id: Int, username: String, password: String, email: String)

case class UserAuth(accessToken: String,
                    tokenType: String,
                    expiresIn: Int,
                    loggedInAt: String)
