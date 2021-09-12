package com.ukonnra.wonderland.rabbithole.core.facade;

import com.ukonnra.wonderland.rabbithole.core.filter.Filter;

import java.util.Map;

public interface FilterFacade {
  Map<String, Filter> filter();
}
