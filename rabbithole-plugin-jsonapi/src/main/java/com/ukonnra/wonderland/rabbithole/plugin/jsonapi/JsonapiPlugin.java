package com.ukonnra.wonderland.rabbithole.plugin.jsonapi;

import com.ukonnra.wonderland.rabbithole.core.ApplicationContext;
import com.ukonnra.wonderland.rabbithole.core.Plugin;
import com.ukonnra.wonderland.rabbithole.core.annotation.Command;
import com.ukonnra.wonderland.rabbithole.core.annotation.RabbitHoleApplication;
import com.ukonnra.wonderland.rabbithole.core.schema.AggregateSchema;
import com.ukonnra.wonderland.rabbithole.core.schema.ApplicationSchema;
import com.ukonnra.wonderland.rabbithole.core.schema.ApplicationSchemaMetadata;
import com.ukonnra.wonderland.rabbithole.core.schema.AttributeSchemaType;
import com.ukonnra.wonderland.rabbithole.core.schema.CommandSchemaMetadata;
import com.ukonnra.wonderland.rabbithole.core.schema.RelationshipSchemaType;
import com.ukonnra.wonderland.rabbithole.core.schema.ValueObjectSchema;
import com.ukonnra.wonderland.rabbithole.plugin.jsonapi.annotation.JsonapiApplication;
import com.ukonnra.wonderland.rabbithole.plugin.jsonapi.annotation.JsonapiCommand;
import com.ukonnra.wonderland.rabbithole.plugin.jsonapi.schema.JsonapiApplicationSchemaMetadata;
import com.ukonnra.wonderland.rabbithole.plugin.jsonapi.schema.JsonapiCommandSchemaMetadata;
import com.ukonnra.wonderland.rabbithole.plugin.jsonapi.schema.JsonapiOperationType;
import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.headers.Header;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.BooleanSchema;
import io.swagger.v3.oas.models.media.ComposedSchema;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.DateTimeSchema;
import io.swagger.v3.oas.models.media.Discriminator;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.NumberSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.StandardLocation;

public record JsonapiPlugin(ProcessingEnvironment processingEnv) implements Plugin {
  private static final String JSONAPI_MEDIA_TYPE = "application/vnd.api+json";

  private static final Parameter PARAMETER_INCLUDE =
      new Parameter()
          .name("include")
          .in("query")
          .schema(new ArraySchema().items(new StringSchema()));

  private static final Parameter PARAMETER_FIELDS =
      new Parameter()
          .name("fields")
          .in("query")
          .schema(
              new ObjectSchema().additionalProperties(new ArraySchema().items(new StringSchema())));

  private static final Parameter PARAMETER_SORT =
      new Parameter().name("sort").in("query").schema(new ArraySchema().items(new StringSchema()));

  private static final Parameter PARAMETER_PAGINATION =
      new Parameter().name("page").in("query").schema(new ObjectSchema());

  private static final Parameter PARAMETER_FILTER =
      new Parameter().name("filter").in("query").schema(new ObjectSchema());

