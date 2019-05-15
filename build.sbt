lazy val akkaHttpVersion = "10.0.9"
lazy val akkaVersion    = "2.4.19"

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization    := "scala.camp",
      scalaVersion    := "2.11.8"
    )),
    name := "app",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-stream" % akkaVersion,
      "com.typesafe.akka" %% "akka-http-core"       % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http"            % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
      "com.typesafe.slick" %% "slick"           % "3.3.0",
      "com.typesafe.slick" %% "slick-hikaricp" % "3.1.0",
      "com.h2database"      % "h2"              % "1.4.197",
      "ch.qos.logback"      % "logback-classic" % "1.2.3",
      
      "com.typesafe.akka" %% "akka-http-testkit"    % akkaHttpVersion % Test,
      "org.scalatest"     %% "scalatest"            % "3.0.5"         % Test
    )

  )
