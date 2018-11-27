package org.chat.server.rest;

import com.google.gson.Gson;

import java.util.ArrayList;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
/**
@Path("/users")
public class RestController {

    @GET
    @Path("/test")
    public String test() {
        System.out.println("rest geht");
        return "<h1>läuft</h1>";
    }
/**
    private static ArrayList<String> user = new ArrayList<>();

    private boolean registerUser(String userId) {
        if (user.contains(userId)) {
            System.out.println("User already registered");
            return false;
        } else {
            user.add(userId);
            return true;
        }
    }

    private boolean deleteUser(String clientId) {
        if (user.contains(clientId)) {
            user.remove(clientId);
            return true;
        } else {
            return false;
        }
    }

    @GET
    @Path("/Login/{username}")
    public Response Login(@PathParam("username") String userID) {
        String result = "Register User : " + userID;
        if (!registerUser(userID)) {
            return Response.status(201).entity(result).build();
        } else {
            return Response.status(200).entity(result).build();
        }
    }

    @GET
    @Path("/Logout/{username}")
    public Response Logout(@PathParam("username") String userID) {
        String result = "Delete User : " + userID;
        if (!deleteUser(userID)) {
            return Response.status(201).entity(result).build();
        } else {
            return Response.status(200).entity(result).build();
        }
    }

    @GET
    public Response GetUserList() {
        String userList = new Gson().toJson(user);
        return Response.status(200).entity(userList).build();
    }

    @POST
    @Path("/Login")
    @Consumes("application/json")
    public Response createProductInJSON(ChatMessage chatMessage) {

        String result = "Register User : " + chatMessage.getUserName();
        return null;
    }


}
*/