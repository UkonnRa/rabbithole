package com.ukonnra.wonderland.rabbithole.core.schema;

import com.ukonnra.wonderland.rabbithole.core.Utils;
import java.time.Instant;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;

public sealed interface AttributeSchemaType
    permits AttributeSchemaType.Array,
        AttributeSchemaType.Map,
        AttributeSchemaType.Primitive,
        AttributeSchemaType.Ref {
  record Primitive(Type type) implements AttributeSchemaType {
    private static final java.util.Map<String, Type> TYPE_MAPPING =
        java.util.Map.ofEntries(
            java.util.Map.entry(Boolean.class.getTypeName(), Type.BOOLEAN),
            java.util.Map.entry(Byte.class.getTypeName(), Type.INTEGER),
            java.util.Map.entry(Short.class.getTypeName(), Type.INTEGER),
            java.util.Map.entry(Integer.class.getTypeName(), Type.INTEGER),
            java.util.Map.entry(Long.class.getTypeName(), Type.INTEGER),
            java.util.Map.entry(Character.class.getTypeName(), Type.STRING),
            java.util.Map.entry(String.class.getTypeName(), Type.STRING),
            java.util.Map.entry(UUID.class.getTypeName(), Type.STRING),
            java.util.Map.entry(Float.class.getTypeName(), Type.FLOAT),
            java.util.Map.entry(Double.class.getTypeName(), Type.FLOAT),
            java.util.Map.entry(Instant.class.getTypeName(), Type.TIMESTAMP));

    public static Primitive of(final PrimitiveType type) {
      var ty =
          switch (type.toString()) {
            case "boolean" -> Type.BOOLEAN;
            case "byte", "short", "int", "long" -> Type.INTEGER;
            case "char" -> Type.STRING;
            case "float", "double" -> Type.FLOAT;
            default -> throw new RuntimeException(type + " is not a primitive plural");
          };

      return new Primitive(ty);
    }

    public static Optional<Primitive> of(final DeclaredType type) {
      return Optional.ofNullable(TYPE_MAPPING.get(type.toString())).map(Primitive::new);
    }

    public enum Type {
      BOOLEAN,
      INTEGER,
      STRING,
      FLOAT,
      TIMESTAMP
    }
  }

  record Ref(String type) implements AttributeSchemaType {}

  record Array(AttributeSchemaType item) implements AttributeSchemaType {}

  record Map(AttributeSchemaType values) implements AttributeSchemaType {}

  record Factory(ProcessingEnvironment processingEnv) {
    private Optional<Array> createArray(final ArrayType type) {
      return Optional.ofNullable(type.getComponentType()).flatMap(this::create).map(Array::new);
    }

    private Optional<Array> createArray(final DeclaredType type) {
      if (Utils.isAssignable(processingEnv, type, Collection.class)
          && type.getTypeArguments().size() == 1) {
        return this.create(type.getTypeArguments().get(0)).map(Array::new);
      }
      return Optional.empty();
    }

    private Optional<Map> createMap(final DeclaredType type) {
      if (Utils.isAssignable(processingEnv, type, java.util.Map.class)
          && type.getTypeArguments().size() == 2) {
        return this.create(type.getTypeArguments().get(1)).map(Map::new);
      }
      return Optional.empty();
    }

    public Optional<? extends AttributeSchemaType> create(final TypeMirror type) {
      if (type instanceof PrimitiveType primitive) {
        return Optional.of(Primitive.of(primitive));
      } else if (type instanceof ArrayType array) {
        return this.createArray(array);
      } else if (type instanceof DeclaredType decl) {
        return Primitive.of(decl)
            .map(AttributeSchemaType.class::cast)
            .or(() -> this.createArray(decl))
            .or(() -> this.createMap(decl))
            .or(() -> Optional.of(new Ref(Utils.valObjGetName(decl))));
      }
      return Optional.empty();
    }
  }
}
