package com.ukonnra.wonderland.rabbithole.example.core.domains.user;

import com.ukonnra.wonderland.rabbithole.core.annotation.Command;
import com.ukonnra.wonderland.rabbithole.core.facade.CommandFacade;
import com.ukonnra.wonderland.rabbithole.jsonapi.annotation.JsonapiCommand;
import java.util.List;

public sealed interface UserCommand extends CommandFacade
    permits UserCommand.Create, UserCommand.UpdateInfo, UserCommand.Delete {
  @Command(name = "createUser")
  @JsonapiCommand(type = JsonapiCommand.Type.CREATE)
  record Create(String name, String password, List<String> articleIds, String userId)
      implements UserCommand {}

  @Command(name = "updateUserInfo", idField = "id")
  @JsonapiCommand(type = JsonapiCommand.Type.UPDATE)
  record UpdateInfo(String id, String name) implements UserCommand {}

  @Command(name = "deleteUser", idField = "id")
  @JsonapiCommand(type = JsonapiCommand.Type.DELETE)
  record Delete(String id) implements UserCommand {}
}
