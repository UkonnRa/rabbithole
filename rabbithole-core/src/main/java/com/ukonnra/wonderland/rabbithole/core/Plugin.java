package com.ukonnra.wonderland.rabbithole.core;

import com.ukonnra.wonderland.rabbithole.core.facade.AggregateRootFacade;
import com.ukonnra.wonderland.rabbithole.core.facade.CommandFacade;
import com.ukonnra.wonderland.rabbithole.core.schema.AggregateSchema;
import com.ukonnra.wonderland.rabbithole.core.schema.AttributeSchemaType;
import com.ukonnra.wonderland.rabbithole.core.schema.CommandSchema;
import com.ukonnra.wonderland.rabbithole.core.schema.FieldSchema;
import com.ukonnra.wonderland.rabbithole.core.schema.FilterSchema;
import com.ukonnra.wonderland.rabbithole.core.schema.RelationshipSchemaType;
import com.ukonnra.wonderland.rabbithole.core.schema.ValueObjectSchema;
import edu.umd.cs.findbugs.annotations.Nullable;
import java.lang.reflect.Field;
import java.util.List;

public interface Plugin {
  @Nullable
  default AggregateSchema parseAggregate(Class<? extends AggregateRootFacade> clazz) {
    return null;
  }

  @Nullable
  default ValueObjectSchema parseValueObject(Class<?> clazz) {
    return null;
  }

  @Nullable
  default ValueObjectSchema.Data.Enum parseValueObjectDataEnum(Class<?> clazz) {
    return null;
  }

  @Nullable
  default ValueObjectSchema.Data.Obj parseValueObjectDataObj(Class<?> clazz) {
    return null;
  }

  @Nullable
  default ValueObjectSchema.Data.OneOf parseValueObjectDataOneOf(Class<?> clazz) {
    return null;
  }

  @Nullable
  default List<Class<?>> getCommandItems(Class<? extends CommandFacade> clazz) {
    return null;
  }

  @Nullable
  default CommandSchema parseCommand(Class<?> clazz) {
    return null;
  }

  @Nullable
  default Boolean isNullable(Field field) {
    return null;
  }

  @Nullable
  default Boolean isAttribute(Field field) {
    return null;
  }

  @Nullable
  default FieldSchema<AttributeSchemaType> parseAttribute(Field field) {
    return null;
  }

  @Nullable
  default AttributeSchemaType.Primary parseAttributeSchemaTypePrimary(Class<?> type) {
    return null;
  }

  @Nullable
  default AttributeSchemaType.Ref parseAttributeSchemaTypeRef(Class<?> type) {
    return null;
  }

  @Nullable
  default AttributeSchemaType.Array parseAttributeSchemaTypeArray(Class<?> type) {
    return null;
  }

  @Nullable
  default AttributeSchemaType.Map parseAttributeSchemaTypeMap(Class<?> type) {
    return null;
  }

  @Nullable
  default Boolean isRelationship(Field field) {
    return null;
  }

  @Nullable
  default FieldSchema<RelationshipSchemaType> parseRelationship(Field field) {
    return null;
  }

  @Nullable
  default RelationshipSchemaType.Ref parseRelationshipSchemaTypeRef(Field field) {
    return null;
  }

  @Nullable
  default RelationshipSchemaType.Array parseRelationshipSchemaTypeArray(Field field) {
    return null;
  }

  @Nullable
  default RelationshipSchemaType.Map parseRelationshipSchemaTypeMap(Field field) {
    return null;
  }

  @Nullable
  default Boolean isFilterable(Field field) {
    return null;
  }

  @Nullable
  default FilterSchema parseFilter(Field field) {
    return null;
  }
}
