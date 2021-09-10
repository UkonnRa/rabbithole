package com.ukonnra.wonderland.rabbithole.core;

import com.google.common.reflect.ClassPath;
import com.ukonnra.wonderland.rabbithole.core.annotation.AggregateRoot;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.stream.Collectors;

public abstract class AbstractPreBuild {
  public abstract Logger logger();

  public void handle() throws IOException {
    var classes =
        ClassPath.from(ClassLoader.getSystemClassLoader()).getAllClasses().stream()
            .filter(
                c ->
                    c.getPackageName()
                        .equals("com.ukonnra.wonderland.rabbithole.examples.vertx.models.user"))
            .map(clazz -> clazz.load())
            .filter(clz -> clz.isAnnotationPresent(AggregateRoot.class))
            .collect(Collectors.toSet());

    for (var clz : classes) {
      logger().info("AggregateRoot annotated class: " + clz);
    }
  }
}
