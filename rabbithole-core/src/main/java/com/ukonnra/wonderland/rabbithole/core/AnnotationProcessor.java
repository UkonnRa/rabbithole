package com.ukonnra.wonderland.rabbithole.core;

import com.google.auto.service.AutoService;
import com.ukonnra.wonderland.rabbithole.core.annotation.RabbitHoleApplication;
import com.ukonnra.wonderland.rabbithole.core.schema.AttributeSchemaType;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.lang.annotation.Annotation;
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
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.StandardLocation;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@SupportedAnnotationTypes("com.ukonnra.wonderland.rabbithole.core.annotation.RabbitHoleApplication")
public class AnnotationProcessor extends AbstractProcessor {
  @SuppressFBWarnings("NP_NONNULL_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR")
  private AttributeSchemaType.Factory attributeSchemaFactory;

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
    this.attributeSchemaFactory = new AttributeSchemaType.Factory(processingEnv);
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    var elemsWithApp = roundEnv.getElementsAnnotatedWith(RabbitHoleApplication.class);
    if (elemsWithApp.size() > 1) {
      error("@RabbitHoleApplication should be only one");
      return false;
    }
    for (var elem : elemsWithApp) {
      var annoApp = elem.getAnnotation(RabbitHoleApplication.class);
      var context =
          new ApplicationContext(
              annoApp.defaultAsNonNull(),
              getTypesInAnnotation(annoApp, RabbitHoleApplication::nullMarkers),
              processingEnv.getElementUtils());
      info("Print content: " + context);

      for (var moduleEnclosedElem :
          processingEnv
              .getElementUtils()
              .getModuleElement(annoApp.module())
              .getEnclosedElements()) {
        if (moduleEnclosedElem instanceof PackageElement packageElem) {
          processingEnv
              .getMessager()
              .printMessage(Diagnostic.Kind.NOTE, "Package: " + packageElem.getQualifiedName());

          for (var inner : packageElem.getEnclosedElements()) {
            if (inner instanceof TypeElement type) {
              processingEnv
                  .getMessager()
                  .printMessage(
                      Diagnostic.Kind.NOTE,
                      "  Class: " + type.getKind() + ", " + type.getQualifiedName());

              for (var field : processingEnv.getElementUtils().getAllMembers(type)) {
                if (field.getKind().isField()) {
                  if (field instanceof VariableElement variable) {
                    processingEnv
                        .getMessager()
                        .printMessage(
                            Diagnostic.Kind.NOTE,
                            "      VariableElement - Type: " + variable.asType());

                    var attributeType = this.attributeSchemaFactory.create(variable.asType());

                    info("AttributeType: " + attributeType);
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
  }

  private static record ApplicationContext(
      boolean defaultAsNonNull, List<TypeElement> nullMarkers) {
    public ApplicationContext(
        boolean defaultAsNonNull,
        final List<? extends TypeMirror> nullMarkers,
        final Elements util) {
      this(
          defaultAsNonNull,
          nullMarkers.stream()
              .map(t -> Utils.toElement(t, util))
              .flatMap(Optional::stream)
              .toList());
    }
  }
}
