package com.sitepark.oparlclient.core.query;

import com.sitepark.oparlclient.internal.OparlTimeSerialization;
import com.sitepark.oparlclient.internal.PercentEncoder;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

/**
 * A URL parameter, appended to a reference with {@link
 * com.sitepark.oparlclient.core.OparlReference#withQueryParams}. Use the subclasses for the
 * parameters defined by the specification, or this class for any other parameter.
 */
public class QueryParam {

  /** Characters besides letters and digits that are not encoded. */
  private static final String UNENCODED_CHARS = "-._~!$'()*,:@/?";

  private final String name;

  private final String value;

  /**
   * @param name the name of the parameter
   * @param value the value, unencoded; it is URL-encoded when the parameter is appended
   */
  public QueryParam(String name, String value) {
    this.name = name;
    this.value = value;
  }

  public String getName() {
    return this.name;
  }

  public String getValue() {
    return this.value;
  }

  /**
   * Returns the given parameters as URL-encoded query string, joined with {@code &}. {@code null}
   * and empty parameters are skipped.
   */
  public static String toString(QueryParam... params) {
    if (params == null) {
      return "";
    }
    StringBuilder paramString = new StringBuilder();
    for (QueryParam param : params) {
      String query = param != null ? param.toString() : "";
      if (query.isBlank()) {
        continue;
      }
      if (paramString.length() > 0) {
        paramString.append('&');
      }
      paramString.append(query);
    }
    return paramString.toString();
  }

  /** Returns {@code name=value}, URL-encoded for use in a query string. */
  @Override
  public String toString() {
    if (this.getName() == null || this.getName().isBlank() || this.getValue() == null) {
      return "";
    }
    return encode(this.getName()) + "=" + encode(this.getValue());
  }

  static String formatDateTime(OffsetDateTime dateTime) {
    // the specification requires a full date-time with seconds, but without fraction
    return OparlTimeSerialization.formatDateTime(dateTime.truncatedTo(ChronoUnit.SECONDS));
  }

  static String formatDateTime(ZonedDateTime dateTime) {
    return formatDateTime(dateTime.toOffsetDateTime());
  }

  static String formatDateTime(Instant instant) {
    return formatDateTime(instant.atOffset(ZoneOffset.UTC));
  }

  static String encode(String s) {
    return PercentEncoder.encode(s, UNENCODED_CHARS, false);
  }
}
