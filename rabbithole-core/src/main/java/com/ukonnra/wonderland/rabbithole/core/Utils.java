package com.ukonnra.wonderland.rabbithole.core;

import com.ukonnra.wonderland.rabbithole.core.annotation.ValueObject;
import java.util.Optional;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

public interface Utils {
  static Optional<TypeElement> toElement(final TypeMirror type, final Elements util) {
    return Optional.ofNullable(util.getTypeElement(type.toString().replace("/", ".")));
  }

  static String valObjGetName(final TypeElement element) {
    var annoValObj = element.getAnnotation(ValueObject.class);
    if (annoValObj == null) {
      throw new RuntimeException(
          String.format("Type[%s] must be annotated with @ValueObject.", element));
    }
    var name = element.getSimpleName().toString();
    if (!annoValObj.rename().equals("")) {
      name = annoValObj.rename();
    }

    return name;
  }

  static String valObjGetName(final DeclaredType type) {
    var elem = type.asElement();

    if (elem instanceof TypeElement typeElem) {
      return valObjGetName(typeElem);
    } else {
      throw new RuntimeException(
          String.format(
              "Invalid type. Type[%s] is not a class or interface and cannot be a attribute.",
              elem));
    }
  }
}
