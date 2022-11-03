package io.embesozzi.keycloak.service;

import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public abstract class ServiceHandler {

    protected final KeycloakSession session;
    protected final Config.Scope config;

    public ServiceHandler(KeycloakSession session, Config.Scope config ) {
        this.session = session;
        this.config = config;
    }
    public abstract void handle(String eventID, String eventValue)  throws ExecutionException, InterruptedException, TimeoutException;

    public abstract void validateConfig();

    public void close() {
        // close this instance of the event listener
    }

}
