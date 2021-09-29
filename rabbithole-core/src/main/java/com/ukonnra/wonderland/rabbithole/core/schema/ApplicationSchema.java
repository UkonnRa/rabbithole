package com.ukonnra.wonderland.rabbithole.core.schema;

import java.util.List;

public record ApplicationSchema(
    List<AggregateSchema> aggregates,
    List<ValueObjectSchema> valueObjects,
    List<ApplicationSchemaMetadata> metadata) {}
