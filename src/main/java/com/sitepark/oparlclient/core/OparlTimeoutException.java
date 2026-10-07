package com.sitepark.oparlclient.core;

import java.net.URI;

/** Thrown when a request takes longer than the configured request timeout. */
public class OparlTimeoutException extends OparlConnectionException {

  private static final long serialVersionUID = 1L;

  public OparlTimeoutException(URI uri, Throwable cause) {
    super(uri, "Request to " + uri + " timed out", cause);
  }
}
