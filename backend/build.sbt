val scala3Version  = "3.4.0"
val circeVersion   = "0.14.6"
val http4sVersion  = "0.23.26"
val logbackVersion = "1.5.3"

lazy val root = project
  .in(file("."))
  .settings(
    name := "backend",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    libraryDependencies ++= Seq(
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-literal" % circeVersion,
      
      "org.http4s" %% "http4s-ember-client" % http4sVersion,
      "org.http4s" %% "http4s-ember-server" % http4sVersion,
      "org.http4s" %% "http4s-dsl"          % http4sVersion,
      "org.http4s" %% "http4s-circe"        % http4sVersion,

      "ch.qos.logback"  %  "logback-classic" % logbackVersion % Runtime,
      
      "org.scalameta" %% "munit" % "0.7.29" % Test,
    ),
    
    assembly / assemblyMergeStrategy := {
      case "module-info.class" => MergeStrategy.discard
      case x => (assembly / assemblyMergeStrategy).value.apply(x)
    },
  )
