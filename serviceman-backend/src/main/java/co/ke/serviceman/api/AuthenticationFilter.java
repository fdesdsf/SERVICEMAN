package co.ke.serviceman.api;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.security.Principal;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        // ✅ Skip authentication for login endpoint (allow login without token)
        if (requestContext.getUriInfo().getPath().contains("auth/login")) {
            return;
        }

        // ✅ 1. Get the token from the Authorization header
        String authHeader = requestContext.getHeaderString("Authorization");
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // Remove "Bearer " prefix
            
            // ✅ 2. Validate token and get username
            String username = validateTokenAndGetUsername(token);
            
            if (username != null) {
                // ✅ 3. Set up the security context with the authenticated user
                requestContext.setSecurityContext(new SecurityContext() {
                    @Override
                    public Principal getUserPrincipal() {
                        return () -> username;
                    }

                    @Override
                    public boolean isUserInRole(String role) {
                        return true; // You can implement role checking later
                    }

                    @Override
                    public boolean isSecure() {
                        return requestContext.getUriInfo().getRequestUri().getScheme().equals("https");
                    }

                    @Override
                    public String getAuthenticationScheme() {
                        return "Bearer";
                    }
                });
                return;
            }
        }
        
        // ✅ If no valid token, let the request continue (for now)
        // This allows endpoints to handle unauthorized access themselves
    }

    private String validateTokenAndGetUsername(String token) {
        try {
            // ✅ Simple token validation: token should be "username:timestamp"
            if (token.contains(":")) {
                String[] parts = token.split(":");
                String username = parts[0];
                long timestamp = Long.parseLong(parts[1]);
                
                // ✅ Check if token is not expired (24 hours validity)
                long currentTime = System.currentTimeMillis();
                long twentyFourHours = 24 * 60 * 60 * 1000;
                
                if (currentTime - timestamp < twentyFourHours) {
                    return username; // ✅ Token is valid
                }
            }
            return null; // ✅ Token is invalid or expired
        } catch (Exception e) {
            return null; // ✅ Token format is incorrect
        }
    }
}