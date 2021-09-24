package com.ukonnra.wonderland.rabbithole.core;

import com.google.auto.service.AutoService;
import com.ukonnra.wonderland.rabbithole.core.annotation.RabbitHoleApplication;
import java.io.IOException;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.tools.Diagnostic;
import javax.tools.StandardLocation;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class AnnotationProcessor extends AbstractProcessor {
  @Override
  public Set<String> getSupportedAnnotationTypes() {
    return Set.of(RabbitHoleApplication.class.getName());
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    var elemsWithApp = roundEnv.getElementsAnnotatedWith(RabbitHoleApplication.class);
    if (elemsWithApp.size() > 1) {
      processingEnv
          .getMessager()
          .printMessage(Diagnostic.Kind.ERROR, "@RabbitHoleApplication should be only one");
      return false;
    }
    for (var elem : elemsWithApp) {
      var annoApp = elem.getAnnotation(RabbitHoleApplication.class);
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
                    var varType = variable.asType();

                    if (varType instanceof DeclaredType decl) {
                      processingEnv
                          .getMessager()
                          .printMessage(
                              Diagnostic.Kind.NOTE,
                              "        DeclaredType - Generics: " + decl.getTypeArguments());
                    } else if (varType instanceof ArrayType arrType) {
                      processingEnv
                          .getMessager()
                          .printMessage(
                              Diagnostic.Kind.NOTE,
                              "        ArrayType - Type: " + arrType.getComponentType());
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
  }
}
