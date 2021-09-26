package com.ukonnra.wonderland.rabbithole.core;

import java.util.Optional;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

interface Utils {
  static Optional<TypeElement> toElement(final TypeMirror type, final Elements util) {
    return Optional.ofNullable(util.getTypeElement(type.toString().replace("/", ".")));
  }
}
