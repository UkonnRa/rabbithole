package com.ukonnra.wonderland.rabbithole.plugin.jsonapi.model;

import edu.umd.cs.findbugs.annotations.Nullable;
import java.util.Map;

public record Jsonapi(String version, @Nullable Map<String, Object> meta) {}
