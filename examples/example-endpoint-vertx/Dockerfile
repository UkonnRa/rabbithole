FROM openjdk:17-alpine AS build

WORKDIR /app

COPY build/libs/example-endpoint-vertx-fat.jar app.jar

RUN jdeps --ignore-missing-deps -q --print-module-deps app.jar > jdeps.info

RUN jlink --verbose --compress 2 --strip-java-debug-attributes --no-header-files --no-man-pages \
    --output jre --add-modules $(cat jdeps.info)

FROM alpine:latest

ARG USER=rabbithole

ARG GROUP=wonderland

ARG UID=1000

ARG GID=1000

ARG HOME=/home/$USER

RUN addgroup -g $GID -S $GROUP

RUN adduser -h $HOME -g "" -G $GROUP -D $USER

USER $USER

WORKDIR $HOME

COPY --from=build --chown=$USER:$GROUP /app/jre jre

COPY --from=build --chown=$USER:$GROUP /app/app.jar app.jar

ENTRYPOINT jre/bin/java -jar app.jar
