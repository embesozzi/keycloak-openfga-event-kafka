package io.embesozzi.keycloak.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ObjectRelation {
    private final String object;
    private final String relation;

    public ObjectRelation(@JsonProperty("relation") String relation, @JsonProperty("object") String object) {
        this.object = object;
        this.relation = relation;
    }

    public static ObjectRelation of(String relation,String object) {
        return new ObjectRelation(relation, object);
    }

    public String getObject() {
        return object;
    }

    public String getRelation() {
        return relation;
    }

    @Override
    public String toString() {
        return "ObjectRelation[" +
                "object=" + object + ", " +
                "relation=" + relation + ']';
    }
}
