package com.ukonnra.wonderland.rabbithole.plugin.jsonapi.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidNullException;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import edu.umd.cs.findbugs.annotations.Nullable;
import java.io.IOException;
import java.util.Map;

@JsonSerialize(using = Link.Serializer.class)
@JsonDeserialize(using = Link.Deserializer.class)
public sealed interface Link permits Link.Obj, Link.Str {
  record Str(String value) implements Link {}

  record Obj(String href, @Nullable Map<String, Object> meta) implements Link {}

  static Link of(final String value) {
    return new Str(value);
  }

  class Serializer extends StdSerializer<Link> {
    public Serializer() {
      super(Link.class);
    }

    @Override
    public void serialize(Link value, JsonGenerator gen, SerializerProvider provider)
        throws IOException {
      if (value instanceof Link.Str str) {
        gen.writeString(str.value);
      } else if (value instanceof Link.Obj obj) {
        gen.writeStartObject();
        gen.writeStringField("href", obj.href);
        if (obj.meta != null) {
          gen.writeObjectField("meta", obj.meta);
        }
        gen.writeEndObject();
      }
    }
  }

  class Deserializer extends StdDeserializer<Link> {
    protected Deserializer() {
      super(Link.class);
    }

    @Override
    public Link deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      JsonNode node = p.getCodec().readTree(p);
      if (node.isTextual()) {
        return new Str(node.asText());
      } else {
        var hrefField = node.get("href");
        if (hrefField == null || hrefField.textValue() == null) {
          throw InvalidNullException.from(
              p, String.class, "Field[`href`] for Link.Obj cannot be null");
        }
        return new Obj(
            node.get("href").asText(),
            ctxt.readTreeAsValue(
                node.get("meta"),
                ctxt.getTypeFactory().constructMapType(Map.class, String.class, Object.class)));
      }
    }
  }
}
