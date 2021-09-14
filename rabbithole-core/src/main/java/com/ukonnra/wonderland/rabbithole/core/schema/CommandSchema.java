package com.ukonnra.wonderland.rabbithole.core.schema;

import edu.umd.cs.findbugs.annotations.Nullable;
import java.util.List;
import java.util.Map;

public record CommandSchema(
    String type,
    String name,
    @Nullable String idField,
    Map<String, FieldSchema<AttributeSchemaType>> attributes,
    List<CommandSchemaMetadata> metadata) {}
