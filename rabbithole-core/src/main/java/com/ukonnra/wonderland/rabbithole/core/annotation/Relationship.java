package com.ukonnra.wonderland.rabbithole.core.annotation;

import com.ukonnra.wonderland.rabbithole.core.facade.RelationshipFacade;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Relationship {
  Class<? extends RelationshipFacade> type();
  String name() default "";
}
