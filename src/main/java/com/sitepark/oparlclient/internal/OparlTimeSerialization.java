package com.sitepark.oparlclient.internal;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;
import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;

/**
 * Reads and writes the {@code date} and {@code date-time} values of OParl, see {@link OparlDate}
 * and {@link OparlDateTime}.
 *
 * <p>Values are written in the format of the specification ({@code yyyy-mm-dd} and {@code
 * yyyy-mm-ddThh:mm:ss±hh:mm}). Reading is tolerant: date-times without offset are interpreted in
 * the time zone {@link #DEFAULT_ZONE}, a date where a date-time is expected as start of that day,
 * and a date-time where a date is expected as its date. Values that can not be parsed at all are
 * read as {@code null} and logged, so that a single malformed value does not prevent the whole
 * object from being read.
 */
public final class OparlTimeSerialization {

  /**
   * Time zone of date-times sent without offset. OParl is used by German council information
   * systems, so their local time is assumed.
   */
  public static final ZoneId DEFAULT_ZONE = ZoneId.of("Europe/Berlin");

  private static final Logger LOGGER = System.getLogger(OparlTimeSerialization.class.getName());

  /** {@code yyyy-mm-ddThh:mm:ss±hh:mm}, with fraction of seconds only if present. */
  private static final DateTimeFormatter DATE_TIME_FORMAT =
      new DateTimeFormatterBuilder()
          .append(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
          .appendOffset("+HH:MM", "+00:00")
          .toFormatter();

  private OparlTimeSerialization() {}

  /**
   * Parses an OParl {@code date-time} tolerantly.
   *
   * @return the date-time, or {@code null} if the value can not be parsed
   */
  public static OffsetDateTime parseDateTime(String value) {
    String normalized = normalize(value);
    try {
      return OffsetDateTime.parse(normalized);
    } catch (DateTimeParseException e) {
      // try the tolerated deviations below
    }
    try {
      return LocalDateTime.parse(normalized).atZone(DEFAULT_ZONE).toOffsetDateTime();
    } catch (DateTimeParseException e) {
      // try the tolerated deviations below
    }
    try {
      return LocalDate.parse(normalized).atStartOfDay(DEFAULT_ZONE).toOffsetDateTime();
    } catch (DateTimeParseException e) {
      LOGGER.log(Level.WARNING, "Ignoring invalid date-time ''{0}''", LogSafe.of(value));
      return null;
    }
  }

  /**
   * Parses an OParl {@code date} tolerantly.
   *
   * @return the date, or {@code null} if the value can not be parsed
   */
  public static LocalDate parseDate(String value) {
    String normalized = normalize(value);
    try {
      return LocalDate.parse(normalized);
    } catch (DateTimeParseException e) {
      // try the tolerated deviations below
    }
    try {
      return OffsetDateTime.parse(normalized).toLocalDate();
    } catch (DateTimeParseException e) {
      // try the tolerated deviations below
    }
    try {
      return LocalDateTime.parse(normalized).toLocalDate();
    } catch (DateTimeParseException e) {
      LOGGER.log(Level.WARNING, "Ignoring invalid date ''{0}''", LogSafe.of(value));
      return null;
    }
  }

  /** Formats a date-time as required by the specification: {@code yyyy-mm-ddThh:mm:ss±hh:mm}. */
  public static String formatDateTime(OffsetDateTime dateTime) {
    return DATE_TIME_FORMAT.format(dateTime);
  }

  /** Trims the value and accepts a space instead of {@code T} between date and time. */
  private static String normalize(String value) {
    String trimmed = value.trim();
    if (trimmed.length() > 10 && trimmed.charAt(10) == ' ') {
      trimmed = trimmed.substring(0, 10) + 'T' + trimmed.substring(11);
    }
    return trimmed;
  }

  /** Reads the string value of the current token, or {@code null} for other tokens. */
  private static String stringValue(JsonParser parser, String kind) throws IOException {
    if (parser.currentToken() != null && parser.currentToken().isScalarValue()) {
      String value = parser.getValueAsString();
      if (value != null) {
        return value;
      }
    }
    LOGGER.log(Level.WARNING, "Ignoring {0} of unexpected type {1}", kind, parser.currentToken());
    parser.skipChildren();
    return null;
  }

  /** Deserializer for {@code date-time} values. */
  public static final class DateTimeDeserializer extends StdScalarDeserializer<OffsetDateTime> {

    private static final long serialVersionUID = 1L;

    public DateTimeDeserializer() {
      super(OffsetDateTime.class);
    }

    @Override
    public OffsetDateTime deserialize(JsonParser parser, DeserializationContext ctxt)
        throws IOException {
      String value = stringValue(parser, "date-time");
      return value != null ? parseDateTime(value) : null;
    }
  }

  /** Serializer for {@code date-time} values. */
  public static final class DateTimeSerializer extends StdScalarSerializer<OffsetDateTime> {

    private static final long serialVersionUID = 1L;

    public DateTimeSerializer() {
      super(OffsetDateTime.class);
    }

    @Override
    public void serialize(
        OffsetDateTime value, JsonGenerator generator, SerializerProvider provider)
        throws IOException {
      generator.writeString(formatDateTime(value));
    }
  }

  /** Deserializer for {@code date} values. */
  public static final class DateDeserializer extends StdScalarDeserializer<LocalDate> {

    private static final long serialVersionUID = 1L;

    public DateDeserializer() {
      super(LocalDate.class);
    }

    @Override
    public LocalDate deserialize(JsonParser parser, DeserializationContext ctxt)
        throws IOException {
      String value = stringValue(parser, "date");
      return value != null ? parseDate(value) : null;
    }
  }

  /** Serializer for {@code date} values. */
  public static final class DateSerializer extends StdScalarSerializer<LocalDate> {

    private static final long serialVersionUID = 1L;

    public DateSerializer() {
      super(LocalDate.class);
    }

    @Override
    public void serialize(LocalDate value, JsonGenerator generator, SerializerProvider provider)
        throws IOException {
      generator.writeString(DateTimeFormatter.ISO_LOCAL_DATE.format(value));
    }
  }
}
