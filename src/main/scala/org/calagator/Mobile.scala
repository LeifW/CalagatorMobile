package org.calagator

import javax.servlet.http.{HttpServletRequest, HttpServletResponse, HttpServlet}
import javax.servlet.ServletConfig

class Mobile extends HttpServlet {

  override def init(config: ServletConfig) {
    super.init(config)
  }

  override def doGet(req:HttpServletRequest, resp:HttpServletResponse ) {
    resp.getWriter.println("Foo-foo")
  }

}

// vim: set ts=4 sw=4 et:
