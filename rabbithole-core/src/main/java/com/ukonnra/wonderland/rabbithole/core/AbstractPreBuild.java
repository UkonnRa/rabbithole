package com.ukonnra.wonderland.rabbithole.core;

import com.ukonnra.wonderland.rabbithole.core.annotation.AggregateRoot;
import com.ukonnra.wonderland.rabbithole.core.facade.AggregateRootFacade;
import java.io.IOException;
import java.util.List;
import org.apache.logging.log4j.Logger;

public abstract class AbstractPreBuild {
  private final List<Class<? extends AggregateRootFacade>> aggregateRoots;
  private final PluginBundle pluginBundle;

  protected AbstractPreBuild(
      List<Class<? extends AggregateRootFacade>> aggregateRoots, PluginBundle pluginBundle) {
    this.aggregateRoots = aggregateRoots;
    this.pluginBundle = pluginBundle;
  }

  public abstract Logger logger();

  public void handle() throws IOException {
    aggregateRoots.stream()
        .filter(c -> c.isAnnotationPresent(AggregateRoot.class))
        .map(this.pluginBundle::parseAggregate)
        .toList();
  }
}
