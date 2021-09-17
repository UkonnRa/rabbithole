package com.ukonnra.wonderland.rabbithole.core;

import com.google.auto.service.AutoService;
import com.ukonnra.wonderland.rabbithole.core.annotation.RabbitHoleApplication;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

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
            }
          }
        }
      }
    }

    return true;
  }
}
