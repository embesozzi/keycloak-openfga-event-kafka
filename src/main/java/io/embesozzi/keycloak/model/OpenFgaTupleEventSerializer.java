package io.embesozzi.keycloak.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class OpenFgaTupleEventSerializer extends StdSerializer<ZanzibarTupleEvent> {

    public OpenFgaTupleEventSerializer() {
        this(null);
    }

    public OpenFgaTupleEventSerializer(Class<ZanzibarTupleEvent> t) {
        super(t);
    }

    @Override
    public void serialize(
            ZanzibarTupleEvent value, JsonGenerator gen, SerializerProvider provider)
            throws IOException, JsonProcessingException {

        gen.writeStartObject();
            gen.writeObjectFieldStart(value.getOperation());
                gen.writeArrayFieldStart("tuple_keys");
                    gen.writeStartObject();
                        gen.writeStringField("object", value.getObject());
                        gen.writeStringField("relation", value.getRelation());
                        gen.writeStringField("user", value.getUser());
                    gen.writeEndObject();
                gen.writeEndArray();
            gen.writeEndObject();
        gen.writeEndObject();
    }
}
