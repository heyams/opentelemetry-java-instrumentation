/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.rediscala;

import io.opentelemetry.instrumentation.api.incubator.semconv.db.DbClientAttributesGetter;
import io.opentelemetry.semconv.incubating.DbIncubatingAttributes;
import java.util.Locale;
import javax.annotation.Nullable;
import redis.RedisCommand;

final class RediscalaAttributesGetter implements DbClientAttributesGetter<RedisCommand<?, ?>> {

  @Override
  public String getSystem(RedisCommand<?, ?> redisCommand) {
    return DbIncubatingAttributes.DbSystemValues.REDIS;
  }

  @Deprecated
  @Override
  @Nullable
  public String getUser(RedisCommand<?, ?> redisCommand) {
    return null;
  }

  @Deprecated
  @Override
  @Nullable
  public String getName(RedisCommand<?, ?> redisCommand) {
    return null;
  }

  @Nullable
  @Override
  public String getNamespace(RedisCommand<?, ?> redisCommand) {
    return null;
  }

  @Deprecated
  @Override
  @Nullable
  public String getConnectionString(RedisCommand<?, ?> redisCommand) {
    return null;
  }

  @Deprecated
  @Override
  @Nullable
  public String getStatement(RedisCommand<?, ?> redisCommand) {
    return null;
  }

  @Nullable
  @Override
  public String getDbQueryText(RedisCommand<?, ?> redisCommand) {
    return null;
  }

  @Deprecated
  @Override
  public String getOperation(RedisCommand<?, ?> redisCommand) {
    return redisCommand.getClass().getSimpleName().toUpperCase(Locale.ROOT);
  }

  @Override
  public String getOperationName(RedisCommand<?, ?> redisCommand) {
    return redisCommand.getClass().getSimpleName().toUpperCase(Locale.ROOT);
  }
}
