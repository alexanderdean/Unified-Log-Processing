import sbt._
import Keys._

import Tasks._

import xerial.sbt.Pack._

object ProjectBuild extends Build {

  lazy val basicSettings = Seq[Setting[_]](
    organization  := "nile",
    version       := "0.1.0",
    description   := "Abandoned cart detector for Samza",
    scalaVersion  := "2.10.4",
    scalacOptions := Seq("-deprecation", "-encoding", "utf8")
  )

  // Define our project, with basic project information and library dependencies
  lazy val project = Project("abandoned-cart-detector", file("."))
    .settings(basicSettings: _*)
    .settings(packAutoSettings: _*)
    .settings(tasks: _*)
    .settings(
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

        "com.fasterxml.jackson.core"     % "jackson-databind"        % "2.6.3",
        "com.fasterxml.jackson.datatype" % "jackson-datatype-jdk8"   % "2.5.1",
        "com.fasterxml.jackson.datatype" % "jackson-datatype-jsr310" % "2.6.4"
      )
    )
}
