package com.ukonnra.wonderland.rabbithole.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AggregateRoot {
  /** @return AggregateRoot plural */
  String plural();

  Class<?> command();

  /** @return AggregateRoot id field type, the default is "id" */
  String idField() default "id";
}
