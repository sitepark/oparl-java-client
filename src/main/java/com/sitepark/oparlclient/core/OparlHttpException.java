package com.sitepark.oparlclient.core;

import com.sitepark.oparlclient.internal.LogSafe;
import java.net.URI;

/**
 * Thrown when a server answers with a status code outside of 2xx. If the server sent an OParl
 * error object, it is available via {@link #getError()}.
 */
public class OparlHttpException extends OparlException {

  private static final long serialVersionUID = 1L;

  private final int statusCode;

  private final transient OparlError error;

  public OparlHttpException(URI uri, int statusCode, OparlError error) {
    super(uri, buildMessage(uri, statusCode, error));
    this.statusCode = statusCode;
    this.error = error;
  }

  public int getStatusCode() {
    return statusCode;
  }

  /**
   * @return the error object sent by the server, or {@code null} if the response did not contain
   *     one
   */
  public OparlError getError() {
    return error;
  }

  private static String buildMessage(URI uri, int statusCode, OparlError error) {
    String message = "HTTP " + statusCode + " for " + uri;
    if (error != null && error.getMessage() != null) {
      message += ": " + LogSafe.of(error.getMessage());
    }
    return message;
  }
}
