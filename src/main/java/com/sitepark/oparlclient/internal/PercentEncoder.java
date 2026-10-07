package com.sitepark.oparlclient.internal;

import java.nio.charset.StandardCharsets;

/** Percent-encoding (RFC 3986) that leaves existing escape sequences untouched. */
public final class PercentEncoder {

  private static final char[] HEX_DIGITS = "0123456789ABCDEF".toCharArray();

  private PercentEncoder() {}

  /**
   * Encodes all characters of {@code value} as UTF-8 escape sequences, except letters, digits and
   * {@code unencodedChars}. A {@code %} is only kept if it starts a valid escape sequence, so
   * values that are already encoded are not encoded twice.
   */
  public static String encode(String value, String unencodedChars) {
    return encode(value, unencodedChars, true);
  }

  /**
   * Encodes all characters of {@code value} as UTF-8 escape sequences, except letters, digits and
   * {@code unencodedChars}.
   *
   * @param keepEscapeSequences whether a {@code %} starting a valid escape sequence is kept, so
   *     that values already encoded are not encoded twice; otherwise every {@code %} is encoded
   */
  public static String encode(String value, String unencodedChars, boolean keepEscapeSequences) {
    byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
    StringBuilder encoded = new StringBuilder(bytes.length);
    for (int i = 0; i < bytes.length; i++) {
      int c = bytes[i] & 0xff;
      if (isUnencoded(c, unencodedChars)
          || (keepEscapeSequences && c == '%' && isEscapeSequence(bytes, i))) {
        encoded.append((char) c);
      } else {
        encoded.append('%').append(HEX_DIGITS[c >> 4]).append(HEX_DIGITS[c & 0xf]);
      }
    }
    return encoded.toString();
  }

  private static boolean isUnencoded(int c, String unencodedChars) {
    return (c >= 'a' && c <= 'z')
        || (c >= 'A' && c <= 'Z')
        || (c >= '0' && c <= '9')
        || (c < 0x80 && unencodedChars.indexOf(c) >= 0);
  }

  private static boolean isEscapeSequence(byte[] bytes, int index) {
    return index + 2 < bytes.length && isHexDigit(bytes[index + 1]) && isHexDigit(bytes[index + 2]);
  }

  private static boolean isHexDigit(byte b) {
    return (b >= '0' && b <= '9') || (b >= 'a' && b <= 'f') || (b >= 'A' && b <= 'F');
  }
}
