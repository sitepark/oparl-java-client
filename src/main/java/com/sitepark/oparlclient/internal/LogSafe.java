package com.sitepark.oparlclient.internal;

/**
 * Makes strings sent by a server safe to include in log and exception messages, so that a server
 * can not forge log lines by sending line breaks or other control characters.
 */
public final class LogSafe {

  private LogSafe() {}

  /** Replaces control characters, including line breaks, with {@code ?}. */
  public static String of(String value) {
    if (value == null) {
      return null;
    }
    StringBuilder safe = new StringBuilder(value.length());
    for (int i = 0; i < value.length(); i++) {
      char c = value.charAt(i);
      safe.append(Character.isISOControl(c) ? '?' : c);
    }
    return safe.toString();
  }
}
