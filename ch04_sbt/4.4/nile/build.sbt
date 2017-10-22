lazy val root = (project in file(".")).
  settings(
    name          := "abandoned-cart-detector",
    organization  := "nile",
    version       := "0.1.0",
    scalaVersion  := "2.10.4",
    javacOptions  := Seq("-source", "1.8", "-target", "1.8", "-Xlint"),
    scalacOptions := Seq("-deprecation", "-encoding", "utf8"),
    libraryDependencies ++= Seq(
      "org.apache.samza"  %  "samza-shell"      % "0.9.1",
      "org.apache.samza"  %  "samza-api"        % "0.9.1",
      "org.apache.samza"  %% "samza-core"       % "0.9.1",
      "org.apache.samza"  %% "samza-yarn"       % "0.9.1",
      "org.apache.samza"  %% "samza-kv"         % "0.9.1",
      "org.apache.samza"  %% "samza-kv-rocksdb" % "0.9.1",
      "org.apache.samza"  %% "samza-kafka"      % "0.9.1",
      "org.apache.samza"  %  "samza-log4j"      % "0.9.1",
      "org.slf4j"         %  "slf4j-api"        % "1.7.7",
      "org.slf4j"         %  "slf4j-log4j12"    % "1.7.7",
      "org.apache.kafka"  %% "kafka"            % "0.8.2.1",
      "com.fasterxml.jackson.core" % "jackson-databind" % "2.6.3",
      "com.fasterxml.jackson.datatype" % "jackson-datatype-jdk8" % "2.5.1",
      "com.fasterxml.jackson.datatype" % "jackson-datatype-jsr310" %"2.6.4"
    )
  ).
  settings(packAutoSettings: _*).
  settings(SamzaTasks.tasks: _*)
