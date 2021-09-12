package com.ukonnra.wonderland.rabbithole.core.schema;

public sealed interface AttributeSchemaType
    permits AttributeSchemaType.Array,
        AttributeSchemaType.Map,
        AttributeSchemaType.Primary,
        AttributeSchemaType.Ref {
  record Primary(Type type) implements AttributeSchemaType {
    public enum Type {
      STRING,
      INTEGER,
      FLOAT,
      BOOLEAN,
      TIMESTAMP
    }
  }

  record Ref(String type) implements AttributeSchemaType {}

  record Array(AttributeSchemaType item) implements AttributeSchemaType {}

  record Map(AttributeSchemaType values) implements AttributeSchemaType {}
}
