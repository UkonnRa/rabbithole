package com.ukonnra.wonderland.rabbithole.core.hook;

import com.ukonnra.wonderland.rabbithole.core.schema.ValueObjectSchema;

public interface OnParserValueObjectSchema {
  ValueObjectSchema parse(Class<?> clazz);
}
