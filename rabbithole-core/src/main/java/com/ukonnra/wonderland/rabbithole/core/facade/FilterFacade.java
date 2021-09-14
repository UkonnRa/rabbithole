package com.ukonnra.wonderland.rabbithole.core.facade;

import com.ukonnra.wonderland.rabbithole.core.schema.FilterSchema;
import java.util.Map;

public interface FilterFacade {
  Map<String, FilterSchema> filter();
}
