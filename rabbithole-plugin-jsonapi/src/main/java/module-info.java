/** */
module rabbithole.plugin.jsonapi {
  requires rabbithole.core;
  requires com.github.spotbugs.annotations;
  requires java.compiler;
  requires io.swagger.v3.core;
  requires io.swagger.v3.oas.models;
  requires com.fasterxml.jackson.databind;

  exports com.ukonnra.wonderland.rabbithole.plugin.jsonapi;
  exports com.ukonnra.wonderland.rabbithole.plugin.jsonapi.annotation;
  exports com.ukonnra.wonderland.rabbithole.plugin.jsonapi.schema;
}
