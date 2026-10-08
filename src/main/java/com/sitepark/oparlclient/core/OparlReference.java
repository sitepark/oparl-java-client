package com.sitepark.oparlclient.core;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.sitepark.oparlclient.core.query.QueryParam;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * Reference to another OParl object or to a list of objects, given by its URL. The referenced
 * object is only requested when the reference is resolved:
 *
 * <pre>{@code
 * OparlReference<OparlMeeting> reference = consultation.getMeeting();
 * URI url = reference.getUri();          // no request
 * OparlMeeting meeting = reference.get(); // request
 * }</pre>
 *
 * <p>References are created when an object is read by an {@code OparlClient} and are resolved
 * through that client. A reference is immutable and can be shared between threads.
 *
 * @param <R> the type of the referenced object, e.g. {@code OparlMeeting} or {@code
 *     OparlList<OparlPaper>}
 */
public class OparlReference<R> {

  private final OparlReferenceResolver resolver;

  private final URI uri;

  private final JavaType referenceType;

  /**
   * Creates a reference. Usually references are created by the client while reading an object.
   *
   * @param uri the URL of the referenced object
   * @param referenceType the type the referenced object is mapped to
   * @param resolver resolves the reference, usually the {@code OparlClient}; without one, {@link
   *     #getAsync()} fails
   */
  public OparlReference(URI uri, JavaType referenceType, OparlReferenceResolver resolver) {
    this.uri = uri;
    this.referenceType = referenceType;
    this.resolver = resolver;
  }

  /** Returns the URL of the referenced object. A reference is serialized as this URL. */
  @JsonValue
  public URI getUri() {
    return this.uri;
  }

  /** Requests the referenced object asynchronously. */
  public CompletableFuture<R> getAsync() {
    if (this.resolver == null) {
      return missingResolver();
    }
    Type returnType = this.referenceType;
    return this.resolver.getAsync(
        this.uri,
        new TypeReference<R>() {
          @Override
          public Type getType() {
            return returnType;
          }
        });
  }

  /**
   * Requests the referenced object and waits for the result, see {@link OparlFutures#join} for the
   * thrown exceptions.
   */
  public R get() {
    return OparlFutures.join(this.getAsync());
  }

  /**
   * Requests the referenced object and maps it to another class than the declared one, e.g. to an
   * own subclass; waits for the result.
   */
  public <T> T get(Class<T> clazz) {
    return OparlFutures.join(this.getAsync(clazz));
  }

  /** See {@link #get(Class)}; for generic types. */
  public <T> T get(TypeReference<T> typeReference) {
    return OparlFutures.join(this.getAsync(typeReference));
  }

  /** Asynchronous variant of {@link #get(Class)}. */
  public <T> CompletableFuture<T> getAsync(Class<T> clazz) {
    return this.getAsync(
        new TypeReference<T>() {
          @Override
          public Type getType() {
            return clazz;
          }
        });
  }

  /** Asynchronous variant of {@link #get(TypeReference)}. */
  public <T> CompletableFuture<T> getAsync(TypeReference<T> typeReference) {
    if (this.resolver == null) {
      return missingResolver();
    }
    return this.resolver.getAsync(this.uri, typeReference);
  }

  private <T> CompletableFuture<T> missingResolver() {
    return CompletableFuture.failedFuture(
        new IllegalStateException(
            "No resolver set for the reference to "
                + this.uri
                + ", references are resolved through the OparlClient that read them"));
  }

  /**
   * Returns a copy of this reference with the given URL parameters appended, e.g. filters for a
   * list:
   *
   * <pre>{@code
   * body.getPaper().withQueryParams(Modified.since(date), OmitInternal.TRUE).get();
   * }</pre>
   *
   * <p>The parameters are URL-encoded; the existing URL is kept unchanged. This reference itself
   * is not modified.
   */
  public OparlReference<R> withQueryParams(QueryParam... queryParams) {
    String paramsString = QueryParam.toString(queryParams);
    URI uri = paramsString.isBlank() ? this.uri : this.uriWithParam(this.uri, paramsString);
    return new OparlReference<>(uri, this.referenceType, this.resolver);
  }

  /**
   * Appends the already encoded {@code param} to the query of {@code uri}. Works on the raw string
   * so that the existing parts of the URL, e.g. of a {@code next} link, stay unchanged.
   */
  private URI uriWithParam(URI uri, String param) {
    String base = uri.toString();
    String fragment = uri.getRawFragment();
    if (fragment != null) {
      base = base.substring(0, base.length() - fragment.length() - 1);
    }
    String separator;
    if (uri.getRawQuery() == null) {
      separator = "?";
    } else if (base.endsWith("?") || base.endsWith("&")) {
      separator = "";
    } else {
      separator = "&";
    }
    return URI.create(base + separator + param + (fragment != null ? "#" + fragment : ""));
  }

  /** Two references are equal if they have the same URL, regardless of their declared type. */
  @Override
  public boolean equals(Object o) {
    return this == o || o instanceof OparlReference<?> other && Objects.equals(this.uri, other.uri);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(this.uri);
  }

  @Override
  public String toString() {
    return "OparlReference[" + this.uri + "]";
  }
}
