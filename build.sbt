organization := "org.calagator"

name := "calagator_mobile"

version := "0.1-SNAPSHOT"

scalaVersion := "2.9.1"

seq(webSettings :_*)

libraryDependencies ++= Seq(
  "org.scala-tools.time" %% "time" % "0.5",
  "org.eclipse.jetty" % "jetty-webapp" % "7.5.0.v20110901" % "jetty",
  "javax.servlet" % "servlet-api" % "2.5" % "provided"
)
