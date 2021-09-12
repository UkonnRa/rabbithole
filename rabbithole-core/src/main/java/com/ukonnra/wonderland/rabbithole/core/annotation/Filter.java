package com.ukonnra.wonderland.rabbithole.core.annotation;

import com.ukonnra.wonderland.rabbithole.core.facade.FilterFacade;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Filter {
  Class<? extends FilterFacade> value();
}
