package com.ukonnra.wonderland.rabbithole.plugin.jsonapi.schema;

import com.ukonnra.wonderland.rabbithole.core.schema.ApplicationSchemaMetadata;
import io.swagger.v3.oas.models.OpenAPI;

public record JsonapiApplicationSchemaMetadata(OpenAPI openAPI)
    implements ApplicationSchemaMetadata {}
