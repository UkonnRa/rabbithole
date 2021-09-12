package com.ukonnra.wonderland.rabbithole.core.schema;

import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.Nullable;

public record CommandSchema(
    String type,
    String name,
    @Nullable String idField,
    Map<String, FieldSchema<AttributeSchemaType>> attributes,
    List<CommandSchemaMetadata> metadata) {}
