package com.sitepark.oparlclient;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/** Minimal HTTP server for tests, serving fixed responses per path. */
final class MockOparlServer implements AutoCloseable {

  private final HttpServer server;

  private final AtomicReference<Map<String, List<String>>> lastRequestHeaders =
      new AtomicReference<>();

  private final Map<String, AtomicInteger> requestCounts = new ConcurrentHashMap<>();

  MockOparlServer() throws IOException {
    this.server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0);
    this.server.start();
  }

  /** Answers requests to {@code path} with the given status code and body. */
  MockOparlServer respond(String path, int statusCode, String contentType, String body) {
    return this.respond(
        path, statusCode, contentType, body.getBytes(StandardCharsets.UTF_8), false);
  }

  /**
   * Answers requests to {@code path} with the given status code and raw body. If {@code chunked}
   * is set, the body is sent without {@code Content-Length}.
   */
  MockOparlServer respond(
      String path, int statusCode, String contentType, byte[] body, boolean chunked) {
    this.server.createContext(
        path,
        exchange -> {
          this.countRequest(path);
          // only read after it has been published by the AtomicReference
          @SuppressWarnings("PMD.UseConcurrentHashMap")
          Map<String, List<String>> headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
          headers.putAll(exchange.getRequestHeaders());
          this.lastRequestHeaders.set(headers);
          exchange.getResponseHeaders().add("Content-Type", contentType);
          long length = chunked ? 0 : (body.length == 0 ? -1 : body.length);
          exchange.sendResponseHeaders(statusCode, length);
          try (OutputStream out = exchange.getResponseBody()) {
            out.write(body);
          } catch (IOException e) {
            // the client cancelled the download
          }
        });
    return this;
  }

  /** Answers requests to {@code path} with the given json after waiting for {@code delay}. */
  MockOparlServer respondJsonDelayed(String path, Duration delay, String json) {
    this.server.createContext(
        path,
        exchange -> {
          try {
            Thread.sleep(delay.toMillis());
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
          }
          byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
          exchange.getResponseHeaders().add("Content-Type", "application/json");
          exchange.sendResponseHeaders(200, bytes.length);
          try (OutputStream out = exchange.getResponseBody()) {
            out.write(bytes);
          } catch (IOException e) {
            // the client gave up waiting
          }
        });
    return this;
  }

  /**
   * Answers requests to {@code path} with headers announcing a body, sends only its first byte and
   * then stalls for {@code stall}.
   */
  MockOparlServer respondStalled(String path, Duration stall) {
    this.server.createContext(
        path,
        exchange -> {
          exchange.getResponseHeaders().add("Content-Type", "application/json");
          exchange.sendResponseHeaders(200, 100);
          try (OutputStream out = exchange.getResponseBody()) {
            out.write('{');
            out.flush();
            Thread.sleep(stall.toMillis());
          } catch (IOException e) {
            // the client gave up waiting
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
          }
        });
    return this;
  }

  MockOparlServer respondJson(String path, int statusCode, String json) {
    return this.respond(path, statusCode, "application/json", json);
  }

  /** Answers requests to {@code path} with a redirect to the path {@code location}. */
  MockOparlServer redirect(String path, int statusCode, String location) {
    return this.redirectToLocationHeader(path, statusCode, this.uri(location).toString());
  }

  /** Answers requests to {@code path} with a redirect, sending {@code location} as is. */
  MockOparlServer redirectToLocationHeader(String path, int statusCode, String location) {
    this.server.createContext(
        path,
        exchange -> {
          this.countRequest(path);
          exchange.getResponseHeaders().add("Location", location);
          exchange.sendResponseHeaders(statusCode, -1);
          exchange.close();
        });
    return this;
  }

  /** Number of requests to {@code path} answered so far. */
  int requestCount(String path) {
    AtomicInteger count = this.requestCounts.get(path);
    return count != null ? count.get() : 0;
  }

  private void countRequest(String path) {
    this.requestCounts.computeIfAbsent(path, p -> new AtomicInteger()).incrementAndGet();
  }

  /** Header of the last request answered by {@link #respond}, or {@code null}. */
  String lastRequestHeader(String name) {
    Map<String, List<String>> headers = this.lastRequestHeaders.get();
    List<String> values = headers != null ? headers.get(name) : null;
    return values != null && !values.isEmpty() ? values.get(0) : null;
  }

  URI uri(String path) {
    return URI.create(
        "http://"
            + this.server.getAddress().getHostString()
            + ":"
            + this.server.getAddress().getPort()
            + path);
  }

  @Override
  public void close() {
    this.server.stop(0);
  }
}
