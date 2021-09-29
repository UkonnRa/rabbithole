package com.ukonnra.wonderland.rabbithole.core.schema;

import java.util.List;
import java.util.Map;

public record AggregateSchema(
    String plural,
    String type,
    Map<String, FieldSchema<AttributeSchemaType>> attributes,
    Map<String, FieldSchema<RelationshipSchemaType>> relationships,
    List<CommandSchema> commands) {}
