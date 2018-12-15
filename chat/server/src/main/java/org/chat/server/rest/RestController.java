package org.chat.server.rest;

import com.google.gson.Gson;
import org.chat.common.ChatMessage;
import org.chat.server.KafkaChatProcess;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


import javax.ws.rs.*;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

@Path("/users")
public class RestController extends Application {

    Set<String> users = new HashSet<>();

    @GET
    @Path("/currentusers")
    public Response getAllUsers() {
        String userList = new Gson().toJson(users);
        return Response.status(200).entity(userList).build();
    }

    @POST
    @Path("/login/{username}")
    public Response login(@PathParam("username") String userName) {
        if (users.contains(userName)) {
            return Response.status(201).entity("User " + userName + " already logged in").build();
        } else {
            if (KafkaChatProcess.getInstance() == null) {
                KafkaChatProcess kCP = new KafkaChatProcess();
                kCP.initializeConsumer();
                kCP.startRecievingMessages();
            }
            users.add(userName);
            return Response.status(200).entity("User " + userName + " logged in").build();
        }
    }

    @DELETE
    @Path("/logout/{username}")
    public Response logout(@PathParam("username") String userName) {
        if (users.contains(userName)) {
            users.remove(userName);
            return Response.status(200).entity("User " + userName + " deleted").build();
        } else {
            return Response.status(201).entity("User " + userName + " wasn't logged in").build();
        }
    }
}
