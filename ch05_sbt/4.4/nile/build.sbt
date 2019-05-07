lazy val root = (project in file(".")).
  settings(
    name          := "abandoned-cart-detector",
    organization  := "nile",
    version       := "0.1.0",
    scalaVersion  := "2.12.7",
    javacOptions  := Seq("-source", "1.8", "-target", "1.8", "-Xlint"),
    scalacOptions := Seq("-deprecation", "-encoding", "utf8"),
    libraryDependencies ++= Seq(
      "org.apache.samza"  %  "samza-shell"      % "0.14.0",
      "org.apache.samza"  %  "samza-api"        % "0.14.0",
      "org.apache.samza"  %% "samza-core"       % "0.14.0",
      "org.apache.samza"  %% "samza-yarn"       % "0.14.0",
      "org.apache.samza"  %% "samza-kv"         % "0.14.0",
      "org.apache.samza"  %% "samza-kv-rocksdb" % "0.14.0",
      "org.apache.samza"  %% "samza-kafka"      % "0.14.0",
      "org.apache.samza"  %  "samza-log4j"      % "0.14.0",
      "org.slf4j"         %  "slf4j-api"        % "1.7.7",
      "org.slf4j"         %  "slf4j-log4j12"    % "1.7.7",
      "org.apache.kafka"  %% "kafka"            % "2.0.0",
      "com.fasterxml.jackson.core" % "jackson-databind" % "2.9.7",
      "com.fasterxml.jackson.datatype" % "jackson-datatype-jdk8" % "2.9.7",
      "com.fasterxml.jackson.datatype" % "jackson-datatype-jsr310" %"2.9.7"
    )
  ).
  settings(packAutoSettings: _*).
  settings(SamzaTasks.tasks: _*)
