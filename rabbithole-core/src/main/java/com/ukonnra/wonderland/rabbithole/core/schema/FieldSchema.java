package com.ukonnra.wonderland.rabbithole.core.schema;

import com.ukonnra.wonderland.rabbithole.core.ApplicationContext;
import com.ukonnra.wonderland.rabbithole.core.annotation.Attribute;
import com.ukonnra.wonderland.rabbithole.core.annotation.Relationship;
import edu.umd.cs.findbugs.annotations.Nullable;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

public record FieldSchema<T>(String name, boolean isNullable, T type) {
  public static class Factory {
    private final ApplicationContext context;
    private final ProcessingEnvironment processingEnv;
    private final AttributeSchemaType.Factory attributeFactory;
    private final RelationshipSchemaType.Factory relationshipFactory;

    public Factory(ProcessingEnvironment processingEnv, ApplicationContext context) {
      this.context = context;
      this.processingEnv = processingEnv;
      this.attributeFactory = new AttributeSchemaType.Factory(processingEnv);
      this.relationshipFactory = new RelationshipSchemaType.Factory(processingEnv);
    }

    public @Nullable FieldSchema<AttributeSchemaType> createAttribute(
        final VariableElement element) {
      var anno = Optional.ofNullable(element.getAnnotation(Attribute.class));

      if (anno.map(Attribute::ignore).orElse(false)) {
        return null;
      }

      var typeData =
          attributeFactory
              .create(element.asType())
              .orElseThrow(
                  () ->
                      new RuntimeException(
                          String.format("Type[%s] cannot be an attribute", element.asType())));
      var name =
          anno.map(Attribute::name)
              .filter(s -> !s.isEmpty())
              .orElse(element.getSimpleName().toString());
      return new FieldSchema<>(name, this.context.isNullable(element), typeData);
    }

    public Map<String, FieldSchema<AttributeSchemaType>> createAttributes(
        final TypeElement element) {
      return this.processingEnv.getElementUtils().getAllMembers(element).stream()
          .filter(field -> field.getKind().isField())
          .map(
              field -> {
                if (field instanceof VariableElement variable) {
                  var value = this.createAttribute(variable);
                  if (value != null) {
                    return Map.entry(variable.getSimpleName().toString(), value);
                  }
                }
                return null;
              })
          .filter(Objects::nonNull)
          .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public @Nullable FieldSchema<RelationshipSchemaType> createRelationship(
        final VariableElement elem) {
      var anno = Optional.ofNullable(elem.getAnnotation(Relationship.class));

      var typeData =
          relationshipFactory
              .create(elem.asType())
              .orElseThrow(
                  () ->
                      new RuntimeException(
                          String.format("Type[%s] cannot be a relationship", elem.asType())));
      var name =
          anno.map(Relationship::name)
              .filter(s -> !s.isEmpty())
              .orElse(elem.getSimpleName().toString());
      return new FieldSchema<>(name, this.context.isNullable(elem), typeData);
    }
  }
}
