package co.ke.serviceman.api;

import co.ke.serviceman.model.AuthRequest;
import co.ke.serviceman.model.AuthResponse;
import co.ke.serviceman.service.UserService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    private UserService userService;

    @POST
    @Path("/login")
    public Response login(AuthRequest authRequest) {
        try {
            AuthResponse authResponse = userService.authenticate(
                authRequest.getUsername(), 
                authRequest.getPassword()
            );
            
            if (authResponse.isSuccess()) {
                return Response.ok(authResponse).build();
            } else {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(authResponse)
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new AuthResponse(false, "Server error during authentication"))
                    .build();
        }
    }
}