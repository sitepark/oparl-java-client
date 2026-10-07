package com.sitepark.oparlclient.core;

import java.util.regex.Pattern;

/**
 * Error object a server may return together with an error status code, see the chapter
 * "Ausnahmebehandlung" of the OParl specification.
 */
public class OparlError extends OparlObject {

  /** Type URL of the error object in OParl 1.1. */
  public static final String TYPE = "https://schema.oparl.org/1.1/Error";

  /** Type URLs of the error object in OParl 1.0 and 1.1. */
  private static final Pattern TYPE_PATTERN =
      Pattern.compile("https?://schema\\.oparl\\.org/1\\.[01]/Error/?");

  private String type;

  private String message;

  private String debug;

  /** Returns whether the given {@code type} URL denotes an error object of OParl 1.0 or 1.1. */
  public static boolean isErrorType(String type) {
    return type != null && TYPE_PATTERN.matcher(type.trim()).matches();
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getDebug() {
    return debug;
  }

  public void setDebug(String debug) {
    this.debug = debug;
  }
}
