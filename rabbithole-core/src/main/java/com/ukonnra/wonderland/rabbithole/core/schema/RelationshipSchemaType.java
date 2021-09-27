package com.ukonnra.wonderland.rabbithole.core.schema;

import com.ukonnra.wonderland.rabbithole.core.annotation.AggregateRoot;
import java.util.Collection;
import java.util.Optional;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

public sealed interface RelationshipSchemaType
    permits RelationshipSchemaType.Array, RelationshipSchemaType.Ref {
  record Ref(String type) implements RelationshipSchemaType {
    public static Ref of(final DeclaredType type) {
      var elem = type.asElement();
      var anno = elem.getAnnotation(AggregateRoot.class);

      if (anno == null) {
        throw new RuntimeException(
            String.format("Relationship[%s] must be annotated with @AggregateRoot.", elem));
      }

      if (elem instanceof TypeElement typeElem) {
        var name = typeElem.getSimpleName().toString();
        if (!anno.rename().equals("")) {
          name = anno.rename();
        }
        return new Ref(name);
      } else {
        throw new RuntimeException(
            String.format(
                "Invalid type. Type[%s] is not a class or interface and cannot be a relationship.",
                elem));
      }
    }
  }

  record Array(Ref ref) implements RelationshipSchemaType {}

  record Factory(ProcessingEnvironment processingEnv) {
    private Optional<Array> createArray(final ArrayType type) {
      return Optional.ofNullable(type.getComponentType())
          .flatMap(
              t -> {
                if (t instanceof DeclaredType decl) {
                  return Optional.of(Ref.of(decl));
                }
                return Optional.empty();
              })
          .map(RelationshipSchemaType.Array::new);
    }

    private Optional<Array> createArray(final DeclaredType type) {
      var target =
          processingEnv.getElementUtils().getTypeElement(Collection.class.getTypeName()).asType();
      if (processingEnv
              .getTypeUtils()
              .isAssignable(type, processingEnv.getTypeUtils().erasure(target))
          && type.getTypeArguments().size() == 1
          && type.getTypeArguments().get(0) instanceof DeclaredType decl) {
        return Optional.of(new Array(Ref.of(decl)));
      }
      return Optional.empty();
    }

    public Optional<? extends RelationshipSchemaType> create(final TypeMirror type) {
      if (type instanceof ArrayType array) {
        return this.createArray(array);
      } else if (type instanceof DeclaredType decl) {
        return this.createArray(decl)
            .map(RelationshipSchemaType.class::cast)
            .or(() -> Optional.of(Ref.of(decl)));
      }
      return Optional.empty();
    }
  }
}
