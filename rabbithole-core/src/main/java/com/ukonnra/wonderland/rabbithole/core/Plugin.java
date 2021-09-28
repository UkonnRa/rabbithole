package com.ukonnra.wonderland.rabbithole.core;

import com.ukonnra.wonderland.rabbithole.core.schema.ApplicationSchema;
import com.ukonnra.wonderland.rabbithole.core.schema.CommandSchemaMetadata;
import java.util.Optional;
import javax.lang.model.element.TypeElement;

public interface Plugin {
  Optional<CommandSchemaMetadata> parseCommandMetadata(final TypeElement element);

  void generate(final ApplicationSchema schema);
}
