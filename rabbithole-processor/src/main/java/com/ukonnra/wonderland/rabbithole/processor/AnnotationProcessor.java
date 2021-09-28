package com.ukonnra.wonderland.rabbithole.processor;

import com.google.auto.service.AutoService;
import com.ukonnra.wonderland.rabbithole.core.ApplicationContext;
import com.ukonnra.wonderland.rabbithole.core.Plugin;
import com.ukonnra.wonderland.rabbithole.core.annotation.AggregateRoot;
import com.ukonnra.wonderland.rabbithole.core.annotation.Command;
import com.ukonnra.wonderland.rabbithole.core.annotation.RabbitHoleApplication;
import com.ukonnra.wonderland.rabbithole.core.annotation.Relationship;
import com.ukonnra.wonderland.rabbithole.core.annotation.ValueObject;
import com.ukonnra.wonderland.rabbithole.core.schema.AggregateSchema;
import com.ukonnra.wonderland.rabbithole.core.schema.ApplicationSchema;
import com.ukonnra.wonderland.rabbithole.core.schema.AttributeSchemaType;
import com.ukonnra.wonderland.rabbithole.core.schema.CommandSchema;
import com.ukonnra.wonderland.rabbithole.core.schema.FieldSchema;
import com.ukonnra.wonderland.rabbithole.core.schema.RelationshipSchemaType;
import com.ukonnra.wonderland.rabbithole.core.schema.ValueObjectSchema;
import com.ukonnra.wonderland.rabbithole.plugin.graphql.GraphqlPlugin;
import com.ukonnra.wonderland.rabbithole.plugin.jsonapi.JsonapiPlugin;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@SupportedAnnotationTypes("com.ukonnra.wonderland.rabbithole.core.annotation.RabbitHoleApplication")
public class AnnotationProcessor extends AbstractProcessor {
  @SuppressFBWarnings("NP_NONNULL_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR")
  private FieldSchema.Factory fieldFactory;

  @SuppressFBWarnings("NP_NONNULL_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR")
  private ValueObjectSchema.Factory valObjFactory;

  @SuppressFBWarnings("NP_NONNULL_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR")
  private List<Plugin> plugins;

  private void info(final String message) {
    processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, message);
  }

  private void error(final String message) {
    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message);
  }

  private static <A extends Annotation> List<? extends TypeMirror> getTypesInAnnotation(
      final A annotation, final Function<A, Class<?>[]> getter) {
    try {
      getter.apply(annotation);
      return List.of();
    } catch (MirroredTypesException e) {
      return e.getTypeMirrors();
    }
  }

  private static <A extends Annotation> Optional<TypeMirror> getTypeInAnnotation(
      final A annotation, final Function<A, Class<?>> getter) {
    try {
      getter.apply(annotation);
      return Optional.empty();
    } catch (MirroredTypeException e) {
      return Optional.ofNullable(e.getTypeMirror());
    }
  }

  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    this.plugins = List.of(new JsonapiPlugin(processingEnv), new GraphqlPlugin(processingEnv));
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    var elemsWithApp = roundEnv.getElementsAnnotatedWith(RabbitHoleApplication.class);
    if (elemsWithApp.size() > 1) {
      error("@RabbitHoleApplication should be only one");
      return false;
    } else if (elemsWithApp.size() == 0) {
      return true;
    }

    var elemApp = new ArrayList<>(elemsWithApp).get(0);

    try {
      var annoApp = elemApp.getAnnotation(RabbitHoleApplication.class);
      var context =
          new ApplicationContext(
              annoApp.defaultAsNonNull(),
              getTypesInAnnotation(annoApp, RabbitHoleApplication::nullMarkers));
      this.fieldFactory = new FieldSchema.Factory(processingEnv, context);
      this.valObjFactory = new ValueObjectSchema.Factory(this.fieldFactory);

      var aggregates = new ArrayList<AggregateSchema>();
      var valObjs = new ArrayList<ValueObjectSchema>();

      for (var moduleEnclosedElem :
          processingEnv
              .getElementUtils()
              .getModuleElement(annoApp.module())
              .getEnclosedElements()) {
        if (moduleEnclosedElem instanceof PackageElement packageElem) {
          for (var inner : packageElem.getEnclosedElements()) {
            if (inner instanceof TypeElement type) {
              var annoAggr = type.getAnnotation(AggregateRoot.class);
              if (annoAggr != null) {
                var attributes = new HashMap<String, FieldSchema<AttributeSchemaType>>();
                var relationships = new HashMap<String, FieldSchema<RelationshipSchemaType>>();

                for (var field : processingEnv.getElementUtils().getAllMembers(type)) {
                  if (field.getKind().isField()) {
                    if (field instanceof VariableElement variable) {
                      var annoRelat = variable.getAnnotation(Relationship.class);

                      if (annoRelat == null) {
                        if (!annoAggr.idField().contentEquals(variable.getSimpleName())) {
                          var attributeType = this.fieldFactory.createAttribute(variable);
                          if (attributeType == null) {
                            continue;
                          }
                          attributes.put(attributeType.name(), attributeType);
                        }
                      } else {
                        var relatType = this.fieldFactory.createRelationship(variable);
                        if (relatType == null) {
                          continue;
                        }
                        relationships.put(relatType.name(), relatType);
                      }
                    }
                  }
                }

                var commands = new ArrayList<CommandSchema>();
                var commandType =
                    getTypeInAnnotation(annoAggr, AggregateRoot::command)
                        .orElseThrow(
                            () -> new RuntimeException("@AggregateRoot(command) is required"));
                if (commandType instanceof DeclaredType decl
                    && decl.asElement() instanceof TypeElement commandElem) {
                  for (var sub : commandElem.getPermittedSubclasses()) {
                    if (sub instanceof DeclaredType subDecl
                        && subDecl.asElement() instanceof TypeElement subElem) {
                      var annoCommand = subElem.getAnnotation(Command.class);
                      if (annoCommand == null) {
                        throw new RuntimeException(
                            String.format(
                                "Command class must be annotated with @Command,"
                                    + " but Type[%s] does not.",
                                subElem));
                      }
                      var commandAttrs = this.fieldFactory.createAttributes(subElem);
                      var subCommand =
                          new CommandSchema(
                              commandElem.getSimpleName() + subElem.getSimpleName().toString(),
                              annoCommand.name(),
                              commandAttrs,
                              this.plugins.stream()
                                  .flatMap(p -> p.parseCommandMetadata(subElem).stream())
                                  .toList());
                      commands.add(subCommand);
                    }
                  }
                }

                aggregates.add(
                    new AggregateSchema(
                        annoAggr.type(),
                        type.getSimpleName().toString(),
                        attributes,
                        relationships,
                        commands));
              }

              var annoValObj = type.getAnnotation(ValueObject.class);
              if (annoValObj != null) {
                var valObj = this.valObjFactory.create(type);
                valObjs.add(valObj);
              }
            }
          }
        }
      }
      var applicationSchema = new ApplicationSchema(aggregates, valObjs);
      for (var plugin : this.plugins) {
        plugin.generate(applicationSchema);
      }

      return true;
    } catch (RuntimeException e) {
      e.printStackTrace();
      error(e.getMessage());
      return false;
    }
  }
}
