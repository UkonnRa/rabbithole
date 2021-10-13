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
import java.util.Objects;
import java.util.stream.StreamSupport;

@JsonSerialize(using = ResourceLinkage.Serializer.class)
@JsonDeserialize(using = ResourceLinkage.Deserializer.class)
public sealed interface ResourceLinkage {
  record Obj(ResourceIdentifier value) implements ResourceLinkage {}

  final class Arr extends ArrayList<ResourceIdentifier> implements ResourceLinkage {
    public Arr(ResourceIdentifier... c) {
      super(Arrays.stream(c).toList());
    }

    public Arr(Collection<? extends ResourceIdentifier> c) {
      super(c);
    }
  }

  class Serializer extends StdSerializer<ResourceLinkage> {
    public Serializer() {
      super(ResourceLinkage.class);
    }

    @Override
    public void serialize(ResourceLinkage value, JsonGenerator gen, SerializerProvider provider)
        throws IOException {
      if (value instanceof ResourceLinkage.Arr arr) {
        gen.writeStartArray();
        for (var item : arr) {
          gen.writeObject(item);
        }
        gen.writeEndArray();
      } else if (value instanceof ResourceLinkage.Obj obj) {
        gen.writeObject(obj.value);
      }
    }
  }

  class Deserializer extends StdDeserializer<ResourceLinkage> {
    protected Deserializer() {
      super(ResourceLinkage.class);
    }

    @Override
    public ResourceLinkage deserialize(JsonParser p, DeserializationContext ctxt)
        throws IOException {
      JsonNode node = p.getCodec().readTree(p);
      if (node.isArray()) {
        return new Arr(
            StreamSupport.stream(node.spliterator(), false)
                .map(
                    n -> {
                      try {
                        return ctxt.readTreeAsValue(n, ResourceIdentifier.class);
                      } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                      }
                    })
                .filter(Objects::nonNull)
                .toList());
      } else {
        return new Obj(ctxt.readTreeAsValue(node, ResourceIdentifier.class));
      }
    }
  }
}
