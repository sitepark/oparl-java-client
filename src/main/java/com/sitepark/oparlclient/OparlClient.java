package com.sitepark.oparlclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.sitepark.oparlclient.core.OparlError;
import com.sitepark.oparlclient.core.OparlException;
import com.sitepark.oparlclient.core.OparlFutures;
import com.sitepark.oparlclient.core.OparlHttpException;
import com.sitepark.oparlclient.core.OparlList;
import com.sitepark.oparlclient.core.OparlParseException;
import com.sitepark.oparlclient.core.OparlReference;
import com.sitepark.oparlclient.core.OparlReferenceResolver;
import com.sitepark.oparlclient.internal.LenientProperties;
import com.sitepark.oparlclient.internal.LenientUriDeserializer;
import com.sitepark.oparlclient.internal.LogSafe;
import com.sitepark.oparlclient.internal.OparlReferenceDeserializer;
import com.sitepark.oparlclient.internal.RequestFailures;
import com.sitepark.oparlclient.v1.objects.OparlBody;
import com.sitepark.oparlclient.v1.objects.OparlObjectV1;
import com.sitepark.oparlclient.v1.objects.OparlTypes;
import java.io.IOException;
import java.io.InputStream;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Client for OParl interfaces. Resolves OParl objects and lists from their URLs and maps them to
 * the classes in {@code com.sitepark.oparlclient.v1.objects}; references between objects are
 * resolved through the same client.
 *
 * <pre>{@code
 * OparlClient client = new OparlClient();
 * OparlSystem system = client.get("https://oparl.example.org/", OparlSystem.class);
 * for (OparlBody body : system.getBody().get().all()) {
 *   // ...
 * }
 * }</pre>
 *
 * <p>Every request exists in two variants: the methods ending in {@code Async} return a {@link
 * CompletableFuture}, the others wait for the result. A client is thread-safe and
 * should be reused. Use {@link #builder()} to configure the timeout or the user agent.
 */
// the entry point of the library: brings together the http client, Jackson and the OParl types
@SuppressWarnings({"PMD.CouplingBetweenObjects", "PMD.ExcessiveImports", "PMD.TooManyMethods"})
public class OparlClient implements OparlReferenceResolver {

  private static final Logger LOGGER = System.getLogger(OparlClient.class.getName());

  /** Timeout of a single request if none is configured. */
  public static final Duration DEFAULT_REQUEST_TIMEOUT = Duration.ofSeconds(10);

  /** {@code User-Agent} sent if none is configured: {@code oparl-java-client/<version>}. */
  public static final String DEFAULT_USER_AGENT = defaultUserAgent();

  private final ObjectMapper mapper;

  private final InjectableValues injectableValues;

  private final HttpClient client;

  private final Duration requestTimeout;

  private final String userAgent;

  /**
   * Creates a client with default settings and a default {@link HttpClient} that follows
   * redirects. Use {@link #builder()} to configure the client.
   */
  public OparlClient() {
    this(builder());
  }

  // the client registers itself as resolver of references; it is only used once the constructor
  // has completed, when the first response is mapped
  @SuppressWarnings("this-escape")
  private OparlClient(Builder builder) {
    this.requestTimeout =
        builder.requestTimeout != null ? builder.requestTimeout : DEFAULT_REQUEST_TIMEOUT;
    this.userAgent = builder.userAgent != null ? builder.userAgent : DEFAULT_USER_AGENT;
    this.client =
        builder.httpClient != null
            ? builder.httpClient
            : HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();
    this.mapper = new ObjectMapper();
    SimpleModule module = new SimpleModule();
    module.addDeserializer(OparlReference.class, new OparlReferenceDeserializer());
    module.addDeserializer(URI.class, new LenientUriDeserializer());
    module.setDeserializerModifier(new LenientProperties());
    this.mapper.registerModule(module);
    // some servers send a single value where the specification requires a list
    this.mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
    if (builder.objectMapperCustomizer != null) {
      builder.objectMapperCustomizer.accept(this.mapper);
    }
    this.injectableValues = new InjectableValues.Std().addValue(OparlReferenceResolver.class, this);
  }

  /** Returns a builder to configure a new client. */
  public static Builder builder() {
    return new Builder();
  }

  /** Returns the http client used to send the requests. */
  public HttpClient getHttpClient() {
    return client;
  }

  /** Returns the timeout of a single request. */
  public Duration getRequestTimeout() {
    return requestTimeout;
  }

  /** Returns the value of the {@code User-Agent} header sent with every request. */
  public String getUserAgent() {
    return userAgent;
  }

  /**
   * Requests the OParl object at the given URL asynchronously.
   *
   * @param uri the URL of the object
   * @param clazz the class to map the object to, e.g. {@code OparlBody.class}
   * @return the object; the future fails with an {@link OparlException}, e.g. an {@link
   *     OparlHttpException} if the server answers with an error
   */
  public <R> CompletableFuture<R> getAsync(String uri, Class<R> clazz) {
    URI parsed;
    try {
      parsed = URI.create(uri);
    } catch (IllegalArgumentException e) {
      return CompletableFuture.failedFuture(invalidUrl(uri, e));
    }
    return this.getAsync(
        parsed,
        new TypeReference<R>() {
          @Override
          public Type getType() {
            return clazz;
          }
        });
  }

  /** See {@link #getAsync(String, Class)}. */
  public <R> CompletableFuture<R> getAsync(URI uri, Class<R> clazz) {
    return this.getAsync(
        uri,
        new TypeReference<R>() {
          @Override
          public Type getType() {
            return clazz;
          }
        });
  }

  /**
   * Requests the OParl object at the given URL and waits for the result, see {@link
   * OparlFutures#join} for the thrown exceptions.
   */
  public <R> R get(String uri, Class<R> clazz) {
    return OparlFutures.join(this.getAsync(uri, clazz));
  }

  /** See {@link #get(String, Class)}. */
  public <R> R get(URI uri, Class<R> clazz) {
    return OparlFutures.join(this.getAsync(uri, clazz));
  }

  /** See {@link #get(String, Class)}. */
  public <R> R get(URI uri, TypeReference<R> typeReference) {
    return OparlFutures.join(this.getAsync(uri, typeReference));
  }

  /**
   * Requests the OParl object or list at the given URL asynchronously. Use this variant for generic
   * types, e.g. a list page:
   *
   * <pre>{@code
   * client.getAsync(uri, new TypeReference<OparlList<OparlMeeting>>() {});
   * }</pre>
   *
   * @see #getAsync(String, Class)
   */
  @Override
  public <R> CompletableFuture<R> getAsync(URI uri, TypeReference<R> typeReference) {
    return this.send(uri)
        .thenApply(
            response -> {
              int statusCode = response.statusCode();
              if (statusCode < 200 || statusCode >= 300) {
                throw new CompletionException(
                    new OparlHttpException(
                        response.uri(), statusCode, this.parseError(response.body())));
              }
              return response;
            })
        .thenApply(
            response -> {
              R result;
              try {
                result = this.deserializeJson(response.body(), typeReference);
              } catch (JsonProcessingException e) {
                throw new CompletionException(new OparlParseException(response.uri(), e));
              }
              if (isEmpty(result)) {
                // e.g. a body "null": must not be mistaken for an object or the end of a list
                throw new CompletionException(
                    new OparlException(response.uri(), "Empty response from " + response.uri()));
              }
              if (result instanceof OparlList) {
                Set<URI> sourceUris = new LinkedHashSet<>();
                sourceUris.add(uri);
                sourceUris.add(response.uri());
                ((OparlList<?>) result).setSourceUris(sourceUris);
              }
              return result;
            });
  }

  /**
   * Requests an OParl object without knowing its type in advance and waits for the result, see
   * {@link OparlFutures#join} for the thrown exceptions. The class of the result is
   * determined by the {@code type} property, e.g. an {@link OparlBody} for {@code
   * https://schema.oparl.org/1.1/Body}.
   *
   * <p>Servers may deliver vendor-specific object types, and the specification requires clients
   * not to fail on them. Objects of an unknown type, or without {@code type}, are therefore returned
   * as plain {@link OparlObjectV1}; their remaining properties are available via {@link
   * OparlObjectV1#getAdditionalProperties()}.
   *
   * @return the object
   */
  public OparlObjectV1 getAny(String uri) {
    return OparlFutures.join(this.getAnyAsync(uri));
  }

  /** See {@link #getAny(String)}. */
  public OparlObjectV1 getAny(URI uri) {
    return OparlFutures.join(this.getAnyAsync(uri));
  }

  /** Asynchronous variant of {@link #getAny(String)}. */
  public CompletableFuture<OparlObjectV1> getAnyAsync(String uri) {
    try {
      return this.getAnyAsync(URI.create(uri));
    } catch (IllegalArgumentException e) {
      return CompletableFuture.failedFuture(invalidUrl(uri, e));
    }
  }

  /** Asynchronous variant of {@link #getAny(String)}. */
  public CompletableFuture<OparlObjectV1> getAnyAsync(URI uri) {
    return this.getAsync(uri, new TypeReference<JsonNode>() {})
        .thenApply(
            node -> {
              String type = node.path("type").isTextual() ? node.get("type").textValue() : null;
              Class<? extends OparlObjectV1> clazz = OparlTypes.classOf(type);
              if (clazz == null) {
                clazz = OparlObjectV1.class;
              }
              return this.convert(uri, node, clazz);
            });
  }

  private <T> T convert(URI uri, JsonNode node, Class<T> clazz) {
    try {
      return this.mapper.reader(this.injectableValues).forType(clazz).readValue(node);
    } catch (IOException e) {
      throw new CompletionException(new OparlParseException(uri, e));
    }
  }

  private static OparlException invalidUrl(String uri, IllegalArgumentException e) {
    return new OparlException("Invalid URL " + LogSafe.of(uri) + ": " + e.getMessage(), e);
  }

  private static boolean isEmpty(Object result) {
    return result == null
        || (result instanceof JsonNode
            && (((JsonNode) result).isNull() || ((JsonNode) result).isMissingNode()));
  }

  /**
   * Sends a request to {@code uri}. All errors, including invalid URLs, are reported by the
   * returned future.
   */
  private CompletableFuture<HttpResponse<String>> send(URI uri) {
    try {
      if (!isHttpUrl(uri)) {
        return CompletableFuture.failedFuture(
            new OparlException(
                uri,
                "Unsupported URL " + uri + ", only absolute http and https URLs can be requested"));
      }
      LOGGER.log(Level.DEBUG, "Requesting {0}", uri);
      return this.withTimeout(
          uri, this.client.sendAsync(this.request(uri), BodyHandlers.ofString()));
    } catch (IllegalArgumentException e) {
      return CompletableFuture.failedFuture(new OparlException(uri, e.getMessage(), e));
    }
  }

  private static boolean isHttpUrl(URI uri) {
    String scheme = uri.getScheme();
    return uri.isAbsolute()
        && uri.getHost() != null
        && ("https".equalsIgnoreCase(scheme) || "http".equalsIgnoreCase(scheme));
  }

  /**
   * Limits the whole exchange, including reading the body, to the request timeout. Depending on the
   * JDK version, the timeout of the {@link HttpRequest} only covers the time until the response
   * headers arrive, so a server stalling while sending the body would block forever.
   */
  private CompletableFuture<HttpResponse<String>> withTimeout(
      URI uri, CompletableFuture<HttpResponse<String>> response) {
    return response
        .copy()
        .orTimeout(this.requestTimeout.toMillis(), TimeUnit.MILLISECONDS)
        .handle(
            (result, error) -> {
              if (error == null) {
                return result;
              }
              throw RequestFailures.of(uri, error, response);
            });
  }

  private HttpRequest request(URI uri) {
    return HttpRequest.newBuilder()
        .uri(uri)
        .timeout(this.requestTimeout)
        .header("Accept", "application/json")
        .header("User-Agent", this.userAgent)
        .GET()
        .build();
  }

  /** Returns the OParl error object contained in the body, or {@code null} if there is none. */
  private OparlError parseError(String body) {
    if (body == null || body.isBlank()) {
      return null;
    }
    try {
      OparlError error = this.mapper.readValue(body, OparlError.class);
      return OparlError.isErrorType(error.getType()) ? error : null;
    } catch (JsonProcessingException e) {
      return null;
    }
  }

  private static String defaultUserAgent() {
    String name = "oparl-java-client";
    try (InputStream in = OparlClient.class.getResourceAsStream("version.properties")) {
      if (in == null) {
        return name;
      }
      Properties properties = new Properties();
      properties.load(in);
      String version = properties.getProperty("version");
      return version != null && !version.isBlank() && !version.startsWith("${")
          ? name + "/" + version
          : name;
    } catch (IOException e) {
      return name;
    }
  }

  /**
   * Maps the given JSON to an object, exactly as for a response of the server. References in the
   * result are resolved through this client.
   *
   * @throws JsonProcessingException if the JSON is invalid or can not be mapped to the type
   */
  public <T> T deserializeJson(String json, TypeReference<T> typeReference)
      throws JsonProcessingException {
    return this.mapper.reader(this.injectableValues).forType(typeReference).readValue(json);
  }

  /** Configures and creates an {@link OparlClient}. */
  public static final class Builder {

    private HttpClient httpClient;

    private Duration requestTimeout;

    private Consumer<ObjectMapper> objectMapperCustomizer;

    private String userAgent;

    private Builder() {}

    /**
     * Sets the http client to use. Its redirect policy is used as is; OParl servers may redirect to
     * their canonical URLs, so {@link HttpClient.Redirect#NORMAL} is recommended. If none is set, a
     * default client that follows redirects is used.
     */
    public Builder httpClient(HttpClient httpClient) {
      this.httpClient = httpClient;
      return this;
    }

    /**
     * Sets the timeout of a single request, {@code null} for {@link #DEFAULT_REQUEST_TIMEOUT}.
     *
     * @throws IllegalArgumentException if the timeout is zero or negative
     */
    public Builder requestTimeout(Duration requestTimeout) {
      if (requestTimeout != null && (requestTimeout.isZero() || requestTimeout.isNegative())) {
        throw new IllegalArgumentException("requestTimeout must be positive: " + requestTimeout);
      }
      this.requestTimeout = requestTimeout;
      return this;
    }

    /**
     * Sets the {@code User-Agent} header, {@code null} for {@link #DEFAULT_USER_AGENT}. Setting an
     * own value that names the application and a contact helps server operators, e.g. {@code
     * "my-app/1.0 (+https://example.org/contact)"}.
     *
     * @throws IllegalArgumentException if the user agent is blank or no valid header value
     */
    public Builder userAgent(String userAgent) {
      if (userAgent != null) {
        if (userAgent.isBlank()) {
          throw new IllegalArgumentException("userAgent must not be blank");
        }
        // fails early for values the http client would reject, e.g. containing line breaks
        HttpRequest.newBuilder().header("User-Agent", userAgent);
      }
      this.userAgent = userAgent;
      return this;
    }

    /**
     * Allows to adjust the {@link ObjectMapper} used to read responses, e.g. to register additional
     * modules. It is called after the client has registered its own deserializers.
     */
    public Builder objectMapperCustomizer(Consumer<ObjectMapper> objectMapperCustomizer) {
      this.objectMapperCustomizer = objectMapperCustomizer;
      return this;
    }

    /** Creates the client. */
    public OparlClient build() {
      return new OparlClient(this);
    }
  }
}
