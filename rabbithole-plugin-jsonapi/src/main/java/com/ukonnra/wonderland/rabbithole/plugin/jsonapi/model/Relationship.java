package com.ukonnra.wonderland.rabbithole.plugin.jsonapi.model;

import edu.umd.cs.findbugs.annotations.Nullable;

public record Relationship(ResourceLinkage data, @Nullable Links links, @Nullable Object meta) {
  record Links(@Nullable Link self, @Nullable Link related) {}
}
