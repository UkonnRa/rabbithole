package com.ukonnra.wonderland.rabbithole.plugin.graphql;

import com.ukonnra.wonderland.rabbithole.core.Plugin;
import com.ukonnra.wonderland.rabbithole.core.schema.ApplicationSchema;
import com.ukonnra.wonderland.rabbithole.core.schema.CommandSchemaMetadata;
import java.util.Optional;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;

public record GraphqlPlugin(ProcessingEnvironment processingEnv) implements Plugin {
  @Override
  public Optional<CommandSchemaMetadata> parseCommandMetadata(TypeElement element) {
    return Optional.empty();
  }

  @Override
  public void generate(ApplicationSchema schema) {}
}
