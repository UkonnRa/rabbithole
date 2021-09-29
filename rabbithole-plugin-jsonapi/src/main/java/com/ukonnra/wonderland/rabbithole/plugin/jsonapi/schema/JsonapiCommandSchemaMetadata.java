package com.ukonnra.wonderland.rabbithole.plugin.jsonapi.schema;

import com.ukonnra.wonderland.rabbithole.core.schema.CommandSchemaMetadata;
import edu.umd.cs.findbugs.annotations.Nullable;

public record JsonapiCommandSchemaMetadata(JsonapiOperationType type, @Nullable String idField)
    implements CommandSchemaMetadata {}
