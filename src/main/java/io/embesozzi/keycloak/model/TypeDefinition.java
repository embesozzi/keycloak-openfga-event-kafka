package io.embesozzi.keycloak.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Optional;

public class TypeDefinition {
    private String type;
    List<ObjectRelation> relationships;
    public TypeDefinition(@JsonProperty("type") String type, @JsonProperty("relationships") List<ObjectRelation> relationships) {
        this.relationships = relationships;
        this.type = type;
    }
    public static TypeDefinition of(String type, List<ObjectRelation> relationships) {
        return new TypeDefinition(type, relationships);
    }

    public ObjectRelation filterByObject(String object) {
        return this.relationships.stream()
                .filter(r -> r.getObject().equals(object))
                .findFirst().get();
    }

    public String getType() { return this.type; }
    public List<ObjectRelation> getRelationships()  { return this.relationships; }

    @Override
    public String toString() {
        return "TypeDefinition[" +
                "type=" + type + ", " +
                "relations=" + relationships + ']';
    }
}
