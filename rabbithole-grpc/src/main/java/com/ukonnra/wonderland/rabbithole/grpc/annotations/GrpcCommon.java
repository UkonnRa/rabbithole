package com.ukonnra.wonderland.rabbithole.grpc.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.PACKAGE)
public @interface GrpcCommon {
  String projectName();
}
