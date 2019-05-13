package scala.camp.config

import com.typesafe.config.ConfigFactory

trait AppConfig {
  private lazy val config = ConfigFactory.load()
  private lazy val httpConfig = config.getConfig("http")
  private lazy val slickConfig = config.getConfig("database")

  lazy val applicationName: String = config.getString("application.name")
  lazy val httpHost: String = httpConfig.getString("interface")
  lazy val httpPort: Int = httpConfig.getInt("port")

  lazy val jdbcUrl: String = slickConfig.getString("url")
  lazy val dbDriver: String = slickConfig.getString("driver")
}
