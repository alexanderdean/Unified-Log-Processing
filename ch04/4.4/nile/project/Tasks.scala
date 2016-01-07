/* 
 * Copyright (c) 2015 Tim Harper.
 */
import sbt._
import Keys._

import xerial.sbt.Pack._

object Tasks {

  import Dependencies._

  private object RenderConfigTask {
    val key = TaskKey[Unit]("renderConfig", "Samza: renders the config file template", rank = KeyRanks.ATask)
    val setting = key := {
      val s = streams.value
      val packPath = pack.value
      for (f <- ((sourceDirectory in Compile).value / "config" * "*.properties").get) yield {
        val output = packPath / "config" / f.getName
        val template = IO.read(f)

        IO.write(
          output,
          template.
            replace("${target}", (target in Compile).value.getCanonicalPath).
            replace("${project.artifactId}", name.value).
            replace("${pom.version}", version.value)
        )
        s.log.info(s"Generated ${output} from ${f}")
      }
    }
  }

  private object GetSamzaShellTask {
    val key = TaskKey[File]("getSamzaShell", "Samza: sources the samza-shell artifact", rank = KeyRanks.ATask)
    val setting = key := {
      val samzaShellFile = (target in Compile).value / s"samza-shell-${V.samza}-dist.tgz"
      if (! samzaShellFile.exists)
        url(s"http://repo1.maven.org/maven2/org/apache/samza/samza-shell/${V.samza}/${samzaShellFile.name}") #> samzaShellFile !

      samzaShellFile
    }
  }

  private object ExtractSamzaShellTask {
    val key = TaskKey[Unit]("extractSamzaShell", "Samza: extracts the samza-shell artifact", rank = KeyRanks.ATask)
    val setting = key := {
      val log = streams.value.log
      val packPath = pack.value
      val shellPath = GetSamzaShellTask.key.value
      val binPath = packPath / "bin"

      binPath.mkdir()

      log.info(s"Extracting ${shellPath} to ${binPath}")
      shellPath #> s"tar xz -C ${binPath}" ! log
    }
  }

  private object PackageTgzTask {
    val key = TaskKey[File]("packageJob", "Samza: produces a tarball artifact for our job", rank = KeyRanks.ATask)
    val setting = key := {
      RenderConfigTask.key.value
      ExtractSamzaShellTask.key.value
      val packPath = pack.value
      val output = (target in Compile).value / s"${name.value}-${version.value}-dist.tar.gz"

      s"tar zc -C ${packPath.getCanonicalPath} ./" #> output !

      output
    }
  }

  lazy val tasks = Seq(
    RenderConfigTask.setting,
    GetSamzaShellTask.setting,
    ExtractSamzaShellTask.setting,
    PackageTgzTask.setting
  )
}