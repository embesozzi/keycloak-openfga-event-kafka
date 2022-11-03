package io.embesozzi.keycloak.model;

import org.keycloak.utils.StringUtil;

public class OpenFgaTupleEvent extends ZanzibarTupleEvent {

    public static final String OPERATION_WRITES = "writes";
    public static final String OPERATION_DELETES = "deletes";

    private OpenFgaTupleEvent(Builder builder) {
        super.setUser(builder.user);
        super.setRelation(builder.relationship);
        super.setObject(builder.object);
        super.setOperation(builder.operation);
    }

    public static class Builder extends ZanzibarTupleEvent.Builder<Builder, OpenFgaTupleEvent> {

        String userId;
        String objectId;
        String userType;
        String objectType;

        public Builder(){
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder userType(String userType) {
            this.userType = userType;
            return this;
        }

        public Builder objectId(String objectId) {
            this.objectId = objectId;
            return this;
        }

        public Builder objectType(TypeDefinition typeDef) {
            this.objectType = typeDef.getType();
            return this;
        }

        public Builder withObjectRelation(ObjectRelation objType) {
            this.userType = objType.getObject();
            this.relationship = objType.getRelation();
            return this;
        }

        public Builder objectType(String objectType) {
            this.objectType = objectType;
            return this;
        }

        @Override
        public OpenFgaTupleEvent build() {
            if(!StringUtil.isBlank(userType) && !StringUtil.isBlank(userId)) {
                this.user(userType + ":" +  userId);
            }
            if(!StringUtil.isBlank(objectType) && !StringUtil.isBlank(objectId)) {
                this.object(objectType + ":" + objectId);
            }
            validate();
            return new OpenFgaTupleEvent(this);
        }

        private void validate() throws IllegalStateException {
            StringBuilder message = new StringBuilder();
            message.append(StringUtil.isBlank(user) ? "Attribute user is must not be null\n" : "");
            message.append(StringUtil.isBlank(object) ? "Attribute object is must not be null\n" : "");
            message.append(StringUtil.isBlank(relationship) ? "Attribute relationship name is must not be null\n" : "");
            message.append(operation == null ? "Attribute operation is must not be null" : "");
            if (message.length() > 0) {
                throw new IllegalStateException(message.toString());
            }
        }
    }
}
