package com.ukonnra.wonderland.rabbithole.example.core.domains.user;

import com.ukonnra.wonderland.rabbithole.core.annotation.Command;
import com.ukonnra.wonderland.rabbithole.plugin.jsonapi.annotation.JsonapiCommand;
import com.ukonnra.wonderland.rabbithole.plugin.jsonapi.schema.JsonapiOperationType;
import java.util.List;

public sealed interface UserCommand
    permits UserCommand.Create, UserCommand.UpdateInfo, UserCommand.Delete {
  @Command(name = "createUser")
  @JsonapiCommand(type = JsonapiOperationType.CREATE)
  record Create(String name, String password, List<String> articleIds, String userId)
      implements UserCommand {}

  @Command(name = "updateUserInfo", idField = "id")
  @JsonapiCommand(type = JsonapiOperationType.UPDATE)
  record UpdateInfo(String id, String name) implements UserCommand {}

  @Command(name = "deleteUser", idField = "id")
  @JsonapiCommand(type = JsonapiOperationType.DELETE)
  record Delete(String id) implements UserCommand {}
}