  private static final Map<String, Schema> COMMON_COMPONENT_SCHEMAS =
      Map.ofEntries(
          Map.entry(
              "Jsonapi",
              new ObjectSchema()
                  .addRequiredItem("version")
                  .addProperties("version", new StringSchema())
                  .addProperties("meta", new ObjectSchema())),
          Map.entry(
              "Link",
              new ComposedSchema()
                  .addOneOfItem(new StringSchema())
                  .addOneOfItem(
                      new ObjectSchema()
                          .addRequiredItem("href")
                          .addProperties("href", new StringSchema())
                          .addProperties("meta", new ObjectSchema()))),
          Map.entry(
              "ResourceIdentifier",
              new ObjectSchema()
                  .required(List.of("id", "type"))
                  .addProperties("id", new StringSchema())
                  .addProperties("type", new StringSchema())),
          Map.entry(
              "ResourceLinkage",
              new ComposedSchema()
                  .addOneOfItem(new Schema<>().$ref("ResourceIdentifier"))
                  .addOneOfItem(
                      new ArraySchema().items(new Schema<>().$ref("ResourceIdentifier")))),
          Map.entry(
              "JsonapiError",
              new ObjectSchema()
                  .properties(
                      Map.of(
                          "id", new StringSchema(),
                          "links",
                              new ObjectSchema()
                                  .addProperties("about", new Schema<>().$ref("Link")),
                          "status", new StringSchema(),
                          "code", new StringSchema(),
                          "title", new StringSchema(),
                          "detail", new StringSchema(),
                          "source",
                              new ObjectSchema()
                                  .properties(
                                      Map.of(
                                          "pointer",
                                          new StringSchema(),
                                          "parameter",
                                          new StringSchema())),
                          "meta", new ObjectSchema()))),
          Map.entry(
              "Relationship",
              new ObjectSchema()
                  .addRequiredItem("data")
                  .properties(
                      Map.of(
                          "data", new Schema<>().$ref("ResourceLinkage"),
                          "links",
                              new ObjectSchema()
                                  .properties(
                                      Map.of(
                                          "self", new Schema<>().$ref("Link"),
                                          "related", new Schema<>().$ref("Link"))),
                          "meta", new ObjectSchema()))),
          Map.entry(
              "Resource",
              new ObjectSchema()
                  .required(List.of("type", "id"))
                  .properties(
                      Map.of(
                          "type", new StringSchema(),
                          "id", new StringSchema(),
                          "attributes", new ObjectSchema(),
                          "relationships",
                              new ObjectSchema()
                                  .additionalProperties(
                                      new ComposedSchema()
                                          .addOneOfItem(new Schema<>().$ref("Relationship"))
                                          .addOneOfItem(
                                              new ArraySchema()
                                                  .items(new Schema<>().$ref("Relationship")))),
                          "links",
                              new ObjectSchema().addProperties("self", new Schema<>().$ref("Link")),
                          "meta", new ObjectSchema()))),
          Map.entry(
              "PaginationMeta",
              new ObjectSchema().addProperties("rangeTruncated", new IntegerSchema())));

  private static RequestBody createRequest(final String type) {
    return new RequestBody()
        .content(
            new Content()
                .addMediaType(
                    JSONAPI_MEDIA_TYPE, new MediaType().schema(new Schema<>().$ref(type))));
  }

  private static ApiResponse createResponse(final String type) {
    return new ApiResponse()
        .content(
            new Content()
                .addMediaType(
                    JSONAPI_MEDIA_TYPE, new MediaType().schema(new Schema<>().$ref(type))));
  }

  private static Schema<?> toSchema(final AttributeSchemaType type) {
    if (type instanceof AttributeSchemaType.Primitive prim) {
      return switch (prim.type()) {
        case BOOLEAN -> new BooleanSchema();
        case INTEGER -> new IntegerSchema();
        case STRING -> new StringSchema();
        case FLOAT -> new NumberSchema();
        case TIMESTAMP -> new DateTimeSchema();
      };
    } else if (type instanceof AttributeSchemaType.Array arr) {
      return new ArraySchema().items(toSchema(arr.item()));
    } else if (type instanceof AttributeSchemaType.Map map) {
      return new ObjectSchema().additionalProperties(toSchema(map.values()));
    } else if (type instanceof AttributeSchemaType.Ref ref) {
      return new Schema<>().$ref(ref.type());
    } else {
      throw new IllegalArgumentException(String.format("Invalid attribute schema: %s", type));
    }
  }

  private static Schema<?> toSchema(final RelationshipSchemaType type) {
    if (type instanceof RelationshipSchemaType.Array arr) {
      return new ArraySchema().items(toSchema(arr.ref()));
    } else if (type instanceof RelationshipSchemaType.Ref ref) {
      return new Schema<>().$ref(String.format("%sResourceIdentifier", ref.type()));
    } else {
      throw new IllegalArgumentException(String.format("Invalid relationship schema: %s", type));
    }
  }

  private static Schema<?> toSchema(final ValueObjectSchema.Data schema) {
    if (schema instanceof ValueObjectSchema.Data.Enum value) {
      var result = new StringSchema();
      for (var item : value.values()) {
        result.addEnumItem(item);
      }
      return result;
    } else if (schema instanceof ValueObjectSchema.Data.Obj value) {
      var result = new ObjectSchema();
      for (var attr : value.attributes().values()) {
        result.addProperties(attr.name(), toSchema(attr.type()));
        if (!attr.isNullable()) {
          result.addRequiredItem(attr.name());
        }
      }
      return result;
    } else if (schema instanceof ValueObjectSchema.Data.OneOf oneOf) {
      var result = new ComposedSchema();
      for (var item : oneOf.members().entrySet()) {
        result
            .addOneOfItem(
                new ObjectSchema()
                    .properties(
                        Map.of(
                            "type", new StringSchema().addEnumItem(item.getKey()),
                            "data", toSchema(item.getValue()))))
            .discriminator(new Discriminator().propertyName("type"))
            .required(List.of("type", "data"));
      }
      return result;
    } else {
      throw new IllegalArgumentException(String.format("Invalid valueObject schema: %s", schema));
    }
  }

