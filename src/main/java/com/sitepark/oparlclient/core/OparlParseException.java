package com.sitepark.oparlclient.core;

import java.net.URI;

/**
 * Thrown when a response is no valid JSON or can not be mapped to the requested type. The cause is
 * the exception of the JSON parser.
 */
public class OparlParseException extends OparlException {

  private static final long serialVersionUID = 1L;

  public OparlParseException(URI uri, Throwable cause) {
    super(uri, "Invalid response from " + uri + ": " + cause.getMessage(), cause);
  }
}
