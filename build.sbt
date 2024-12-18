ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.4"

// Ensure Scala 3 compatibility
ThisBuild / scalacOptions ++= Seq(
    "-deprecation",
    "-feature",
    "-language:implicitConversions",
    "-unchecked"
)

lazy val root = (project in file("."))
    .settings(
        name := "Scala_Todo_API",

        // Apache Pekko and Pekko HTTP dependencies
        libraryDependencies ++= {
            val pekkoVersion = "1.0.3"
            val pekkoHttpVersion = "1.0.1"
            Seq(
                "org.apache.pekko" %% "pekko-actor-typed" % pekkoVersion,
                "org.apache.pekko" %% "pekko-stream" % pekkoVersion,
                "org.apache.pekko" %% "pekko-http" % pekkoHttpVersion,
                "org.apache.pekko" %% "pekko-http-spray-json" % pekkoHttpVersion // Spray JSON support
            )
        },

        // Logging dependencies
        libraryDependencies ++= Seq(
            "ch.qos.logback" % "logback-classic" % "1.5.6",
            "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5"
        ),

        // Database-related dependencies
        libraryDependencies ++= Seq(
            "org.tpolecat" %% "doobie-core"      % "1.0.0-RC5",
            "org.tpolecat" %% "doobie-hikari"    % "1.0.0-RC5",
            "org.tpolecat" %% "doobie-postgres"  % "1.0.0-RC5",
            "org.tpolecat" %% "doobie-scalatest" % "1.0.0-RC5" % Test
        ),

        // Testing dependencies
        libraryDependencies ++= Seq(
            "org.scalatest" %% "scalatest" % "3.2.18" % Test,
            "org.apache.pekko" %% "pekko-http-testkit" % "1.0.0" % Test
        ),

        // Additional utility dependencies
        libraryDependencies += "com.typesafe" % "config" % "1.4.3",

        // Disable parallel execution in tests
        Test / parallelExecution := false
    )
