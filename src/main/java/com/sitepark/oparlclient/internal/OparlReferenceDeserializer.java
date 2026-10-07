package com.sitepark.oparlclient.internal;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.sitepark.oparlclient.core.OparlReference;
import com.sitepark.oparlclient.core.OparlReferenceResolver;
import java.io.IOException;
import java.net.URI;

public class OparlReferenceDeserializer extends JsonDeserializer<OparlReference<?>>
    implements ContextualDeserializer {

  private JavaType type;

  @Override
  public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) {
    OparlReferenceDeserializer oparlReferenceDeserializer = new OparlReferenceDeserializer();
    oparlReferenceDeserializer.type = this.findReferencedType(ctxt, property);
    return oparlReferenceDeserializer;
  }

  /**
   * Determines {@code T} of the {@code OparlReference<T>} being deserialized. The contextual type
   * also covers references inside collections and root values; the property type is only a
   * fallback.
   */
  private JavaType findReferencedType(DeserializationContext ctxt, BeanProperty property) {
    JavaType referenceType = ctxt.getContextualType();
    if (referenceType == null && property != null) {
      referenceType = property.getType();
    }
    while (referenceType != null
        && !referenceType.hasRawClass(OparlReference.class)
        && referenceType.getContentType() != null) {
      referenceType = referenceType.getContentType();
    }
    JavaType referencedType =
        referenceType != null && referenceType.hasRawClass(OparlReference.class)
            ? referenceType.containedType(0)
            : null;
    return referencedType != null ? referencedType : ctxt.constructType(Object.class);
  }

  @Override
  // the non-deprecated variant of findInjectableValue requires Jackson 2.20; keep compatibility
  // with the Jackson version of the application using this library
  @SuppressWarnings("deprecation")
  public OparlReference<?> deserialize(
      JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
    JsonNode node = jsonParser.getCodec().readTree(jsonParser);
    URI uri = LenientUriDeserializer.parse(this.urlOf(node, deserializationContext));
    if (uri == null) {
      return null;
    }
    OparlReferenceResolver resolver =
        (OparlReferenceResolver)
            deserializationContext.findInjectableValue(
                OparlReferenceResolver.class.getName(), null, null);
    return new OparlReference<>(uri, this.type, resolver);
  }

  /**
   * Returns the URL of a reference: the string itself or, if the server embedded the object instead
   * of referencing it, the {@code id} of that object.
   */
  private String urlOf(JsonNode node, DeserializationContext ctxt) throws IOException {
    if (node.isTextual()) {
      return node.textValue();
    }
    if (node.isObject() && node.path("id").isTextual()) {
      return node.get("id").textValue();
    }
    return ctxt.reportInputMismatch(
        this,
        "Expected a URL or an object with an \"id\" for a reference, but got %s",
        node.getNodeType());
  }
}
