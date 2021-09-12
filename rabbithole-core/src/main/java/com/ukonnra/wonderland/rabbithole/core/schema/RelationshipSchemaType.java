package com.ukonnra.wonderland.rabbithole.core.schema;

public sealed interface RelationshipSchemaType
    permits RelationshipSchemaType.Array, RelationshipSchemaType.Map, RelationshipSchemaType.Ref {
  record Ref(String type) implements RelationshipSchemaType {}

  record Array(RelationshipSchemaType item) implements RelationshipSchemaType {}

  record Map(RelationshipSchemaType values) implements RelationshipSchemaType {}
}
