/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.influxdb.v2_4;

import io.opentelemetry.instrumentation.api.incubator.semconv.db.DbClientAttributesGetter;
import javax.annotation.Nullable;

final class InfluxDbAttributesGetter implements DbClientAttributesGetter<InfluxDbRequest> {

  @Deprecated
  @Nullable
  @Override
  public String getStatement(InfluxDbRequest request) {
    return request.getSqlStatementInfo().getFullStatement();
  }

  @Nullable
  @Override
  public String getDbQueryText(InfluxDbRequest request) {
    return request.getSqlStatementInfo().getFullStatement();
  }

  @Deprecated
  @Nullable
  @Override
  public String getOperation(InfluxDbRequest request) {
    if (request.getOperation() != null) {
      return request.getOperation();
    }
    return request.getSqlStatementInfo().getOperation();
  }

  @Nullable
  @Override
  public String getDbOperationName(InfluxDbRequest request) {
    if (request.getOperation() != null) {
      return request.getOperation();
    }
    return request.getSqlStatementInfo().getOperation();
  }

  @Override
  public String getSystem(InfluxDbRequest request) {
    return "influxdb";
  }

  @Deprecated
  @Nullable
  @Override
  public String getUser(InfluxDbRequest request) {
    return null;
  }

  @Deprecated
  @Nullable
  @Override
  public String getName(InfluxDbRequest request) {
    String dbName = request.getDbName();
    return "".equals(dbName) ? null : dbName;
  }

  @Nullable
  @Override
  public String getNamespace(InfluxDbRequest request) {
    String dbName = request.getDbName();
    return "".equals(dbName) ? null : dbName;
  }

  @Deprecated
  @Nullable
  @Override
  public String getConnectionString(InfluxDbRequest influxDbRequest) {
    return null;
  }
}
