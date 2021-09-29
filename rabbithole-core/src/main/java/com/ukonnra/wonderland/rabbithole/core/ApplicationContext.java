package com.ukonnra.wonderland.rabbithole.core;

import java.util.List;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

public class ApplicationContext {
  private final String moduleName;
  private final boolean defaultAsNonNull;
  private final List<String> nullMarkers;

  public ApplicationContext(
      String moduleName, boolean defaultAsNonNull, final List<? extends TypeMirror> nullMarkers) {
    this.moduleName = moduleName;
    this.defaultAsNonNull = defaultAsNonNull;
    this.nullMarkers = nullMarkers.stream().map(TypeMirror::toString).toList();
  }

  public boolean isNullable(final VariableElement element) {
    var markerExists =
        element.getAnnotationMirrors().stream()
            .anyMatch(a -> this.nullMarkers.contains(a.getAnnotationType().toString()));
    return (!defaultAsNonNull && !markerExists) || (defaultAsNonNull && markerExists);
  }

  public String getModuleName() {
    return moduleName;
  }
}
