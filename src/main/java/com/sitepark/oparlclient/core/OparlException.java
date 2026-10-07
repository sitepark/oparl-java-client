package com.sitepark.oparlclient.core;

import java.net.URI;

/**
 * Base class of all exceptions thrown by the OParl client. Every failed request is reported as an
 * {@code OparlException}, asynchronously as cause of the {@code CompletionException} and
 * synchronously as is:
 *
 * <ul>
 *   <li>{@link OparlHttpException}: the server answered with a status code outside of 2xx
 *   <li>{@link OparlParseException}: the response is no valid JSON or does not match the type
 *   <li>{@link OparlConnectionException}: the server could not be reached, see also {@link
 *       OparlTimeoutException}
 *   <li>{@code OparlException} itself: other errors, e.g. an invalid URL or an empty response
 * </ul>
 */
public class OparlException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  private final URI uri;

  public OparlException(String message) {
    this(null, message, null);
  }

  public OparlException(String message, Throwable cause) {
    this(null, message, cause);
  }

  public OparlException(URI uri, String message) {
    this(uri, message, null);
  }

  public OparlException(URI uri, String message, Throwable cause) {
    super(message, cause);
    this.uri = uri;
  }

  /** Returns the URL of the failed request, or {@code null} if the error is not tied to one. */
  public URI getUri() {
    return this.uri;
  }
}
