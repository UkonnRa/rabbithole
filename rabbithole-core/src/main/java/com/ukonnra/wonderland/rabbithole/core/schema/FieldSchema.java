package com.ukonnra.wonderland.rabbithole.core.schema;

public record FieldSchema<T>(String name, boolean isNullable, T type) {}
