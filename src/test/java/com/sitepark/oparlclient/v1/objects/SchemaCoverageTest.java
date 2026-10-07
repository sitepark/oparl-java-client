package com.sitepark.oparlclient.v1.objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sitepark.oparlclient.core.OparlList;
import com.sitepark.oparlclient.core.OparlReference;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

/**
 * Checks the object classes against the OParl 1.1 schema in {@code src/test/resources/schema/1.1}:
 * every property of the schema must be mapped by the corresponding class, with a matching type.
 */
class SchemaCoverageTest {

  private static final String EXTERNAL_LIST = "externalList";

  private static final List<String> TYPES =
      List.of(
          "AgendaItem",
          "Body",
          "Consultation",
          "File",
          "LegislativeTerm",
          "Location",
          "Meeting",
          "Membership",
          "Organization",
          "Paper",
          "Person",
          "System");

  private final ObjectMapper mapper = new ObjectMapper();

  @Test
  void coversAllObjectTypes() {
    for (String type : TYPES) {
      assertNotNull(OparlTypes.classOf(OparlTypes.NAMESPACE + type), "no class for type " + type);
    }
    assertEquals(TYPES.size(), Set.copyOf(TYPES).size());
  }

  @TestFactory
  @SuppressWarnings("PMD.UnitTestShouldIncludeAssert") // the dynamic tests assert
  Stream<DynamicTest> mapsEveryPropertyOfTheSchema() {
    return TYPES.stream().map(type -> DynamicTest.dynamicTest(type, () -> this.checkSchema(type)));
  }

  private void checkSchema(String type) throws IOException {
    JsonNode schema = this.readSchema(type);
    Class<?> clazz = OparlTypes.classOf(OparlTypes.NAMESPACE + type);
    Map<String, JavaType> mappedProperties = this.mappedProperties(clazz);

    List<String> problems = new ArrayList<>();
    for (Map.Entry<String, JsonNode> property : this.propertiesOf(schema).entrySet()) {
      String name = property.getKey();
      JavaType javaType = mappedProperties.get(name);
      if (javaType == null) {
        problems.add(name + ": not mapped");
      } else if (!this.matches(name, property.getValue(), javaType)) {
        problems.add(name + ": " + describe(property.getValue()) + " mapped as " + javaType);
      }
    }
    assertTrue(problems.isEmpty(), clazz.getSimpleName() + "\n  " + String.join("\n  ", problems));
  }

  private JsonNode readSchema(String type) throws IOException {
    try (InputStream in =
        SchemaCoverageTest.class.getResourceAsStream("/schema/1.1/" + type + ".json")) {
      assertNotNull(in, "schema " + type + " not found");
      return this.mapper.readTree(in);
    }
  }

  /** The JSON properties Jackson can deserialize for the given class, with their types. */
  private Map<String, JavaType> mappedProperties(Class<?> clazz) {
    JavaType type = this.mapper.constructType(clazz);
    BeanDescription description = this.mapper.getDeserializationConfig().introspect(type);
    return description.findProperties().stream()
        .filter(property -> property.hasSetter() || property.hasField())
        .collect(
            Collectors.toMap(
                BeanPropertyDefinition::getName, BeanPropertyDefinition::getPrimaryType));
  }

  private boolean matches(String name, JsonNode property, JavaType javaType) {
    String type = property.path("type").asText();
    switch (type) {
      case "boolean":
        return is(javaType, boolean.class, Boolean.class);
      case "integer":
        return is(javaType, int.class, Integer.class, long.class, Long.class);
      case "string":
        return this.matchesString(name, property, javaType);
      case "array":
        JsonNode items = property.get("items");
        if (property.has("references") && !items.has("references")) {
          // the schema declares the referenced type of the elements on the array itself
          items =
              ((ObjectNode) items.deepCopy())
                  .put("references", property.get("references").asText());
        }
        return javaType.isCollectionLikeType()
            && this.matches(name, items, javaType.getContentType());
      case "object":
        String schema = property.path("schema").asText(null);
        if (schema == null) {
          // free-form object, e.g. geojson
          return is(javaType, Object.class, JsonNode.class, Map.class);
        }
        return javaType.hasRawClass(classOfSchema(schema));
      default:
        return false;
    }
  }

  private boolean matchesString(String name, JsonNode property, JavaType javaType) {
    String format = property.path("format").asText("");
    switch (format) {
      case "url":
        String references = property.path("references").asText(null);
        if (references == null) {
          // license is a url only for Body and System, a plain string for all other types; it is
          // mapped as String in the common base class, so that no value is lost
          return is(javaType, URI.class) || ("license".equals(name) && is(javaType, String.class));
        }
        return this.matchesReference(references, property.get("items"), javaType);
      case "date":
        return is(javaType, LocalDate.class);
      case "date-time":
        return is(javaType, OffsetDateTime.class);
      default:
        return is(javaType, String.class);
    }
  }

  /** A reference, also inside an array, is mapped as {@code OparlReference} of the referenced class. */
  private boolean matchesReference(String references, JsonNode items, JavaType javaType) {
    if (!javaType.hasRawClass(OparlReference.class) || javaType.containedType(0) == null) {
      return false;
    }
    JavaType referenced = javaType.containedType(0);
    if (EXTERNAL_LIST.equals(references)) {
      return referenced.hasRawClass(OparlList.class)
          && referenced.containedType(0) != null
          && referenced.containedType(0).hasRawClass(classOfSchema(items.path("schema").asText()));
    }
    return referenced.hasRawClass(OparlTypes.classOf(OparlTypes.NAMESPACE + references));
  }

  private static Class<?> classOfSchema(String schema) {
    return OparlTypes.classOf(OparlTypes.NAMESPACE + schema.replaceAll("\\.json$", ""));
  }

  private static boolean is(JavaType javaType, Class<?>... classes) {
    for (Class<?> clazz : classes) {
      if (javaType.hasRawClass(clazz)) {
        return true;
      }
    }
    return false;
  }

  private static String describe(JsonNode property) {
    StringBuilder description = new StringBuilder(64).append(property.path("type").asText());
    if (property.has("format")) {
      description.append(" (").append(property.get("format").asText()).append(')');
    }
    if (property.has("references")) {
      description.append(" -> ").append(property.get("references").asText());
    }
    if (property.has("items")) {
      description.append(" of ").append(describe(property.get("items")));
    }
    return description.toString();
  }

  /** The properties of a schema; {@code JsonNode.properties()} requires Jackson 2.15. */
  private Map<String, JsonNode> propertiesOf(JsonNode schema) {
    return this.mapper.convertValue(
        schema.get("properties"), new TypeReference<Map<String, JsonNode>>() {});
  }
}
