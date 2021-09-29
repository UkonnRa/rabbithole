package com.ukonnra.wonderland.rabbithole.plugin.graphql;

import com.ukonnra.wonderland.rabbithole.core.ApplicationContext;
import com.ukonnra.wonderland.rabbithole.core.Plugin;
import com.ukonnra.wonderland.rabbithole.core.schema.ApplicationSchema;
import com.ukonnra.wonderland.rabbithole.core.schema.ApplicationSchemaMetadata;
import com.ukonnra.wonderland.rabbithole.core.schema.CommandSchemaMetadata;
import edu.umd.cs.findbugs.annotations.Nullable;
import java.util.Optional;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;

public record GraphqlPlugin(ProcessingEnvironment processingEnv) implements Plugin {
  @Override
  public Optional<ApplicationSchemaMetadata> parseApplicationMetadata(
      final PackageElement element) {
    return Optional.empty();
  }

  @Override
  public @Nullable CommandSchemaMetadata parseCommandMetadata(final TypeElement element) {
    return null;
  }

  @Override
  public void generate(final ApplicationContext context, final ApplicationSchema schema) {}
}
