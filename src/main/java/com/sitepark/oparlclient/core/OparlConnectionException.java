package com.sitepark.oparlclient.core;

import java.net.URI;

/**
 * Thrown when a request fails before a complete response arrived, e.g. because the server can not
 * be reached or closes the connection. The cause is the exception of the http client.
 */
public class OparlConnectionException extends OparlException {

  private static final long serialVersionUID = 1L;

  public OparlConnectionException(URI uri, Throwable cause) {
    this(uri, "Request to " + uri + " failed: " + cause, cause);
  }

  protected OparlConnectionException(URI uri, String message, Throwable cause) {
    super(uri, message, cause);
  }
}
