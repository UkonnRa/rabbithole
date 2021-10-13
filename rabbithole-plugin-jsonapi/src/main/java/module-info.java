/** */
module rabbithole.plugin.jsonapi {
  requires rabbithole.core;
  requires java.compiler;
  requires io.swagger.v3.core;
  requires io.swagger.v3.oas.models;
  requires com.fasterxml.jackson.databind;
  requires static lombok;
  requires com.github.spotbugs.annotations;

  exports com.ukonnra.wonderland.rabbithole.plugin.jsonapi;
  exports com.ukonnra.wonderland.rabbithole.plugin.jsonapi.annotation;
  exports com.ukonnra.wonderland.rabbithole.plugin.jsonapi.model;
  exports com.ukonnra.wonderland.rabbithole.plugin.jsonapi.schema;

  opens com.ukonnra.wonderland.rabbithole.plugin.jsonapi.model to
      com.fasterxml.jackson.databind;
}
