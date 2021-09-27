package com.ukonnra.wonderland.rabbithole.core;

import com.google.auto.service.AutoService;
import com.ukonnra.wonderland.rabbithole.core.annotation.AggregateRoot;
import com.ukonnra.wonderland.rabbithole.core.annotation.RabbitHoleApplication;
import com.ukonnra.wonderland.rabbithole.core.annotation.Relationship;
import com.ukonnra.wonderland.rabbithole.core.schema.FieldSchema;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.StandardLocation;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@SupportedAnnotationTypes("com.ukonnra.wonderland.rabbithole.core.annotation.RabbitHoleApplication")
public class AnnotationProcessor extends AbstractProcessor {
  @SuppressFBWarnings("NP_NONNULL_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR")
  private FieldSchema.Factory fieldFactory;

  protected void info(final String message) {
    processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, message);
  }

  protected void error(final String message) {
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
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    var elemsWithApp = roundEnv.getElementsAnnotatedWith(RabbitHoleApplication.class);
    if (elemsWithApp.size() > 1) {
      error("@RabbitHoleApplication should be only one");
      return false;
    }

    try {
      for (var elem : elemsWithApp) {
        var annoApp = elem.getAnnotation(RabbitHoleApplication.class);
        var context =
            new ApplicationContext(
                annoApp.defaultAsNonNull(),
                getTypesInAnnotation(annoApp, RabbitHoleApplication::nullMarkers));
        this.fieldFactory = new FieldSchema.Factory(processingEnv, context);

        for (var moduleEnclosedElem :
            processingEnv
                .getElementUtils()
                .getModuleElement(annoApp.module())
                .getEnclosedElements()) {
          if (moduleEnclosedElem instanceof PackageElement packageElem) {
            info("Package: " + packageElem.getQualifiedName());

            for (var inner : packageElem.getEnclosedElements()) {
              if (inner instanceof TypeElement type) {
                info("  Class: " + type.getKind() + ", " + type.getQualifiedName());
                var annoAggr = type.getAnnotation(AggregateRoot.class);

                if (annoAggr == null) {
                  continue;
                }

                for (var field : processingEnv.getElementUtils().getAllMembers(type)) {
                  if (field.getKind().isField()) {
                    if (field instanceof VariableElement variable) {
                      var annoRelat = variable.getAnnotation(Relationship.class);

                      if (annoRelat == null) {
                        if (!annoAggr.idField().contentEquals(variable.getSimpleName())) {
                          var attributeType = this.fieldFactory.createAttribute(variable);
                          info("AttributeType: " + attributeType);
                        }
                      } else {
                        var relatType = this.fieldFactory.createRelationship(variable);
                        info("RelationshipType: " + relatType);
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }

      try (var writer =
          processingEnv
              .getFiler()
              .createResource(StandardLocation.CLASS_OUTPUT, "", "resources/hello.info")
              .openWriter()) {
        writer.write("World");
      } catch (IOException ignored) {
      }

      return true;
    } catch (RuntimeException e) {
      error(e.getMessage());
      return false;
    }
  }
}
