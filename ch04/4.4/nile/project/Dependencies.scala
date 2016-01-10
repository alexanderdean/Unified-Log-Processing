import sbt._

object Dependencies {
  val resolutionRepos = Seq()

  object V {
    val samza      = "0.9.1"
    val slf4j      = "1.7.7"
    val kafka      = "0.8.2.1"
  }

  object Libraries {

    val samzaShell     = "org.apache.samza"  %  "samza-shell"      % V.samza
    val samzaApi       = "org.apache.samza"  %  "samza-api"        % V.samza
    val samzaCore      = "org.apache.samza"  %% "samza-core"       % V.samza
    val samzaYarn      = "org.apache.samza"  %% "samza-yarn"       % V.samza
    val samzaKv        = "org.apache.samza"  %% "samza-kv"         % V.samza
    val samzaKvRocksdb = "org.apache.samza"  %% "samza-kv-rocksdb" % V.samza
    val samzaKafka     = "org.apache.samza"  %% "samza-kafka"      % V.samza
    val samzaLog4j     = "org.apache.samza"  %  "samza-log4j"      % V.samza

    val sl4fjApi       = "org.slf4j"         %  "slf4j-api"        % V.slf4j
    val sl4fjLog4j12   = "org.slf4j"         %  "slf4j-log4j12"    % V.slf4j

    val kafka          = "org.apache.kafka"  %% "kafka"            % V.kafka
    val jackson        = "com.fasterxml.jackson.core" % "jackson-databind" % "2.6.3"
    val jacksonJava8   = "com.fasterxml.jackson.datatype" % "jackson-datatype-jdk8" % "2.5.1"
    val jacksonDates   = "com.fasterxml.jackson.datatype" % "jackson-datatype-jsr310" % "2.6.4"
  }
}
