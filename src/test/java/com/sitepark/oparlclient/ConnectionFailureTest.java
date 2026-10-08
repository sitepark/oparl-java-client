package com.sitepark.oparlclient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.sitepark.oparlclient.core.OparlConnectionException;
import com.sitepark.oparlclient.core.OparlException;
import com.sitepark.oparlclient.v1.objects.OparlBody;
import java.io.IOException;
import java.net.Authenticator;
import java.net.CookieHandler;
import java.net.ProxySelector;
import java.net.ServerSocket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import org.junit.jupiter.api.Test;

class ConnectionFailureTest {

  private final OparlClient client = new OparlClient();

  @Test
  void reportsUnreachableServerAsConnectionException() throws Exception {
    URI uri = URI.create("http://127.0.0.1:" + unusedPort() + "/body/1");

    CompletionException e =
        assertThrows(
            CompletionException.class, () -> this.client.getAsync(uri, OparlBody.class).join());

    OparlConnectionException cause = assertInstanceOf(OparlConnectionException.class, e.getCause());
    assertEquals(uri, cause.getUri());
    assertInstanceOf(IOException.class, cause.getCause());
  }

  @Test
  void synchronousGetThrowsConnectionException() throws Exception {
    URI uri = URI.create("http://127.0.0.1:" + unusedPort() + "/body/1");

    assertThrows(OparlConnectionException.class, () -> this.client.get(uri, OparlBody.class));
  }

  @Test
  void reportsOtherFailuresOfTheHttpClientAsOparlException() {
    URI uri = URI.create("https://oparl.example.org/body/1");
    IllegalStateException failure = new IllegalStateException("client closed");
    OparlClient client = OparlClient.builder().httpClient(new FailingHttpClient(failure)).build();

    OparlException e = assertThrows(OparlException.class, () -> client.get(uri, OparlBody.class));

    assertEquals(uri, e.getUri());
    assertSame(failure, e.getCause());
  }

  /** An http client whose requests fail with the given exception. */
  private static final class FailingHttpClient extends HttpClient {

    private final RuntimeException failure;

    FailingHttpClient(RuntimeException failure) {
      this.failure = failure;
    }

    @Override
    public <T> CompletableFuture<HttpResponse<T>> sendAsync(
        HttpRequest request, HttpResponse.BodyHandler<T> handler) {
      return CompletableFuture.failedFuture(this.failure);
    }

    @Override
    public <T> CompletableFuture<HttpResponse<T>> sendAsync(
        HttpRequest request,
        HttpResponse.BodyHandler<T> handler,
        HttpResponse.PushPromiseHandler<T> pushPromiseHandler) {
      return this.sendAsync(request, handler);
    }

    @Override
    public <T> HttpResponse<T> send(HttpRequest request, HttpResponse.BodyHandler<T> handler) {
      throw new UnsupportedOperationException("the client only sends asynchronously");
    }

    @Override
    public Optional<CookieHandler> cookieHandler() {
      return Optional.empty();
    }

    @Override
    public Optional<Duration> connectTimeout() {
      return Optional.empty();
    }

    @Override
    public Redirect followRedirects() {
      return Redirect.NEVER;
    }

    @Override
    public Optional<ProxySelector> proxy() {
      return Optional.empty();
    }

    @Override
    public SSLContext sslContext() {
      return null;
    }

    @Override
    public SSLParameters sslParameters() {
      return new SSLParameters();
    }

    @Override
    public Optional<Authenticator> authenticator() {
      return Optional.empty();
    }

    @Override
    public Version version() {
      return Version.HTTP_1_1;
    }

    @Override
    public Optional<Executor> executor() {
      return Optional.empty();
    }
  }

  private static int unusedPort() throws IOException {
    try (ServerSocket socket = new ServerSocket(0)) {
      return socket.getLocalPort();
    }
  }
}
