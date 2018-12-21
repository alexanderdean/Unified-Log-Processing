javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint") // a

lazy val root = (project in file(".")).
  settings(
    name := "aow-lambda",
    version := "0.1.0",
    scalaVersion := "2.12.7", // a
    retrieveManaged := true,
    libraryDependencies += "com.amazonaws" % "aws-lambda-java-core"       % "1.2.0",
    libraryDependencies += "com.amazonaws" % "aws-lambda-java-events"     % "2.2.4",
    libraryDependencies += "com.amazonaws" % "aws-java-sdk"               % "1.11.473" % "provided", // b
    libraryDependencies += "com.amazonaws" % "aws-java-sdk-core"          % "1.11.473" % "provided", // b
    libraryDependencies += "com.amazonaws" % "aws-java-sdk-kinesis"       % "1.11.473" % "compile", // b
    libraryDependencies += "com.fasterxml.jackson.module" % "jackson-module-scala_2.12" % "2.8.4",
    libraryDependencies += "org.json4s" %% "json4s-jackson" % "3.6.2",
    libraryDependencies += "org.json4s" %% "json4s-ext" % "3.6.2",
    libraryDependencies += "com.github.seratch" %% "awscala" % "0.8.+"
  )

mergeStrategy in assembly := { // c
    case PathList("META-INF", xs @ _*) => MergeStrategy.discard
    case x => MergeStrategy.first
}

jarName in assembly := { s"${name.value}-${version.value}" }
