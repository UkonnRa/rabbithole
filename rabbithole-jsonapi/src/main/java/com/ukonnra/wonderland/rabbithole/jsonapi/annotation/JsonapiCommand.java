package com.ukonnra.wonderland.rabbithole.jsonapi.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonapiCommand {
  Type type();

  enum Type {
    CREATE,
    UPDATE,
    DELETE;
  }
}
