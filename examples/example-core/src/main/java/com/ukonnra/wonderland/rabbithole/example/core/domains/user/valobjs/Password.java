package com.ukonnra.wonderland.rabbithole.example.core.domains.user.valobjs;

import com.ukonnra.wonderland.rabbithole.core.annotation.ValueObject;
import java.time.Instant;

@ValueObject
public sealed interface Password permits Password.Normal, Password.Hanging {
  record Normal(String value) implements Password {}

  record Hanging(Instant createAt) implements Password {}
}
