package com.sitepark.oparlclient.internal;

import com.sitepark.oparlclient.core.OparlConnectionException;
import com.sitepark.oparlclient.core.OparlException;
import com.sitepark.oparlclient.core.OparlTimeoutException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpTimeoutException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeoutException;

/** Maps the failures of requests to the exceptions of the client. */
public final class RequestFailures {

  private RequestFailures() {}

  /**
   * Maps a failed exchange of the http client to the matching {@link OparlException}, wrapped in
   * a {@link CompletionException} to complete the future with.
   *
   * @param response the future of the exchange, cancelled on a timeout
   */
  public static CompletionException of(URI uri, Throwable error, CompletableFuture<?> response) {
    Throwable cause =
        error instanceof CompletionException && error.getCause() != null ? error.getCause() : error;
    if (cause instanceof TimeoutException) {
      // aborts the exchange on JDKs supporting it; otherwise the connection is released when the
      // server closes it
      response.cancel(true);
      return new CompletionException(new OparlTimeoutException(uri, cause));
    }
    if (cause instanceof HttpTimeoutException) {
      return new CompletionException(new OparlTimeoutException(uri, cause));
    }
    if (cause instanceof IOException) {
      return new CompletionException(new OparlConnectionException(uri, cause));
    }
    if (cause instanceof OparlException || cause instanceof Error) {
      return error instanceof CompletionException completion
          ? completion
          : new CompletionException(error);
    }
    // any other failure of the http client, so that every failed request is an OparlException
    return new CompletionException(
        new OparlException(uri, "Request to " + uri + " failed: " + cause, cause));
  }
}
