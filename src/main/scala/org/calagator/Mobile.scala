package org.calagator

import javax.servlet.http.{HttpServletRequest, HttpServletResponse, HttpServlet}
import java.net.URL

import org.scala_tools.time.Imports._
import xml.{NodeSeq, XML}
import xml.dtd.{DocType, PublicID}

class Mobile extends HttpServlet {

  val parse = org.joda.time.format.ISODateTimeFormat.dateTimeNoMillis.parseDateTime _
  def listEvents(events:NodeSeq) =
    <ul>{
      for (event <- events) yield
        <li>
          <a href={event\"id" text}>{ event\"title" text }</a>
          <div>{
            def displayTime(t:String) = parse(event\t text) toString "h:mma"
            displayTime("start-time") +"-"+ displayTime("end-time") +" at "+ (event\"venue"\"title" text)
          }</div>
        </li>
    }</ul>

  override def doGet(req:HttpServletRequest, resp:HttpServletResponse ) {
    val today = LocalDate.now
    val tomorrow = today + 1.day
    val query = "http://calagator.org/events.xml?date[start]="+today+"&date[end]="+tomorrow+"&commit=Filter"
    val calagator = XML load new URL(query)

    val path = req.getPathInfo.tail

    val (ongoing, current) = (calagator\"event")
      .groupBy( (event)=> parse(event\"start-time" text).toLocalDate )
      .partition(_._1 < today)

    // filter >= tomorrow
    val content =
      if (path == "")
        <div>
          <h2>Today</h2>
            <h3>{ today.dayOfWeek.asText }</h3>
            { listEvents(current(today)) }
          <h2>Tomorrow</h2>
            <h3>{ tomorrow.dayOfWeek.asText }</h3>
            { listEvents(current(tomorrow)) }
        </div>
      else
        <div>{
          val event = calagator\"event" filter ((e)=>(e\"id").text == path)
          val venue = event\"venue"
          <h4>{ event\"title" text }</h4>
          <div>{ venue\"title" text }</div>
          <div>{ venue\"street-address" text }</div>
          <div>{ List("locality", "region", "postal-code") map (venue\_ text) }</div>
        }</div>

    resp setContentType "application/xhtml+xml"
    XML.write(
      resp.getWriter,
      <html xmlns="http://www.w3.org/1999/xhtml">
        <head>
        </head>
        <body>
          {content}
        </body>
      </html>,
      "",
      false,
      new DocType("html", new PublicID("-//W3C//DTD XHTML Basic 1.1//EN", "http://www.w3.org/TR/xhtml-basic/xhtml-basic11.dtd"), List())
    )
  }

}

// vim: set ts=2 sw=2 et:
