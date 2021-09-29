package com.ukonnra.wonderland.rabbithole.core;

import com.ukonnra.wonderland.rabbithole.core.schema.ApplicationSchema;
import com.ukonnra.wonderland.rabbithole.core.schema.ApplicationSchemaMetadata;
import com.ukonnra.wonderland.rabbithole.core.schema.CommandSchemaMetadata;
import java.io.IOException;
import java.util.Optional;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;

public interface Plugin {
  Optional<ApplicationSchemaMetadata> parseApplicationMetadata(final PackageElement element);

  CommandSchemaMetadata parseCommandMetadata(final TypeElement element);

  void generate(final ApplicationContext context, final ApplicationSchema schema)
      throws IOException;
}
