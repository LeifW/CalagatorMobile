import sbt._

class CalagatorMobileProject(info: ProjectInfo) extends DefaultWebProject(info)
{
  val jodaTime = "joda-time" % "joda-time" % "1.6.2"
  val servletApi = "javax.servlet" % "servlet-api" % "2.5" % "provided"
  val jetty7 = "org.eclipse.jetty" % "jetty-webapp" % "7.4.0.v20110414" % "test"
}

// vim: set ts=4 sw=4 et:
