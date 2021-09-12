package com.ukonnra.wonderland.rabbithole.core.schema;

public record FieldSchema<T>(boolean isNullable, T type) {}
