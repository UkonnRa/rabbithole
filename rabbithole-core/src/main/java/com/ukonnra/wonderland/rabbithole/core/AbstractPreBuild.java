package com.ukonnra.wonderland.rabbithole.core;

import com.google.common.reflect.ClassPath;
import com.ukonnra.wonderland.rabbithole.core.annotation.AggregateRoot;
import com.ukonnra.wonderland.rabbithole.core.annotation.Command;
import java.io.IOException;
import java.util.stream.Collectors;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractPreBuild<T extends AbstractPreBuild> {
  private final @NotNull Class<T> clazz;

  protected AbstractPreBuild(final @NotNull Class<T> clazz) {
    this.clazz = clazz;
  }

  public abstract @NotNull Logger logger();

  public void handle() throws IOException {
    var classes =
        ClassPath.from(ClassLoader.getSystemClassLoader()).getAllClasses().stream()
            .filter(c -> c.getPackageName().startsWith(clazz.getPackageName()))
            .map(clazz -> clazz.load())
            .filter(clz -> clz.isAnnotationPresent(AggregateRoot.class))
            .collect(Collectors.toSet());

    for (var clz : classes) {
      var anno = clz.getAnnotation(AggregateRoot.class);
      var command = anno.command();
      logger()
          .info("Command Class: {} - isSealed: {}", command.getSimpleName(), command.isSealed());
      for (var subCommand : command.getPermittedSubclasses()) {
        logger().info("  SubCommand: {}", subCommand.getSimpleName());
        var commandAnno = subCommand.getAnnotation(Command.class);
        logger()
            .info(
                "  - @Command: name - {}, idField - {}", commandAnno.name(), commandAnno.idField());
      }
    }
  }
}
