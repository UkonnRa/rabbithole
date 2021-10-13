package com.ukonnra.wonderland.rabbithole.example.endpoint.vertx;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.openapi.RouterBuilder;
import io.vertx.ext.web.validation.RequestParameters;
import io.vertx.ext.web.validation.ValidationHandler;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MainVerticle extends AbstractVerticle {
  private static final Logger LOGGER = LogManager.getLogger(MainVerticle.class);

  @Override
  public void start() throws IOException {
    LOGGER.info("Start example vertx");

    LOGGER.info("currentDir: {}", vertx.fileSystem().readDirBlocking("."));

    RouterBuilder.create(
            vertx,
            "../example-core/build/generated/sources/annotationProcessor/java/main/resources/rabbithole.example.core.openapi.yaml")
        .map(
            rb -> {
              rb.operation("getUser")
                  .handler(
                      ctx -> {
                        RequestParameters params = ctx.get(ValidationHandler.REQUEST_CONTEXT_KEY);
                        LOGGER.info("getUser QueryParams: {}", params);
                        var body = params.body();
                        var jsonBody = body.getJsonObject();
                        LOGGER.info("getUser body: {}", jsonBody);
                      });

              rb.operation("updateUser")
                  .handler(
                      ctx -> {
                        RequestParameters params = ctx.get(ValidationHandler.REQUEST_CONTEXT_KEY);
                        LOGGER.info("updateUser QueryParams: {}", params.queryParametersNames());
                        var body = params.body();
                        var jsonBody = body.getJsonObject();
                        LOGGER.info("updateUser body: {}", jsonBody);
                        ctx.response()
                            .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
                            .send(jsonBody.toString());
                      });

              return rb.createRouter();
            })
        .flatMap(
            router -> {
              router.route().handler(BodyHandler.create());
              return vertx
                  .createHttpServer(new HttpServerOptions().setPort(8080))
                  .requestHandler(router)
                  .listen();
            })
        .onFailure(err -> LOGGER.error("Load openapi failed: {}", err.getMessage(), err));
  }
}
