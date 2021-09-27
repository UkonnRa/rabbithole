package com.ukonnra.wonderland.rabbithole.core;

import java.util.List;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

public class ApplicationContext {
  private final boolean defaultAsNonNull;
  private final List<String> nullMarkers;

  public ApplicationContext(
      boolean defaultAsNonNull, final List<? extends TypeMirror> nullMarkers) {
    this.defaultAsNonNull = defaultAsNonNull;
    this.nullMarkers = nullMarkers.stream().map(TypeMirror::toString).toList();
    System.out.println("ApplicationContext.nullMarkers: " + this.nullMarkers);
  }

  public boolean isNullable(final VariableElement element) {
    var markerExists =
        element.getAnnotationMirrors().stream()
            .anyMatch(a -> this.nullMarkers.contains(a.getAnnotationType().toString()));
    return (!defaultAsNonNull && !markerExists) || (defaultAsNonNull && markerExists);
  }
}
