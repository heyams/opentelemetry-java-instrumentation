/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.redisson;

import static io.opentelemetry.semconv.incubating.DbIncubatingAttributes.DbSystemValues.REDIS;

import io.opentelemetry.instrumentation.api.incubator.semconv.db.DbClientAttributesGetter;
import javax.annotation.Nullable;

final class RedissonDbAttributesGetter implements DbClientAttributesGetter<RedissonRequest> {

  @Override
  public String getDbSystem(RedissonRequest request) {
    return REDIS;
  }

  @Deprecated
  @Nullable
  @Override
  public String getUser(RedissonRequest request) {
    return null;
  }

  @Nullable
  @Override
  public String getDbNamespace(RedissonRequest request) {
    return null;
  }

  @Deprecated
  @Override
  public String getConnectionString(RedissonRequest request) {
    return null;
  }

  @Override
  public String getDbQueryText(RedissonRequest request) {
    return request.getStatement();
  }

  @Nullable
  @Override
  public String getDbOperationName(RedissonRequest request) {
    return request.getOperation();
  }
}
