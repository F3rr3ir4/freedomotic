/**
 *
 * Copyright (c) 2009-2014 Freedomotic team http://freedomotic.com
 *
 * This file is part of Freedomotic
 *
 * This Program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2, or (at your option) any later version.
 *
 * This Program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Freedomotic; see the file COPYING. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package com.freedomotic.plugins.devices.restapiv3.resources.jersey;

import com.freedomotic.app.Freedomotic;
import com.freedomotic.plugins.devices.restapiv3.filters.ItemNotFoundException;
import com.freedomotic.plugins.devices.restapiv3.utils.AbstractResource;
import com.freedomotic.reactions.Command;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author matteo
 */
@Path("commands/user")
@Api(value = "userCommands", description = "Operations on user commands", position = 5)
public class UserCommandResource extends AbstractResource<Command> {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "List all user commands", position = 10)
    @Override
    public Response list() {
        return super.list();
    }

    /**
     * @param UUID
     * @return
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get an user's command", position = 20)
    @Path("/{id}")
    @ApiResponses(value = {
        @ApiResponse(code = 404, message = "User's command not found")
    })
    @Override
    public Response get(
            @ApiParam(value = "UUID of user's command to fetch (e.g. df28cda0-a866-11e2-9e96-0800200c9a66)", required = true)
            @PathParam("id") String UUID) {
        return super.get(UUID);
    }

    public UserCommandResource() {
        authContext = "commands";
    }

    @Override
    protected URI doCreate(Command c) throws URISyntaxException {
        c.setHardwareLevel(false);
        api.commands().create(c);
        return createUri(c.getUuid());
    }

    @Override
    protected boolean doDelete(String UUID) {
        return api.commands().delete(UUID);
    }

    @Override
    protected Command doUpdate(Command c) {
        return api.commands().modify(c.getUuid(), c);
    }

    @Override
    protected List<Command> prepareList() {
        List<Command> cl = new ArrayList<Command>();
        cl.addAll(api.commands().getUserCommands());
        return cl;
    }

    @Override
    protected Command prepareSingle(String uuid) {
        return api.commands().get(uuid);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}/run")
    @ApiOperation("Fires a user command")
    public Response fire(
            @ApiParam(value = "ID of Command to execute", required = true)
            @PathParam("id") String UUID) {
        Command c = api.commands().get(UUID);
        if (c != null) {
            return fire(c);
        }
        throw new ItemNotFoundException();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/runonce")
    @ApiOperation("Fires a custom command")
    public Response fire(Command c) {
        if (c != null) {

            Command reply = Freedomotic.sendCommand(c);
            if (c.getReplyTimeout() > 0) {
                return Response.ok(reply).build();
            }
        }
        return Response.accepted(c).build();
    }

    @Override
    protected URI doCopy(String uuid) {
        Command c = api.commands().copy(uuid);
        return createUri(c.getUuid());
    }
}
