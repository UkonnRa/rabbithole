package com.ukonnra.wonderland.rabbithole.example.core.domains.user.valobjs;

import com.ukonnra.wonderland.rabbithole.core.annotation.ValueObject;
import java.time.Instant;

@ValueObject
public sealed interface Password permits Password.Normal, Password.Hanging {
  @ValueObject(rename = "PasswordNormal")
  record Normal(String value) implements Password {}

  @ValueObject(rename = "PasswordHanging")
  record Hanging(Instant createAt) implements Password {}
}
