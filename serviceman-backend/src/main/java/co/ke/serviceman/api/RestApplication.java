package co.ke.serviceman.api;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/api")
public class RestApplication extends Application {
    // Automatically scans for @Path classes in the project
}
