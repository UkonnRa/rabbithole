package com.ukonnra.wonderland.rabbithole.example.core.domains.article.valobjs;

import com.ukonnra.wonderland.rabbithole.core.annotation.ValueObject;
import edu.umd.cs.findbugs.annotations.Nullable;

@ValueObject
public class Tag {
  private final @Nullable String id;
  private final int number;

  public Tag(@Nullable String id, int number) {
    this.id = id;
    this.number = number;
  }
}
