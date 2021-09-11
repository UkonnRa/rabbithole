package com.ukonnra.wonderland.rabbithole.example.core.domains.user;

import com.ukonnra.wonderland.rabbithole.core.annotation.AggregateRoot;
import com.ukonnra.wonderland.rabbithole.core.annotation.Attribute;
import com.ukonnra.wonderland.rabbithole.core.annotation.Relationship;
import com.ukonnra.wonderland.rabbithole.core.facade.AggregateRootFacade;
import com.ukonnra.wonderland.rabbithole.example.core.domains.article.Article;
import com.ukonnra.wonderland.rabbithole.example.core.domains.user.valobjs.Password;
import java.time.Instant;
import java.util.List;
import org.jetbrains.annotations.Nullable;

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
    @Attribute(ignore = true) String ignored)
    implements AggregateRootFacade {}
