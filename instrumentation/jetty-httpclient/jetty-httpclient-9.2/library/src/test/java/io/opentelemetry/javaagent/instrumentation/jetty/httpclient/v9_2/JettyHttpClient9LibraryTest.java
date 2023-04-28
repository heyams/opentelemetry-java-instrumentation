/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.jetty.httpclient.v9_2;

import io.opentelemetry.instrumentation.jetty.httpclient.v9_2.AbstractJettyClient9Test;
import io.opentelemetry.instrumentation.jetty.httpclient.v9_2.JettyClientTelemetry;
import io.opentelemetry.instrumentation.testing.junit.http.AbstractHttpClientTest;
import java.util.Collections;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.ssl.SslContextFactory;

class JettyHttpClient9LibraryTest extends AbstractJettyClient9Test {

  @Override
  public HttpClient createStandardClient() {
    return JettyClientTelemetry.builder(testing.getOpenTelemetry())
        .setCapturedRequestHeaders(
            Collections.singletonList(AbstractHttpClientTest.TEST_REQUEST_HEADER))
        .setCapturedResponseHeaders(
            Collections.singletonList(AbstractHttpClientTest.TEST_RESPONSE_HEADER))
        .build()
        .getHttpClient();
  }

  @Override
  public HttpClient createHttpsClient(SslContextFactory sslContextFactory) {
    return JettyClientTelemetry.builder(testing.getOpenTelemetry())
        .setSslContextFactory(sslContextFactory)
        .build()
        .getHttpClient();
  }
}
