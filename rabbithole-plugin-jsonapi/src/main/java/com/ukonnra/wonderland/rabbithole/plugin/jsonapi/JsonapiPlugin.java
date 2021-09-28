package com.ukonnra.wonderland.rabbithole.plugin.jsonapi;

import com.ukonnra.wonderland.rabbithole.core.Plugin;
import com.ukonnra.wonderland.rabbithole.core.schema.ApplicationSchema;
import com.ukonnra.wonderland.rabbithole.core.schema.CommandSchemaMetadata;
import com.ukonnra.wonderland.rabbithole.plugin.jsonapi.annotation.JsonapiCommand;
import com.ukonnra.wonderland.rabbithole.plugin.jsonapi.schema.JsonapiCommandSchemaMetadata;
import java.util.Optional;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

public record JsonapiPlugin(ProcessingEnvironment processingEnv) implements Plugin {

  @Override
  public Optional<CommandSchemaMetadata> parseCommandMetadata(TypeElement element) {
    return Optional.ofNullable(element.getAnnotation(JsonapiCommand.class))
        .map(anno -> new JsonapiCommandSchemaMetadata(anno.type()));
  }

  @Override
  public void generate(ApplicationSchema schema) {
    processingEnv
        .getMessager()
        .printMessage(Diagnostic.Kind.NOTE, "App schema in Jsonapi: " + schema);
  }
}