  private static String capitalize(final Enum<?> item) {
    return item.name().substring(0, 1).toUpperCase() + item.name().substring(1).toLowerCase();
  }

  @Override
  public Optional<ApplicationSchemaMetadata> parseApplicationMetadata(PackageElement element) {
    return Optional.ofNullable(element.getAnnotation(RabbitHoleApplication.class))
        .flatMap(
            anno ->
                Optional.ofNullable(element.getAnnotation(JsonapiApplication.class))
                    .map(a -> Map.entry(anno, a)))
        .flatMap(
            e -> {
              try {
                var path =
                    Path.of(
                            processingEnv
                                .getFiler()
                                .getResource(StandardLocation.SOURCE_PATH, "", "module-info.java")
                                .toUri())
                        .getParent()
                        .getParent()
                        .resolve("resources")
                        .resolve(e.getValue().schemaPath());
                var openapi = Yaml.mapper().readValue(Files.readString(path), OpenAPI.class);
                return Optional.of(new JsonapiApplicationSchemaMetadata(openapi));
              } catch (IOException ex) {
                ex.printStackTrace();
                processingEnv
                    .getMessager()
                    .printMessage(
                        Diagnostic.Kind.ERROR,
                        String.format(
                            "Cannot find File[%s] in Module[%s]",
                            e.getValue().schemaPath(), e.getKey().module()));
                return Optional.empty();
              }
            });
  }

  @Override
  public CommandSchemaMetadata parseCommandMetadata(TypeElement element) {
    var annoCommand = element.getAnnotation(Command.class);
    if (annoCommand == null) {
      throw new IllegalArgumentException("Command class must be annotated with @Command");
    }

    var annoJsonapiCommand = element.getAnnotation(JsonapiCommand.class);
    if (annoJsonapiCommand == null) {
      throw new IllegalArgumentException(
          "JSONAPI Command class must be annotated with @JsonapiCommand");
    }

    if (this.processingEnv.getElementUtils().getAllMembers(element).stream()
        .anyMatch(f -> f.getSimpleName().toString().equals(annoCommand.idField()))) {
      return new JsonapiCommandSchemaMetadata(annoJsonapiCommand.type(), annoCommand.idField());
    } else {
      return new JsonapiCommandSchemaMetadata(annoJsonapiCommand.type(), null);
    }
  }

  @Override
  public void generate(final ApplicationContext context, final ApplicationSchema schema)
      throws IOException {
    OpenAPI openapi = new OpenAPI();
    for (var meta : schema.metadata()) {
      if (meta instanceof JsonapiApplicationSchemaMetadata jsonapiMeta) {
        openapi = jsonapiMeta.openAPI();
        break;
      }
    }

    openapi.paths(this.createPaths(schema));
    openapi.components(this.createComponents(schema));

    try (var writer =
        processingEnv
            .getFiler()
            .createResource(
                StandardLocation.CLASS_OUTPUT,
                "",
                String.format("resources/%s.openapi.yaml", context.getModuleName()))
            .openWriter()) {
      writer.write(Yaml.pretty(openapi));
    }
  }

  private Paths createPaths(final ApplicationSchema schema) {
    var paths = new Paths();
    for (var aggregate : schema.aggregates()) {
      for (var e : createPathItems(aggregate).entrySet()) {
        paths.addPathItem(e.getKey(), e.getValue());
      }
    }
    return paths;
  }

