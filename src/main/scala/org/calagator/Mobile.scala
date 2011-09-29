package org.calagator

import javax.servlet.http.{HttpServletRequest, HttpServletResponse, HttpServlet}
import java.net.URL

import org.scala_tools.time.Imports._
import xml.{NodeSeq, XML}
import xml.dtd.{DocType, PublicID}

class Mobile extends HttpServlet {

  val parse = org.joda.time.format.ISODateTimeFormat.dateTimeNoMillis.parseDateTime _
  lazy val context = getServletContext

  def listEvents(events:NodeSeq) =
    for (event <- events) yield
      <li>
        <a href={event\"id" text}>{ event\"title" text }</a>
        <div>{
          def displayTime(t:String) = parse(event\t text) toString "h:mma"
          displayTime("start-time") +"-"+ displayTime("end-time") +" at "+ (event\"venue"\"title" text)
        }</div>
      </li>

  override def doGet(request:HttpServletRequest, response:HttpServletResponse ) {
    val now = DateTime.now
    val today = now.toLocalDate
    val tomorrow = today + 1.day
    val calagator = {
      val last_fetched = context.getAttribute("last_fetched").asInstanceOf[DateTime]
      // If it's never been fetched before OR it's been more three hours OR it happened yesterday (so tommorow's not blank after midnight rolls over)
      if ( last_fetched == null || last_fetched < now - 3.hours || last_fetched.toLocalDate < today  ) { 
        val query = "http://calagator.org/events.xml?date[start]="+today+"&date[end]="+tomorrow+"&commit=Filter"
        val calagator = XML load new URL(query)
        context.setAttribute("calagator", calagator)
        context.setAttribute("last_fetched", now)
        calagator
      } else {
        context.getAttribute("calagator").asInstanceOf[NodeSeq]
      }
    }

    val body = request.getPathInfo.tail match {
      case "" =>  // Root level: index view
        val groupedEvents = calagator\"event" groupBy ( (event)=> parse(event\"start-time" text).toLocalDate )
        <body>
          <h2>Today</h2>
          {for ((day, events) <- groupedEvents
             if (day < today)) yield
            <h4>Started { day.dayOfWeek.asText }</h4>
            <ul>{ listEvents(events) }</ul>
          }
          <h3>{ today.dayOfWeek.asText }</h3>
          <ul>{ listEvents(groupedEvents.get(today).toSeq.flatten) }</ul>
          <h2>Tomorrow</h2>
          {for ((day, events) <- groupedEvents map { case(day, events)=> (day, events filter ((e)=> parse(e\"end-time" text).toLocalDate >= tomorrow)) }
             if (day != tomorrow && !events.isEmpty)) yield
            <h4>Started { day.dayOfWeek.asText }</h4>
            <ul>{ listEvents(events) }</ul>
          }
          <h3>{ tomorrow.dayOfWeek.asText }</h3>
          <ul>{ listEvents(groupedEvents.get(tomorrow).toSeq.flatten) }</ul>
        </body>
      case path =>       // If path was passed in, look up an event by that id and display detail.
        val event = calagator\"event" filter ((e)=>(e\"id").text == path)
        val venue = event\"venue"
        val venueLoc = List("latitude", "longitude") map (venue\_ text) mkString ","
        def displayTime(t:String) = parse(event\t text) toString "h:mma"
        <body>
          <h3>{ event\"title" text }</h3>
          <p>{ event\"description" text }</p>
          <div>{ displayTime("start-time") }-{ displayTime("end-time") }</div>
          <div>{ venue\"title" text }</div>
          <div>{ venue\"street-address" text }</div>
          <div>{ List("locality", "region", "postal-code") map (venue\_ text) }</div>
          <img src={ "http://maps.google.com/maps/api/staticmap?center="+venueLoc+"&zoom=15&size=130x110&sensor=false&markers="+venueLoc } />
        </body>
    }

    response setContentType "application/xhtml+xml; charset=UTF-8"
    XML.write(
      response.getWriter,
      <html xmlns="http://www.w3.org/1999/xhtml">
        <head>
          <title>Calagator mobile</title>
        </head>
        {body}
      </html>,
      "UTF-8",
      false,
      DocType("html", PublicID("-//W3C//DTD XHTML Basic 1.1//EN", "http://www.w3.org/TR/xhtml-basic/xhtml-basic11.dtd"), List())
    )
  }

}

// vim: set ts=2 sw=2 et:
