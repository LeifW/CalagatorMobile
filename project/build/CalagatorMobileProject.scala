import sbt._

class CalagatorMobileProject(info: ProjectInfo) extends DefaultWebProject(info)
{
  val time = "org.scala-tools.time" %% "time" % "0.3"
  val servletApi = "javax.servlet" % "servlet-api" % "2.5" % "provided"
  val jetty7 = "org.eclipse.jetty" % "jetty-webapp" % "7.4.0.v20110414" % "test"
}

// vim: set ts=4 sw=4 et:
