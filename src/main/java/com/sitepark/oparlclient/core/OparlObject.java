package com.sitepark.oparlclient.core;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
// the specification recommends to leave out properties without value
@JsonInclude(JsonInclude.Include.NON_NULL)
@SuppressWarnings("PMD.AbstractClassWithoutAbstractMethod") // only the subclasses are mapped
public abstract class OparlObject {

  private Map<String, JsonNode> additionalProperties;

  /**
   * Returns all properties of the JSON object that are not mapped to a field, e.g. vendor-specific
   * extensions like {@code "Hersteller:faxNumber"} (see "Herstellerspezifische Erweiterungen" in
   * the OParl specification). They are written back when the object is serialized.
   *
   * <p>Values of mapped properties that can not be mapped, e.g. a URL where the specification
   * requires an embedded object, are kept here as well with their original value; the mapped
   * property then leaves out the invalid elements of a list, or is {@code null}.
   */
  @JsonAnyGetter
  public Map<String, JsonNode> getAdditionalProperties() {
    return this.additionalProperties != null
        ? Collections.unmodifiableMap(this.additionalProperties)
        : Collections.emptyMap();
  }

  /**
   * Returns a property that is not mapped to a field, see {@link #getAdditionalProperties()}.
   *
   * @return the value, or {@code null} if the JSON object did not contain the property
   */
  public JsonNode getAdditionalProperty(String name) {
    return this.additionalProperties != null ? this.additionalProperties.get(name) : null;
  }

  @JsonAnySetter
  public void setAdditionalProperty(String name, JsonNode value) {
    if (this.additionalProperties == null) {
      this.additionalProperties = new LinkedHashMap<>();
    }
    this.additionalProperties.put(name, value);
  }
}
