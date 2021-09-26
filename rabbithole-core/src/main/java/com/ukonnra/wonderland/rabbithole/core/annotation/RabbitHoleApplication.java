package com.ukonnra.wonderland.rabbithole.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PACKAGE)
public @interface RabbitHoleApplication {
  String module();

  boolean defaultAsNonNull() default true;

  /**
   * Custom null/nonNull markers. If {@link RabbitHoleApplication#defaultAsNonNull()} is true, it
   * will be considered as @Nullable; otherwise, considered as @NonNull
   *
   * @return null/nonNull markers
   */
  Class<? extends Annotation>[] nullMarkers() default {};
}
