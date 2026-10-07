package com.sitepark.oparlclient.v1.objects;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Maps the {@code type} URLs of OParl objects to the classes representing them. */
public final class OparlTypes {

  /** Namespace of the OParl 1.1 object types. */
  public static final String NAMESPACE = "https://schema.oparl.org/1.1/";

  /** Type URLs of OParl 1.0 and 1.1, e.g. {@code https://schema.oparl.org/1.1/Body}. */
  private static final Pattern TYPE_PATTERN =
      Pattern.compile("https?://schema\\.oparl\\.org/1\\.[01]/(\\w+)/?");

  private static final Map<String, Class<? extends OparlObjectV1>> CLASSES =
      Map.ofEntries(
          Map.entry("AgendaItem", OparlAgendaItem.class),
          Map.entry("Body", OparlBody.class),
          Map.entry("Consultation", OparlConsultation.class),
          Map.entry("File", OparlFile.class),
          Map.entry("LegislativeTerm", OparlLegislativeTerm.class),
          Map.entry("Location", OparlLocation.class),
          Map.entry("Meeting", OparlMeeting.class),
          Map.entry("Membership", OparlMembership.class),
          Map.entry("Organization", OparlOrganization.class),
          Map.entry("Paper", OparlPaper.class),
          Map.entry("Person", OparlPerson.class),
          Map.entry("System", OparlSystem.class));

  private OparlTypes() {}

  /**
   * Returns the class for the given {@code type} URL. Type URLs of OParl 1.0 and 1.1 are accepted.
   *
   * @return the class, or {@code null} if the type is unknown
   */
  public static Class<? extends OparlObjectV1> classOf(String type) {
    if (type == null) {
      return null;
    }
    Matcher matcher = TYPE_PATTERN.matcher(type.trim());
    return matcher.matches() ? CLASSES.get(matcher.group(1)) : null;
  }

  /**
   * Returns the OParl 1.1 {@code type} URL for the given class.
   *
   * @return the type URL, or {@code null} if the class represents no OParl object type
   */
  public static String typeOf(Class<?> clazz) {
    for (Map.Entry<String, Class<? extends OparlObjectV1>> entry : CLASSES.entrySet()) {
      if (entry.getValue().equals(clazz)) {
        return NAMESPACE + entry.getKey();
      }
    }
    return null;
  }
}
