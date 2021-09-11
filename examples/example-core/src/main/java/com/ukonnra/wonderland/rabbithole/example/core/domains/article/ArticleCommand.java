package com.ukonnra.wonderland.rabbithole.example.core.domains.article;

import com.ukonnra.wonderland.rabbithole.core.annotation.Command;
import com.ukonnra.wonderland.rabbithole.core.facade.CommandFacade;
import com.ukonnra.wonderland.rabbithole.jsonapi.annotation.JsonapiCommand;
import org.jetbrains.annotations.Nullable;

public sealed interface ArticleCommand extends CommandFacade
    permits ArticleCommand.Create,
        ArticleCommand.Update,
        ArticleCommand.UpdateAuthor,
        ArticleCommand.Delete {
  @Command(name = "createUser")
  @JsonapiCommand(type = JsonapiCommand.Type.CREATE)
  record Create(int count, @Nullable String authorId) implements ArticleCommand {}

  @Command(name = "updateArticleInfo", idField = "id")
  @JsonapiCommand(type = JsonapiCommand.Type.UPDATE)
  record Update(String id, @Nullable Integer count, @Nullable Article.State state) implements ArticleCommand {}

  @Command(name = "updateAuthor", idField = "id")
  @JsonapiCommand(type = JsonapiCommand.Type.UPDATE)
  record UpdateAuthor(String id, String authorId) implements ArticleCommand {}

  @Command(name = "deleteUser", idField = "id")
  @JsonapiCommand(type = JsonapiCommand.Type.DELETE)
  record Delete(String id) implements ArticleCommand {}
}
