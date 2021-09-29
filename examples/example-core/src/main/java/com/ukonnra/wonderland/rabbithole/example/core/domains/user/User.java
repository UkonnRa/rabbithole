package com.ukonnra.wonderland.rabbithole.example.core.domains.user;

import com.ukonnra.wonderland.rabbithole.core.annotation.AggregateRoot;
import com.ukonnra.wonderland.rabbithole.core.annotation.Attribute;
import com.ukonnra.wonderland.rabbithole.core.annotation.Relationship;
import com.ukonnra.wonderland.rabbithole.core.facade.AggregateRootFacade;
import com.ukonnra.wonderland.rabbithole.example.core.domains.article.Article;
import com.ukonnra.wonderland.rabbithole.example.core.domains.user.valobjs.Password;
import edu.umd.cs.findbugs.annotations.Nullable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@AggregateRoot(plural = "users", command = UserCommand.class)
public record User(
    String id,
    @Nullable String name,
    Instant createAt,
    int[] numbers,
    @Attribute(name = "password") Password passwordComponent,
    @Relationship List<Article> articles,
    @Nullable @Relationship User manager,
    @Attribute(ignore = true) boolean ignored)
    implements AggregateRootFacade {
  public User(String name, String password) {
    this(
        UUID.randomUUID().toString(),
        name,
        Instant.now(),
        new int[] {},
        new Password.Normal(password),
        List.of(),
        null,
        false);
  }
}
