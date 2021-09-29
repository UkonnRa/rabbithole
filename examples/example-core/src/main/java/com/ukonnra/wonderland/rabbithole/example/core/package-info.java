@DefaultAnnotation(NonNull.class)
@RabbitHoleApplication(module = "rabbithole.example.core", nullMarkers = Nullable.class)
@JsonapiApplication(schemaPath = "openapi.template.yaml")
package com.ukonnra.wonderland.rabbithole.example.core;

import com.ukonnra.wonderland.rabbithole.core.annotation.RabbitHoleApplication;
import com.ukonnra.wonderland.rabbithole.plugin.jsonapi.annotation.JsonapiApplication;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
