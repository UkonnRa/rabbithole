package com.ukonnra.wonderland.rabbithole.plugin.jsonapi.model;

import edu.umd.cs.findbugs.annotations.Nullable;
import java.util.Map;

public record JsonapiError(
    @Nullable String id,
    @Nullable Links links,
    @Nullable String status,
    @Nullable String code,
    @Nullable String title,
    @Nullable String detail,
    @Nullable Source source,
    @Nullable Map<String, Object> meta) {
  record Links(@Nullable Link about) {}

  record Source(@Nullable String pointer, @Nullable String parameter) {}
}
