package com.ukonnra.wonderland.rabbithole.plugin.jsonapi.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

@JsonSerialize(using = Relationships.Serializer.class)
@JsonDeserialize(using = Relationships.Deserializer.class)
public sealed interface Relationships {
  record Obj(Relationship value) implements Relationships {}

  final class Arr extends ArrayList<Relationship> implements Relationships {
    public Arr(Relationship... c) {
      super(Arrays.stream(c).toList());
    }

    public Arr(Collection<? extends Relationship> c) {
      super(c);
    }
  }

  class Serializer extends StdSerializer<Relationships> {
    public Serializer() {
      super(Relationships.class);
    }

    @Override
    public void serialize(Relationships value, JsonGenerator gen, SerializerProvider provider)
        throws IOException {
      if (value instanceof Relationships.Arr arr) {
        gen.writeStartArray();
        for (var item : arr) {
          gen.writeObject(item);
        }
        gen.writeEndArray();
      } else if (value instanceof Relationships.Obj obj) {
        gen.writeObject(obj.value);
      }
    }
  }

  class Deserializer extends StdDeserializer<Relationships> {
    protected Deserializer() {
      super(Relationships.class);
    }

    @Override
    public Relationships deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      JsonNode node = p.getCodec().readTree(p);
      if (node.isArray()) {
        return new Arr(
            ctxt.<Collection<Relationship>>readTreeAsValue(
                node,
                ctxt.getTypeFactory()
                    .constructCollectionType(Collection.class, Relationship.class)));
      } else {
        return new Obj(ctxt.readTreeAsValue(node, Relationship.class));
      }
    }
  }
}
