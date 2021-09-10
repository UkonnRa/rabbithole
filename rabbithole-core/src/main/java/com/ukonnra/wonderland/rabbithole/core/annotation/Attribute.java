package com.ukonnra.wonderland.rabbithole.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Attribute {
  String readPermission() default "";
  boolean ignore() default false;
  boolean filterable() default true;
}