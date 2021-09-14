package com.ukonnra.wonderland.rabbithole.core.schema;

import java.util.List;
import java.util.Map;

public record ValueObjectSchema(String type, Data data) {
  public sealed interface Data {
    record Enum(List<String> values) implements Data {}

    record Obj(Map<String, FieldSchema<AttributeSchemaType>> attributes) implements Data {}

    record OneOf(Map<String, Data> members) implements Data {}
  }
}
