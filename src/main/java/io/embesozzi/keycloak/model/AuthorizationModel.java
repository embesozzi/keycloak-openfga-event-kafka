package io.embesozzi.keycloak.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Optional;

public class AuthorizationModel {
    @JsonProperty("type_definitions")
    private final List<TypeDefinition> typeDefinitions;

    public AuthorizationModel(@JsonProperty("type_definitions") List<TypeDefinition> typeDefinitions) {
        this.typeDefinitions = typeDefinitions;
    }
    public static AuthorizationModel of(List<TypeDefinition> typeDefinitions){
        return new AuthorizationModel(typeDefinitions);
    }

    public TypeDefinition filterByType(String type) {
            return this.getTypeDefinitions().stream()
                    .filter(c -> c.getType().equals(type))
                    .findFirst().get();
    }

    public List<TypeDefinition> getTypeDefinitions() {
        return typeDefinitions;
    }

    @Override
    public String toString() {
        return "AuthorizationModel[" +
                "typeDefinitions=" + typeDefinitions + ']';
    }
}
