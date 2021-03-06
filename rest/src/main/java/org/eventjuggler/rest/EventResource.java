/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.eventjuggler.rest;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.eventjuggler.services.EventQuery;
import org.eventjuggler.services.EventService;
import org.eventjuggler.services.EventProperty;
import org.eventjuggler.services.UserService;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
@Path("/event")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EventResource {

    @Inject
    private EventService eventService;

    @Inject
    private UserService userService;

    @GET
    @Path("/{id}")
    public EventDetails getEvent(@PathParam("id") long eventId) {
        return new EventDetails(eventService.getEvent(eventId));
    }

    @GET
    @Path("/")
    public List<Event> getEvents(@QueryParam("first") Integer firstResult, @QueryParam("max") Integer maxResult,
            @QueryParam("query") String query, @QueryParam("tags") String tags, @QueryParam("sort") String sortBy,
            @QueryParam("order") String order) {
        EventQuery q = eventService.query();

        if (firstResult != null) {
            q.firstResult(firstResult);
        }

        if (maxResult != null) {
            q.maxResult(maxResult);
        }

        if (query != null) {
            q.query(query);
        }

        if (tags != null) {
            q.tags(ObjectFactory.createTags(tags));
        }

        if (sortBy != null) {
            q.sortBy(EventProperty.valueOf(sortBy.toUpperCase()), order == null || order.equals("asc"));
        }

        List<Event> events = new LinkedList<Event>();
        for (org.eventjuggler.model.Event e : q.getEvents()) {
            events.add(new Event(e));
        }
        return events;
    }

    @GET
    @Path("/{id}/rsvp")
    public List<Attendance> getRSVP(@PathParam("id") long eventId) {
        return ObjectFactory.createAttendance(eventService.getEvent(eventId).getAttendance());
    }

    @PUT
    @Path("/")
    public void createEvent(Event event) {
        eventService.create(event.toInternal());
    }

    @DELETE
    @Path("/{id}")
    public void deleteEvent(@PathParam("id") long eventId) {
        eventService.remove(eventService.getEvent(eventId));
    }

    @PUT
    @Path("/{id}/rsvp")
    public void createRSVP(@PathParam("id") long eventId, String response) {
        // TODO
    }

    // @Context
    // private SecurityContext securityContext;
    //
    // private User getUser() {
    // if (securityContext.getUserPrincipal() == null) {
    // return null;
    // }
    //
    // String id = securityContext.getUserPrincipal().getName();
    // return userService.getUser(id);
    // }

}
