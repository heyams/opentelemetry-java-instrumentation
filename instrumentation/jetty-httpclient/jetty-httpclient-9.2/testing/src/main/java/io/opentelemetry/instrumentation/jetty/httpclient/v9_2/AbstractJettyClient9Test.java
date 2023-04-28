/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.jetty.httpclient.v9_2;

import static org.assertj.core.api.Assertions.assertThat;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.instrumentation.testing.junit.InstrumentationExtension;
import io.opentelemetry.instrumentation.testing.junit.http.AbstractHttpClientTest;
import io.opentelemetry.instrumentation.testing.junit.http.HttpClientInstrumentationExtension;
import io.opentelemetry.instrumentation.testing.junit.http.HttpClientResult;
import io.opentelemetry.semconv.trace.attributes.SemanticAttributes;
import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.HttpRequest;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import spock.lang.Unroll;

public abstract class AbstractJettyClient9Test
    extends AbstractHttpClientTest<HttpRequest> {

  @RegisterExtension
  protected static final InstrumentationExtension testing =
      HttpClientInstrumentationExtension.forAgent();

  private static HttpClient standardHttpClient;
  private static HttpClient httpsClient;

  @BeforeAll
  protected void setupSpec() throws Exception {
    // start the main Jetty HttpClient
    standardHttpClient = createStandardClient();
    standardHttpClient.start();

    // start a https client
    SslContextFactory sslContextFactory = new SslContextFactory();
    httpsClient = createHttpsClient(sslContextFactory);
    httpsClient.setFollowRedirects(false);
    httpsClient.start();
  }

  @AfterAll
  protected void cleanUpSpec() throws Exception {
    standardHttpClient.stop();
    httpsClient.stop();
  }

  @BeforeEach
  void setup() {
    testing.clearData();
  }

  @Override
  public HttpRequest buildRequest(String method, URI uri, Map<String, String> headers) {
    HttpClient httpClient = "https".equals(uri.getScheme()) ? httpsClient : standardHttpClient;
    Request request = httpClient.newRequest(uri);
    request. agent("Jetty");

    HttpMethod httpMethod = HttpMethod.valueOf(method);
    request.method(httpMethod);
    request.timeout(CONNECTION_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);

    return (HttpRequest) request;
  }

  @Override
  public int sendRequest(HttpRequest request, String method, URI uri, Map<String, String> headers)
          throws ExecutionException, InterruptedException, TimeoutException {
    headers.forEach(request::header);
    ContentResponse response = request.send();
    return response.getStatus();
  }

  @Override
  public void sendRequestWithCallback(HttpRequest request, String method, URI uri, Map<String, String> headers, HttpClientResult httpClientResult) {
    JettyClientListener listener = new JettyClientListener();
    request.onRequestFailure(listener);
    request.onResponseFailure(listener);
    headers.forEach(request::header);

    request.send(
        result -> {
          if (listener.failure != null) {
            httpClientResult.complete(listener.failure);
          }
          httpClientResult.complete(result.getResponse().getStatus());
        });
  }

  @Override
  protected String userAgent() {
    return "Jetty";
  }

  @Override
  protected boolean testRedirects() {
    return false;
  }

  @Override
  protected Set<AttributeKey<?>> httpAttributes(URI uri) {
    Set<AttributeKey<?>> attributeKeys = super.httpAttributes(uri);
    attributeKeys.add(SemanticAttributes.HTTP_SCHEME);
    attributeKeys.add(SemanticAttributes.HTTP_TARGET);
    return attributeKeys;
  }

  protected abstract HttpClient createStandardClient();

  protected abstract HttpClient createHttpsClient(SslContextFactory sslContextFactory);

  private static class JettyClientListener
      implements Request.FailureListener, Response.FailureListener {
    private volatile Throwable failure;

    @Override
    public void onFailure(Request request, Throwable throwable) {
      failure = throwable;
    }

    @Override
    public void onFailure(Response response, Throwable throwable) {
      failure = throwable;
    }
  }

  @Unroll
  @Test
  void testContentOfMethodRequestUrl() throws Exception {
    String method = "GET";
    URI uri = resolveAddress("/success").toURL().toURI();

    testing.runWithSpan(
        "someTrace",
        () -> {
          Request request = buildRequest(method, uri, null);
          ContentResponse response = request.send();
          assertThat(response.getStatus()).isEqualTo(200);
          assertThat(response.getContentAsString()).isEqualTo("Hello.");
        });

    testing.waitAndAssertTraces(
        trace -> trace.hasSpansSatisfyingExactly(
            span -> span.hasName("someTrace").hasKind(SpanKind.INTERNAL).hasNoParent(),
            span -> assertClientSpan(span, uri, method, 200).hasParent(trace.getSpan(0)),
            span -> assertServerSpan(span).hasParent(trace.getSpan(1))));
  }
}
