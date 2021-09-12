package com.ukonnra.wonderland.rabbithole.core.schema;

import com.ukonnra.wonderland.rabbithole.core.filter.Filter;
import java.util.List;
import java.util.Map;

public record AggregateSchema(
    String type,
    String name,
    Map<String, FieldSchema<AttributeSchemaType>> attributes,
    Map<String, FieldSchema<RelationshipSchemaType>> relationships,
    Map<String, Filter> filters,
    List<CommandSchema> commands) {}
