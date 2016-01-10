import sbt._
import Keys._

object AbandonedCartDetectorProjectBuild extends Build {

  import Dependencies._
  import BuildSettings._
  import Tasks._

  // Configure prompt to show current project
  override lazy val settings = super.settings :+ {
    shellPrompt := { s => Project.extract(s).currentProject.id + " > " }
  }

  // Define our project, with basic project information and library dependencies
  lazy val project = Project("abandoned-cart-detector", file("."))
    .settings(buildSettings: _*)
    .settings(tasks: _*)
    .settings(
      libraryDependencies ++= Seq(
        Libraries.samzaShell,
        Libraries.samzaApi,
        Libraries.samzaCore,
        Libraries.samzaYarn,
        Libraries.samzaKvRocksdb,
        Libraries.samzaKafka,
        Libraries.samzaLog4j,
        Libraries.sl4fjApi,
        Libraries.sl4fjLog4j12,
        Libraries.kafka,
        Libraries.jackson,
        Libraries.jacksonJava8,
        Libraries.jacksonDates
      )
    )
}
