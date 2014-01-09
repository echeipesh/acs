organization := "acs"

name := "acs"

version := "0.1-SNAPSHOT"

scalaVersion := "2.10.3"

libraryDependencies  ++= Seq(
      "com.typesafe.akka" %% "akka-actor" % "2.2.3"
    , "com.typesafe.akka" %% "akka-testkit" % "2.2.3"
    , "org.slf4j" % "slf4j-simple" % "1.6.4"
    , "org.scalatest"      % "scalatest_2.10" % "2.0" % "test"
)
