package com.ukonnra.wonderland.rabbithole.example.core.domains.article;

import com.ukonnra.wonderland.rabbithole.core.annotation.AggregateRoot;
import com.ukonnra.wonderland.rabbithole.core.annotation.Relationship;
import com.ukonnra.wonderland.rabbithole.core.facade.AggregateRootFacade;
import com.ukonnra.wonderland.rabbithole.example.core.domains.user.User;
import edu.umd.cs.findbugs.annotations.Nullable;
import java.util.List;
import java.util.Map;

@AggregateRoot(type = "articles", command = ArticleCommand.class)
public record Article(
    String id,
    int count,
    State state,
    List<String> tags,
    Map<String, Integer> ranks,
    @Nullable @Relationship(type = User.class, mappingType = Relationship.MappingType.TO_ONE)
        User author)
    implements AggregateRootFacade {
  enum State {
    IN,
    OUT;
  }
}
