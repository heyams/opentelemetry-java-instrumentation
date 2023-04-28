/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.jetty.httpclient.v9_2;

import io.opentelemetry.instrumentation.jetty.httpclient.v9_2.AbstractJettyClient9Test;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.ssl.SslContextFactory;

class JettyHttpClient9AgentTest extends AbstractJettyClient9Test {

  @Override
  public HttpClient createStandardClient() {
    return new HttpClient();
  }

  @Override
  public HttpClient createHttpsClient(SslContextFactory sslContextFactory) {
    return new HttpClient(sslContextFactory);
  }
}
