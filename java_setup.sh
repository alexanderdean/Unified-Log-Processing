#!/bin/bash

function install_requirements {
  sudo apt-get install -y software-properties-common \
    python-software-properties \
    debconf-utils
}

function install_oracle_package_archive {
  install_requirements
  sudo add-apt-repository -y ppa:webupd8team/java
  sudo apt-get -y update
}

function install_gradle_package_archive {
  sudo add-apt-repository -y ppa:cwchien/gradle
  sudo apt-get -y update
}

function accept_license_agreement {
  #echo debconf shared/accepted-oracle-license-v1-1 select true | \
  #  sudo debconf-set-selections
  #echo debconf shared/accepted-oracle-license-v1-1 seen true | \
  #  sudo debconf-set-selections
  echo "oracle-java8-installer shared/accepted-oracle-license-v1-1 select true" | \
    sudo debconf-set-selections
}

function configure_environment_variables {
  printf '\nJAVA_HOME="/usr/lib/jvm/java-8-oracle/jre"\n' | \
    sudo tee -a /etc/environment
  printf '\nPATH="$PATH:$JAVA_HOME/bin"\n'
}

function configure_gradle_jdk {
  #printf 'org.gradle.java.home=/usr/lib/jvm/java-8-oracle/jre\n' | \
  #  tee -a ~/.gradle/gradle.properties
  echo "no op"
}

function install_jdk {
  install_oracle_package_archive
  accept_license_agreement
  sudo apt-get install -y oracle-java8-installer
  configure_environment_variables
}

function install_gradle {
  install_gradle_package_archive
  sudo apt-get install -y gradle
  configure_gradle_jdk
}

install_jdk

install_gradle
