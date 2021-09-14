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

@AggregateRoot(type = "users", command = UserCommand.class)
public record User(
    String id,
    String name,
    Instant createAt,
    @Attribute(name = "password") Password passwordComponent,
    @Relationship(type = Article.class, mappingType = Relationship.MappingType.TO_MANY)
        List<Article> articles,
    @Nullable @Relationship(type = User.class, mappingType = Relationship.MappingType.TO_ONE)
        User manager,
    @Attribute(ignore = true) boolean ignored)
    implements AggregateRootFacade {
  public User(String name, String password) {
    this(
        UUID.randomUUID().toString(),
        name,
        Instant.now(),
        new Password.Normal(password),
        List.of(),
        null,
        false);
  }
}
