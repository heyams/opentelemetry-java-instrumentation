/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.cassandra.v4_0;

import com.datastax.oss.driver.api.core.session.Session;
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class CassandraRequest {

  public static CassandraRequest create(Session session, String statement) {
    return new AutoValue_CassandraRequest(session, statement);
  }

  public abstract Session getSession();

  /**
   * @deprecated Use {@link #getDbQueryText()} instead.
   */
  @Deprecated
  public abstract String getStatement();

  public abstract String getDbQueryText();
}
