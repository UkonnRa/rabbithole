package com.ukonnra.wonderland.rabbithole.plugin.jsonapi.model;

import edu.umd.cs.findbugs.annotations.Nullable;
import java.util.Map;

public record Resource(
    String type,
    String id,
    @Nullable Map<String, Object> attributes,
    @Nullable Map<String, Relationships> relationships,
    @Nullable Links links,
    @Nullable Map<String, Object> meta) {
  record Links(@Nullable Link self) {}
}
