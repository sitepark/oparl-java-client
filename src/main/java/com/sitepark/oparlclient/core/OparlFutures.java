package com.sitepark.oparlclient.core;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/** Helpers for waiting on the futures returned by the client. */
public final class OparlFutures {

  private OparlFutures() {}

  /**
   * Waits for the future and returns its result. Unlike {@link CompletableFuture#join()}, the
   * actual cause is thrown instead of a {@link CompletionException}, for the futures of this
   * library always an {@link OparlException}, e.g. an {@link OparlHttpException}. Other unchecked
   * exceptions, e.g. the {@link CancellationException} of a cancelled future, are thrown as they
   * are, checked ones wrapped in an {@link OparlException}.
   */
  public static <T> T join(CompletableFuture<T> future) {
    try {
      return future.join();
    } catch (CompletionException e) {
      throw unwrap(e);
    }
  }

  private static RuntimeException unwrap(CompletionException e) {
    Throwable cause = e.getCause();
    if (cause == null) {
      return e;
    }
    if (cause instanceof RuntimeException runtimeException) {
      return runtimeException;
    }
    if (cause instanceof Error error) {
      throw error;
    }
    return new OparlException(cause.getMessage(), cause);
  }
}
