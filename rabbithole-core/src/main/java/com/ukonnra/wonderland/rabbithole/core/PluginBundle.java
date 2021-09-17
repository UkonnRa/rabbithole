package com.ukonnra.wonderland.rabbithole.core;

import com.ukonnra.wonderland.rabbithole.core.annotation.AggregateRoot;
import com.ukonnra.wonderland.rabbithole.core.annotation.Attribute;
import com.ukonnra.wonderland.rabbithole.core.annotation.Filter;
import com.ukonnra.wonderland.rabbithole.core.annotation.Relationship;
import com.ukonnra.wonderland.rabbithole.core.facade.AggregateRootFacade;
import com.ukonnra.wonderland.rabbithole.core.facade.CommandFacade;
import com.ukonnra.wonderland.rabbithole.core.schema.AggregateSchema;
import com.ukonnra.wonderland.rabbithole.core.schema.AttributeSchemaType;
import com.ukonnra.wonderland.rabbithole.core.schema.CommandSchema;
import com.ukonnra.wonderland.rabbithole.core.schema.FieldSchema;
import com.ukonnra.wonderland.rabbithole.core.schema.FilterSchema;
import com.ukonnra.wonderland.rabbithole.core.schema.RelationshipSchemaType;
import com.ukonnra.wonderland.rabbithole.core.schema.ValueObjectSchema;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public record PluginBundle(List<Plugin> plugins) implements Plugin {
  private static final Logger LOGGER = LogManager.getLogger(PluginBundle.class);

  @Nullable
  @Override
  public RelationshipSchemaType.Ref parseRelationshipSchemaTypeRef(Field field) {
    return null;
  }

  @Nullable
  @Override
  public RelationshipSchemaType.Array parseRelationshipSchemaTypeArray(Field field) {
    return null;
  }

  @Nullable
  @Override
  public RelationshipSchemaType.Map parseRelationshipSchemaTypeMap(Field field) {
    return null;
  }

  private <R> Optional<R> doParse(Function<Plugin, R> function) {
    for (var p : plugins) {
      var item = function.apply(p);
      if (item != null) {
        return Optional.of(item);
      }
    }
    return Optional.empty();
  }

  private String getTypeAsString(Class<?> clazz) {
    return clazz.getSimpleName().replace("$", "");
  }

  public @NonNull List<AggregateSchema> parseAggregates(
      List<Class<? extends AggregateRootFacade>> classes) {
    return classes.stream()
        .filter(c -> c.isAnnotationPresent(AggregateRoot.class))
        .map(this::parseAggregate)
        .toList();
  }

  @Override
  public @NonNull AggregateSchema parseAggregate(Class<? extends AggregateRootFacade> clazz) {
    return doParse(p -> p.parseAggregate(clazz))
        .orElseGet(
            () -> {
              LOGGER.info("=== AggregateRoot: {} ===", clazz.getName());

              var annoAggregate = clazz.getAnnotation(AggregateRoot.class);

              var attributes =
                  Arrays.stream(clazz.getDeclaredFields())
                      .filter(this::isAttribute)
                      .map(this::parseAttribute)
                      .collect(Collectors.toMap(FieldSchema::name, a -> a));
              var relationships =
                  Arrays.stream(clazz.getDeclaredFields())
                      .filter(this::isRelationship)
                      .map(this::parseRelationship)
                      .collect(Collectors.toMap(FieldSchema::name, a -> a));

              var commands =
                  getCommandItems(annoAggregate.command()).stream()
                      .map(this::parseCommand)
                      .toList();

              var filters =
                  Arrays.stream(
                          (clazz.isAnnotationPresent(Filter.class)
                                  ? clazz.getAnnotation(Filter.class).value()
                                  : clazz)
                              .getDeclaredFields())
                      .filter(this::isFilterable)
                      .map(this::parseFilter)
                      .collect(Collectors.toMap(FilterSchema::name, a -> a));

              return new AggregateSchema(
                  getTypeAsString(clazz),
                  annoAggregate.type(),
                  attributes,
                  relationships,
                  filters,
                  commands);
            });
  }

  @Override
  public @NonNull ValueObjectSchema parseValueObject(Class<?> clazz) {
    return doParse(p -> p.parseValueObject(clazz))
        .orElseGet(
            () -> {
              ValueObjectSchema.Data data = parseValueObjectDataEnum(clazz);
              if (data == null) {
                data = parseValueObjectDataObj(clazz);
              }

              if (data == null) {
                data = parseValueObjectDataOneOf(clazz);
              }

              if (data == null) {
                throw new IllegalArgumentException(
                    String.format("Class[%s] cannot be parsed as a value object", clazz.getName()));
              }

              return new ValueObjectSchema(getTypeAsString(clazz), data);
            });
  }

  @Nullable
  @Override
  public ValueObjectSchema.Data.Enum parseValueObjectDataEnum(Class<?> clazz) {
    return null;
  }

  @Nullable
  @Override
  public ValueObjectSchema.Data.Obj parseValueObjectDataObj(Class<?> clazz) {
    return null;
  }

  @Nullable
  @Override
  public ValueObjectSchema.Data.OneOf parseValueObjectDataOneOf(Class<?> clazz) {
    return null;
  }

  @NonNull
  @Override
  public List<Class<?>> getCommandItems(Class<? extends CommandFacade> clazz) {
    return doParse(p -> p.getCommandItems(clazz))
        .orElseGet(
            () -> {
              if (clazz.isSealed()) {
                return Arrays.stream(clazz.getPermittedSubclasses()).toList();
              }

              throw new IllegalArgumentException(
                  String.format("Class[%s] is not a valid command", clazz.getName()));
            });
  }

  @Override
  public @Nullable CommandSchema parseCommand(Class<?> clazz) {
    return null;
  }

  @Override
  @NonNull
  public Boolean isNullable(Field field) {
    return doParse(p -> p.isNullable(field))
        .orElseGet(
            () -> {
              if (field.isAnnotationPresent(Attribute.class)) {
                return field.getAnnotation(Attribute.class).nullable();
              } else if (field.isAnnotationPresent(Relationship.class)) {
                return field.getAnnotation(Relationship.class).nullable();
              }
              return false;
            });
  }

  @Override
  @NonNull
  public Boolean isAttribute(Field field) {
    return doParse(p -> p.isAttribute(field))
        .orElseGet(
            () -> {
              if (field.isAnnotationPresent(Attribute.class)) {
                return !field.getAnnotation(Attribute.class).ignore();
              } else {
                return !field.isAnnotationPresent(Relationship.class);
              }
            });
  }

  @NonNull
  @Override
  public FieldSchema<AttributeSchemaType> parseAttribute(Field field) {
    return doParse(p -> p.parseAttribute(field))
        .orElseGet(
            () -> {
              String name;
              if (field.isAnnotationPresent(Attribute.class)) {
                var anno = field.getAnnotation(Attribute.class);
                name = anno.name().equals("") ? field.getName() : anno.name();
              } else {
                name = field.getName();
              }

              LOGGER.info("  == Attribute: {} ==", field.getName());
              var genericType = field.getType();
              LOGGER.info("     type: {}", genericType);
              var genericTypeParams = genericType.getTypeParameters();
              if (genericTypeParams.length > 0) {
                LOGGER.info("      generic types: {}", Arrays.stream(genericTypeParams).toList());
              }
              LOGGER.info("     name: {}", name);

              return new FieldSchema<>(
                  name, isNullable(field), parseAttributeSchemaType(genericType));
            });
  }

  private static final Map<AttributeSchemaType.Primary.Type, List<Class<?>>> PRIMITIVE_MAPPER =
      Map.of(
          AttributeSchemaType.Primary.Type.STRING, List.of(String.class, UUID.class),
          AttributeSchemaType.Primary.Type.INTEGER, List.of(Integer.class, int.class),
          AttributeSchemaType.Primary.Type.FLOAT,
              List.of(Float.class, Double.class, float.class, double.class),
          AttributeSchemaType.Primary.Type.BOOLEAN, List.of(Boolean.class, boolean.class),
          AttributeSchemaType.Primary.Type.TIMESTAMP,
              List.of(
                  Date.class,
                  LocalDateTime.class,
                  Instant.class,
                  OffsetDateTime.class,
                  ZonedDateTime.class));

  @NonNull
  private AttributeSchemaType parseAttributeSchemaType(final Class<?> genericType) {
    AttributeSchemaType type = parseAttributeSchemaTypePrimary(genericType);

    if (type == null) {
      type = parseAttributeSchemaTypeMap(genericType);
    }

    if (type == null) {
      type = parseAttributeSchemaTypeArray(genericType);
    }

    if (type == null) {
      type = parseAttributeSchemaTypeRef(genericType);
    }

    return type;
  }

  @Override
  @Nullable
  public AttributeSchemaType.Primary parseAttributeSchemaTypePrimary(Class<?> type) {
    return doParse(p -> p.parseAttributeSchemaTypePrimary(type))
        .orElseGet(
            () -> {
              for (var entry : PRIMITIVE_MAPPER.entrySet()) {
                if (entry.getValue().stream().anyMatch(c -> c.isAssignableFrom(type))) {
                  return new AttributeSchemaType.Primary(entry.getKey());
                }
              }

              return null;
            });
  }

  @Override
  @NonNull
  public AttributeSchemaType.Ref parseAttributeSchemaTypeRef(Class<?> type) {
    return doParse(p -> p.parseAttributeSchemaTypeRef(type))
        .orElseGet(() -> new AttributeSchemaType.Ref(getTypeAsString(type)));
  }

  @Override
  @Nullable
  public AttributeSchemaType.Array parseAttributeSchemaTypeArray(Class<?> type) {
    return doParse(p -> p.parseAttributeSchemaTypeArray(type)).orElseGet(() -> null);
  }

  @Override
  @Nullable
  public AttributeSchemaType.Map parseAttributeSchemaTypeMap(Class<?> type) {
    return doParse(p -> p.parseAttributeSchemaTypeMap(type))
        .orElseGet(
            () -> {
              //      var typeParams = type.getTypeParameters();
              //      if (Map.class.isAssignableFrom(type) && typeParams.length == 2) {
              //        var schemaType =
              // parseAttributeSchemaType(typeParams[1].getGenericDeclaration());
              //        return new AttributeSchemaType.Map(schemaType);
              //      }

              return null;
            });
  }

  @Override
  @NonNull
  public Boolean isRelationship(Field field) {
    return doParse(p -> p.isRelationship(field))
        .orElseGet(() -> field.isAnnotationPresent(Relationship.class));
  }

  @NonNull
  @Override
  public FieldSchema<RelationshipSchemaType> parseRelationship(Field field) {
    return doParse(p -> p.parseRelationship(field))
        .orElseGet(
            () -> {
              String name;

              if (field.isAnnotationPresent(Relationship.class)) {
                var anno = field.getAnnotation(Relationship.class);
                name = anno.name().equals("") ? field.getName() : anno.name();
              } else {
                name = field.getName();
              }
              LOGGER.info("  == Relationship: {} ==", field.getName());
              LOGGER.info(
                  "     type: {} - type.name: {}",
                  field.getGenericType(),
                  field.getGenericType().getTypeName());
              LOGGER.info("     name: {}", name);

              RelationshipSchemaType type = parseRelationshipSchemaTypeArray(field);

              if (type == null) {
                type = parseRelationshipSchemaTypeMap(field);
              }

              if (type == null) {
                type = parseRelationshipSchemaTypeRef(field);
              }

              return new FieldSchema<>(name, isNullable(field), type);
            });
  }

  @Override
  @NonNull
  public Boolean isFilterable(Field field) {
    return doParse(p -> p.isFilterable(field))
        .orElseGet(
            () -> {
              if (!this.isAttribute(field)) {
                return false;
              }

              if (!field.isAnnotationPresent(Attribute.class)) {
                return true;
              }

              var annoAttribute = field.getAnnotation(Attribute.class);
              return annoAttribute.filterable();
            });
  }

  @NonNull
  @Override
  public FilterSchema parseFilter(Field field) {
    return doParse(p -> p.parseFilter(field))
        .orElseGet(
            () -> {
              String name;
              if (field.isAnnotationPresent(Attribute.class)) {
                var anno = field.getAnnotation(Attribute.class);
                name = anno.name().equals("") ? field.getName() : anno.name();
              } else {
                name = field.getName();
              }

              LOGGER.info("  == Filter: {} ==", field.getName());
              var genericType = field.getGenericType();
              LOGGER.info("     type: {}", field.getGenericType());
              if (genericType instanceof ParameterizedType) {
                LOGGER.info(
                    "      generic types: {}",
                    Arrays.stream(((ParameterizedType) genericType).getActualTypeArguments())
                        .toList());
              }
              LOGGER.info("     name: {}", name);

              return new FilterSchema(name, FilterSchema.Data.FILTER_SCHEMA_OBJECT);
            });
  }
}
