package com.ukonnra.wonderland.rabbithole.plugin.jsonapi.schema;

import com.ukonnra.wonderland.rabbithole.core.schema.CommandSchemaMetadata;

public record JsonapiCommandSchemaMetadata(JsonapiOperationType type)
    implements CommandSchemaMetadata {}
