package io.embesozzi.keycloak.service;

import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
public class ServiceHandlerFactory {

    public static ServiceHandler create(String serviceName, KeycloakSession session, Config.Scope config){
        switch (serviceName) {
            case ("KAFKA"):
                return new KafkaServiceHandler(session, config);
            case ("HTTP_CLIENT"):
                throw new IllegalArgumentException("This service has not been implemented yet... " + serviceName);
            default:
                throw new IllegalArgumentException("The service " + serviceName + " is not implemented");
        }
    }
}
