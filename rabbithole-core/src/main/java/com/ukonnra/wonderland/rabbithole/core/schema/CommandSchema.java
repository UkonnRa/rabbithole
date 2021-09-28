package com.ukonnra.wonderland.rabbithole.core.schema;

import java.util.List;
import java.util.Map;

public record CommandSchema(
    String type,
    String name,
    Map<String, FieldSchema<AttributeSchemaType>> attributes,
    List<CommandSchemaMetadata> metadata) {}
