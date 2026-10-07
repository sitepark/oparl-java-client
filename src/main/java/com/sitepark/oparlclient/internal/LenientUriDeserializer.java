package com.sitepark.oparlclient.internal;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.URI;

/**
 * Deserializes URLs that are not valid URIs, e.g. because they contain spaces, by percent-encoding
 * the offending characters. A single malformed URL thereby no longer prevents the whole object or
 * list page from being read. URLs that can not be repaired are deserialized as {@code null}.
 *
 * <p>Valid URLs are kept exactly as sent by the server, as required by the OParl specification.
 * Only invalid URLs are changed, since they could not be requested otherwise; for these, the
 * resulting URI differs from the string sent by the server.
 */
public class LenientUriDeserializer extends StdScalarDeserializer<URI> {

  private static final long serialVersionUID = 1L;

  private static final Logger LOGGER = System.getLogger(LenientUriDeserializer.class.getName());

  /** Characters besides letters and digits that may appear in a URI (RFC 3986). */
  private static final String URI_CHARS = "-._~:/?#[]@!$&'()*+,;=";

  public LenientUriDeserializer() {
    super(URI.class);
  }

  @Override
  public URI deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
    String value = parser.getValueAsString();
    if (value == null) {
      return (URI) ctxt.handleUnexpectedToken(URI.class, parser);
    }
    return parse(value);
  }

  /**
   * Parses the given URL, percent-encoding characters that are not allowed in a URI.
   *
   * @return the URI, or {@code null} if the URL can not be parsed even after encoding
   */
  public static URI parse(String value) {
    String trimmed = value.trim();
    try {
      return URI.create(trimmed);
    } catch (IllegalArgumentException e) {
      // fall through and try to repair the URL
    }
    try {
      URI uri = URI.create(PercentEncoder.encode(trimmed, URI_CHARS));
      LOGGER.log(Level.DEBUG, "Encoded invalid characters in URL ''{0}''", LogSafe.of(value));
      return uri;
    } catch (IllegalArgumentException e) {
      LOGGER.log(
          Level.WARNING,
          "Ignoring invalid URL ''{0}'': {1}",
          LogSafe.of(value),
          LogSafe.of(e.getMessage()));
      return null;
    }
  }
}
