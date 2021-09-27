package com.ukonnra.wonderland.rabbithole.core.schema;

import com.ukonnra.wonderland.rabbithole.core.Utils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;

public record ValueObjectSchema(String type, Data data) {
  public sealed interface Data {
    record Enum(List<String> values) implements Data {
      public static Optional<Enum> of(TypeElement element) {
        if (element.getKind() != ElementKind.ENUM) {
          return Optional.empty();
        }

        var values =
            element.getEnclosedElements().stream()
                .filter(e -> e.getKind() == ElementKind.ENUM_CONSTANT)
                .map(Object::toString)
                .toList();
        if (values.isEmpty()) {
          return Optional.empty();
        } else {
          return Optional.of(new Enum(values));
        }
      }
    }

    record Obj(Map<String, FieldSchema<AttributeSchemaType>> attributes) implements Data {}

    record OneOf(Map<String, Obj> members) implements Data {}
  }

  public record Factory(FieldSchema.Factory fieldFactory) {
    private Optional<Data.OneOf> createOneOf(final TypeElement element) {
      var members = new HashMap<String, Data.Obj>();
      for (var sub : element.getPermittedSubclasses()) {
        if (sub instanceof DeclaredType decl && decl.asElement() instanceof TypeElement subElem) {
          members.put(Utils.valObjGetName(subElem), this.createObj(subElem));
        }
      }

      if (members.isEmpty()) {
        return Optional.empty();
      }

      return Optional.of(new Data.OneOf(members));
    }

    private Data.Obj createObj(final TypeElement element) {
      var attributes = fieldFactory.createAttributes(element);
      if (attributes.isEmpty()) {
        throw new RuntimeException(
            String.format(
                "ValueObject.Obj must contain at least one attribute. But Type[%s] does not",
                element));
      }
      return new Data.Obj(attributes);
    }

    public ValueObjectSchema create(final TypeElement element) {
      var data =
          Data.Enum.of(element)
              .map(Data.class::cast)
              .or(() -> this.createOneOf(element))
              .orElseGet(() -> this.createObj(element));
      return new ValueObjectSchema(Utils.valObjGetName(element), data);
    }
  }
}
