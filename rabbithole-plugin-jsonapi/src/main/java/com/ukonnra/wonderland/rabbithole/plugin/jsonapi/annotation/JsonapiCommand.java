package com.ukonnra.wonderland.rabbithole.plugin.jsonapi.annotation;

import com.ukonnra.wonderland.rabbithole.plugin.jsonapi.schema.JsonapiOperationType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonapiCommand {
  JsonapiOperationType type();
}
