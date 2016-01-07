import sbt._
import Keys._

import xerial.sbt.Pack._

object BuildSettings {

  // Basic settings for our app
  lazy val basicSettings = Seq[Setting[_]](
    organization  := "nile",
    version       := "0.1.0",
    description   := "Abandoned cart detector for Samza",
    scalaVersion  := "2.10.4",
    scalacOptions := Seq("-deprecation", "-encoding", "utf8"),
    resolvers     ++= Dependencies.resolutionRepos
  )

  // sbt-pack settings for building a YARN-ready job .tgz
  lazy val sbtPackSettings = packAutoSettings //++
    //Seq(packExcludeJars := Seq("samza-kv_2\\.10.*\\.jar"))

  lazy val buildSettings = basicSettings ++ sbtPackSettings
}