  private Map<String, PathItem> createPathItems(final AggregateSchema schema) {
    var paramId = new Parameter().name("id").in("path").required(true).schema(new StringSchema());

    return Map.of(
        String.format("/%s", schema.plural()),
        new PathItem()
            .get(
                new Operation()
                    .parameters(
                        List.of(
                            PARAMETER_INCLUDE,
                            PARAMETER_FIELDS,
                            PARAMETER_SORT,
                            PARAMETER_PAGINATION,
                            PARAMETER_FILTER))
                    .responses(
                        new ApiResponses()
                            .addApiResponse(
                                "200",
                                createResponse(String.format("%sTopLevelDataArray", schema.type()))
                                    .description("OK"))))
            .post(
                new Operation()
                    .requestBody(createRequest(String.format("%sCommandCreate", schema.type())))
                    .responses(
                        new ApiResponses()
                            .addApiResponse(
                                "201",
                                createResponse(String.format("%sTopLevelDataSingle", schema.type()))
                                    .addHeaderObject(
                                        "Location",
                                        new Header().required(true).schema(new StringSchema()))
                                    .description("Created")))),
        String.format("/%s/{id}", schema.plural()),
        new PathItem()
            .get(
                new Operation()
                    .parameters(List.of(PARAMETER_INCLUDE, PARAMETER_FIELDS, paramId))
                    .responses(
                        new ApiResponses()
                            .addApiResponse(
                                "200",
                                createResponse(String.format("%sTopLevelDataSingle", schema.type()))
                                    .description("OK"))))
            .patch(
                new Operation()
                    .addParametersItem(paramId)
                    .requestBody(createRequest(String.format("%sCommandUpdate", schema.type())))
                    .responses(
                        new ApiResponses()
                            .addApiResponse("204", new ApiResponse().description("No Content"))))
            .delete(
                new Operation()
                    .addParametersItem(paramId)
                    .responses(
                        new ApiResponses()
                            .addApiResponse("204", new ApiResponse().description("No Content")))));
  }

  private Components createComponents(final ApplicationSchema schema) {
    var components = new Components();

    for (var e : COMMON_COMPONENT_SCHEMAS.entrySet()) {
      components.addSchemas(e.getKey(), e.getValue());
    }

    for (var a : schema.aggregates()) {
      var gen = new AggregateGen(a);
      components.addSchemas(String.format("%sAttributes", a.type()), gen.attributes);
      components.addSchemas(String.format("%sRelationships", a.type()), gen.relationships);
      components.addSchemas(String.format("%sResource", a.type()), gen.resource);
      components.addSchemas(
          String.format("%sResourceIdentifier", a.type()), gen.resourceIdentifier);
      components.addSchemas(String.format("%sTopLevelDataArray", a.type()), gen.topLevelDataArray);
      components.addSchemas(
          String.format("%sTopLevelDataSingle", a.type()), gen.topLevelDataSingle);

      for (var e : gen.commands.entrySet()) {
        components.addSchemas(
            String.format("%sCommand%s", a.type(), capitalize(e.getKey())), e.getValue());
      }
    }

    for (var v : schema.valueObjects()) {
      components.addSchemas(v.type(), toSchema(v.data()));
    }

    return components;
  }

  private static class AggregateGen {
    private final Schema<?> attributes;
    private final Schema<?> relationships;
    private final Schema<?> resource;
    private final Schema<?> resourceIdentifier;
    private final Schema<?> topLevelDataArray;
    private final Schema<?> topLevelDataSingle;
    private final Map<JsonapiOperationType, Schema> commands;

