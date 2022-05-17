package edu.uwb.css533.service;

import edu.uwb.css533.service.resources.UserResources;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class ToDoListsApplication extends Application<ToDoListsConfiguration> {

    public static void main(final String[] args) throws Exception {
        new ToDoListsApplication().run(args);
    }

    @Override
    public String getName() {
        return "ToDoLists";
    }

    @Override
    public void initialize(final Bootstrap<ToDoListsConfiguration> bootstrap) {
        // TODO: application initialization
    }

    @Override
    public void run(final ToDoListsConfiguration configuration,
                    final Environment environment) {
        // TODO: implement application
        UserResources userResources = new UserResources();
        environment.jersey().register(userResources);
    }

}
