package co.ke.serviceman;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("api")
public class App extends Application {
    // This class is the entry point for your Jakarta EE application.
    // The @ApplicationPath("api") annotation tells the server to map
    // all your resource classes (like UserResource and CompanyResource)
    // to the "/api" base URL.
}