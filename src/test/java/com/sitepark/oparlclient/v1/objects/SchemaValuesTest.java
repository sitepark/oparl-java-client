package com.sitepark.oparlclient.v1.objects;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sitepark.oparlclient.OparlClient;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

/**
 * Reads an object of every type in which each property of the OParl 1.1 schema is set, and checks
 * that every value arrives at its getter. Complements {@link SchemaCoverageTest}, which only
 * compares the declared types.
 */
class SchemaValuesTest {

  private static final JsonNodeFactory JSON = JsonNodeFactory.instance;

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

  private final OparlClient client = new OparlClient();

  @TestFactory
  @SuppressWarnings("PMD.UnitTestShouldIncludeAssert") // the dynamic tests assert
  Stream<DynamicTest> passesEveryPropertyToItsGetter() {
    return TYPES.stream().map(type -> DynamicTest.dynamicTest(type, () -> this.check(type)));
  }

  private void check(String type) throws Exception {
    Map<String, JsonNode> properties = this.propertiesOf(this.readSchema(type));
    ObjectNode json = JSON.objectNode();
    properties.forEach((name, property) -> json.set(name, sample(property)));
    Class<? extends OparlObjectV1> clazz = OparlTypes.classOf(OparlTypes.NAMESPACE + type);

    OparlObjectV1 object =
        this.client.deserializeJson(
            json.toString(),
            new TypeReference<OparlObjectV1>() {
              @Override
              public Type getType() {
                return clazz;
              }
            });

    Map<String, Method> getters = this.getters(clazz);
    List<String> problems = new ArrayList<>();
    for (Map.Entry<String, JsonNode> property : properties.entrySet()) {
      String name = property.getKey();
      Method getter = getters.get(name);
      Object value = getter != null ? getter.invoke(object) : null;
      if (value == null || value instanceof Collection<?> c && c.isEmpty()) {
        problems.add(name + ": no value");
      }
    }
    assertTrue(problems.isEmpty(), clazz.getSimpleName() + "\n  " + String.join("\n  ", problems));
    assertTrue(
        object.getAdditionalProperties().isEmpty(),
        "not mapped: " + object.getAdditionalProperties().keySet());
  }

  /** A valid value for a property of the schema. */
  private static JsonNode sample(JsonNode property) {
    switch (property.path("type").asText()) {
      case "boolean":
        return JSON.booleanNode(true);
      case "integer":
        return JSON.numberNode(1);
      case "array":
        ArrayNode array = JSON.arrayNode();
        array.add(sample(property.get("items")));
        return array;
      case "object":
        return property.has("schema")
            ? JSON.objectNode().put("id", "https://oparl.example.org/embedded/1")
            : JSON.objectNode().put("type", "Point");
      default:
        return switch (property.path("format").asText("")) {
          case "date-time" -> JSON.textNode("2024-01-02T03:04:05+01:00");
          case "date" -> JSON.textNode("2024-01-02");
          case "url" -> JSON.textNode("https://oparl.example.org/1");
          default -> JSON.textNode("value");
        };
    }
  }

  private Map<String, Method> getters(Class<?> clazz) {
    BeanDescription description =
        this.mapper.getSerializationConfig().introspect(this.mapper.constructType(clazz));
    return description.findProperties().stream()
        .filter(BeanPropertyDefinition::hasGetter)
        .collect(
            Collectors.toMap(
                BeanPropertyDefinition::getName,
                property -> (Method) ((AnnotatedMember) property.getGetter()).getMember()));
  }

  private JsonNode readSchema(String type) throws IOException {
    try (InputStream in =
        SchemaValuesTest.class.getResourceAsStream("/schema/1.1/" + type + ".json")) {
      assertNotNull(in, "schema " + type + " not found");
      return this.mapper.readTree(in);
    }
  }

  /** The properties of a schema; {@code JsonNode.properties()} requires Jackson 2.15. */
  private Map<String, JsonNode> propertiesOf(JsonNode schema) {
    return this.mapper.convertValue(
        schema.get("properties"), new TypeReference<Map<String, JsonNode>>() {});
  }
}
