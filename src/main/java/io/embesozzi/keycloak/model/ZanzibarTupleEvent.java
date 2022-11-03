package io.embesozzi.keycloak.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Objects;

@JsonSerialize(using = OpenFgaTupleEventSerializer.class)
public abstract class ZanzibarTupleEvent {
    private java.lang.String object;
    private java.lang.String relation;
    private java.lang.String user;
    private String operation;

    public void setObject(java.lang.String object) { this.object = object; }
    public void setRelation(java.lang.String relation) { this.relation = relation; }
    public void setUser(java.lang.String user) { this.user = user; }
    public void setOperation(String operation) { this.operation = operation; }
    public java.lang.String getObject() { return object; }
    public java.lang.String getRelation() { return relation; }
    public java.lang.String getUser() { return user; }
    public String getOperation() { return operation; }
    @Override
    public int hashCode() {
        return Objects.hash(object, relation, user, operation);
    }

    protected static abstract class Builder<T extends Builder,B extends ZanzibarTupleEvent> {
        java.lang.String user;
        java.lang.String relationship;
        java.lang.String object;
        String operation;
        protected Builder() {
        }
        public T user(java.lang.String user) {
            this.user = user;
            return (T) this;

        }

        public T relationship(java.lang.String relationship) {
            this.relationship = relationship;
            return (T) this;
        }

        public T object(java.lang.String object) {
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
