package io.embesozzi.keycloak.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Objects;

@JsonSerialize(using = OpenFgaTupleEventSerializer.class)
public abstract class ZanzibarTupleEvent {

    private String object;
    private String relation;
    private String user;
    private String operation;

    public void setObject(String object) {
        this.object = object;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }
    public void setUser(String user) {
        this.user = user; }


    public void setOperation(String operation) {
        this.operation = operation;
    }

    public java.lang.String getObject() { return object; }

    public java.lang.String getRelation() { return relation; }

    public java.lang.String getUser() { return user; }

    public String getOperation() { return operation; }

    @Override
    public int hashCode() {
        return Objects.hash(object, relation, user, operation);
    }

    protected static abstract class Builder<T extends Builder,B extends ZanzibarTupleEvent> {

        String user;
        String relationship;
        String object;
        String operation;

        protected Builder() {
        }

        public T user(String user) {
            this.user = user;
            return (T) this;

        }

        public T relationship(String relationship) {
            this.relationship = relationship;
            return (T) this;
        }

        public T object(String object) {
            this.object = object;
            return (T) this;
        }

        public T operation(String operation) {
            this.operation = operation;
            return (T) this;
        }
        public abstract B build();
    }

    @Override
    public java.lang.String toString() {
        return "ZanzibarTupleEvent[" +
                "operation=" + operation + ", " +
                "object=" + object + ", " +
                "relation=" + relation + ", " +
                "user=" + user + ']';
    }
}
