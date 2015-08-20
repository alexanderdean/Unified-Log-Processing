javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint") // a

lazy val root = (project in file(".")).
  settings(
    name := "aow-lambda",
    version := "0.1.0",
    scalaVersion := "2.11.6", // a
    retrieveManaged := true,
    libraryDependencies += "com.amazonaws" % "aws-lambda-java-core"       % "1.0.0",
    libraryDependencies += "com.amazonaws" % "aws-lambda-java-events"     % "1.0.0",
    libraryDependencies += "com.amazonaws" % "aws-java-sdk"               % "1.9.34" % "provided", // b
    libraryDependencies += "com.amazonaws" % "aws-java-sdk-core"          % "1.9.34" % "provided", // b
    libraryDependencies += "com.fasterxml.jackson.module" % "jackson-module-scala_2.11" % "2.5.2",
    libraryDependencies += "org.json4s" %% "json4s-jackson" % "3.2.11",
    libraryDependencies += "org.json4s" %% "json4s-ext" % "3.2.11",
    libraryDependencies += "com.github.seratch" %% "awscala" % "0.5.+"
  )

mergeStrategy in assembly := { // c
    case PathList("META-INF", xs @ _*) => MergeStrategy.discard
    case x => MergeStrategy.first
}

jarName in assembly := { s"${name.value}-${version.value}" }
