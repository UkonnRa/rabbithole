package com.ukonnra.wonderland.rabbithole.examples.vertx.models.user;

import com.ukonnra.wonderland.rabbithole.core.annotation.AggregateRoot;

@AggregateRoot(type = "users")
public record User(String id) {

}