    public AggregateGen(final AggregateSchema schema) {
      this.attributes = new ObjectSchema().additionalProperties(false);
      for (var value : schema.attributes().values()) {
        this.attributes.addProperties(value.name(), toSchema(value.type()));
        if (!value.isNullable()) {
          this.attributes.addRequiredItem(value.name());
        }
      }

      this.relationships = new ObjectSchema().additionalProperties(false);
      for (var value : schema.relationships().values()) {
        this.relationships.addProperties(value.name(), toSchema(value.type()));
        if (!value.isNullable()) {
          this.relationships.addRequiredItem(value.name());
        }
      }

      this.resource =
          new ObjectSchema()
              .required(List.of("type", "id"))
              .additionalProperties(false)
              .properties(
                  Map.of(
                      "type", new StringSchema().addEnumItem(schema.plural()),
                      "id", new StringSchema(),
                      "attributes",
                          new Schema<>().$ref(String.format("%sAttributes", schema.type())),
                      "relationships",
                          new Schema<>().$ref(String.format("%sRelationships", schema.type())),
                      "links",
                          new ObjectSchema().addProperties("self", new Schema<>().$ref("Link"))));

      this.resourceIdentifier =
          new ObjectSchema()
              .additionalProperties(false)
              .addProperties("id", new StringSchema())
              .addProperties("type", new StringSchema().addEnumItem(schema.plural()));

      this.topLevelDataArray =
          new ObjectSchema()
              .addRequiredItem("data")
              .additionalProperties(false)
              .properties(
                  Map.of(
                      "jsonapi", new Schema<>().$ref("Jsonapi"),
                      "links",
                          new ObjectSchema()
                              .properties(
                                  Map.of(
                                      "self", new Schema<>().$ref("Link"),
                                      "related", new Schema<>().$ref("Link"),
                                      "first", new Schema<>().$ref("Link"),
                                      "last", new Schema<>().$ref("Link"),
                                      "prev", new Schema<>().$ref("Link"),
                                      "next", new Schema<>().$ref("Link"))),
                      "included", new ArraySchema().items(new Schema<>().$ref("Resource")),
                      "data",
                          new ArraySchema()
                              .items(
                                  new Schema<>().$ref(String.format("%sResource", schema.type()))),
                      "meta",
                          new ObjectSchema()
                              .addProperties("page", new Schema<>().$ref("PaginationMeta"))));

      this.topLevelDataSingle =
          new ObjectSchema()
              .addRequiredItem("data")
              .additionalProperties(false)
              .properties(
                  Map.of(
                      "jsonapi", new Schema<>().$ref("Jsonapi"),
                      "links",
                          new ObjectSchema()
                              .properties(
                                  Map.of(
                                      "self", new Schema<>().$ref("Link"),
                                      "related", new Schema<>().$ref("Link"))),
                      "included", new ArraySchema().items(new Schema<>().$ref("Resource")),
                      "data", new Schema<>().$ref(String.format("%sResource", schema.type()))));

      var commands = new HashMap<JsonapiOperationType, List<Schema>>();

      for (var command : schema.commands()) {
        var opt =
            command.metadata().stream()
                .filter(c -> c instanceof JsonapiCommandSchemaMetadata)
                .findFirst();
        if (opt.isPresent()) {
          var meta = (JsonapiCommandSchemaMetadata) opt.get();
          commands.compute(
              meta.type(),
              (t, l) -> {
                if (l == null) {
                  l = new ArrayList<>();
                }

                var attributes =
                    command.attributes().values().stream()
                        .filter(attr -> !attr.name().equals(meta.idField()))
                        .toList();

                if (attributes.isEmpty()) {
                  l.add(
                      new ObjectSchema()
                          .required(List.of("type"))
                          .additionalProperties(false)
                          .properties(
                              Map.of("type", new StringSchema().addEnumItem(command.type()))));
                  return l;
                }

                var result = new ObjectSchema();
                for (var attr : attributes) {
                  result.addProperties(attr.name(), toSchema(attr.type()));
                  if (!attr.isNullable()) {
                    result.addRequiredItem(attr.name());
                  }
                }
                l.add(
                    new ObjectSchema()
                        .required(List.of("type", "data"))
                        .additionalProperties(false)
                        .properties(
                            Map.of(
                                "type",
                                new StringSchema().addEnumItem(command.type()),
                                "data",
                                result)));
                return l;
              });
        }
      }

      this.commands =
          commands.entrySet().stream()
              .filter(e -> !e.getValue().isEmpty())
              .map(
                  e -> {
                    Schema<?> attributes;

                    if (e.getValue().size() == 1) {
                      Object data = e.getValue().get(0).getProperties().get("data");
                      if (data != null) {
                        attributes = (Schema<?>) data;
                      } else {
                        return Map.entry(
                            e.getKey(),
                            new ObjectSchema()
                                .additionalProperties(false)
                                .properties(Map.of("jsonapi", new Schema<>().$ref("Jsonapi"))));
                      }
                    } else {
                      attributes =
                          new ComposedSchema()
                              .oneOf(e.getValue())
                              .discriminator(new Discriminator().propertyName("type"));
                    }

                    return Map.entry(
                        e.getKey(),
                        new ObjectSchema()
                            .addRequiredItem("data")
                            .additionalProperties(false)
                            .properties(
                                Map.of(
                                    "jsonapi",
                                    new Schema<>().$ref("Jsonapi"),
                                    "data",
                                    new ObjectSchema()
                                        .addRequiredItem("attributes")
                                        .additionalProperties(false)
                                        .addProperties("attributes", attributes))));
                  })
              .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
  }
}
