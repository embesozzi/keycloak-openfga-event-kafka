package io.embesozzi.keycloak.service;

import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
public class ServiceHandlerFactory {

    public static ServiceHandler create(String serviceName, KeycloakSession session, Config.Scope config){
        switch (serviceName) {
            case ("KAFKA"):
                return new KafkaServiceHandler(session, config);
            default:
                throw new IllegalArgumentException("The service " + serviceName + " is not implemented");
        }
    }
}
