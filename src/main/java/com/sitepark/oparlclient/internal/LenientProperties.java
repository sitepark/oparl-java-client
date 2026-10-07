package com.sitepark.oparlclient.internal;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBuilder;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.sitepark.oparlclient.core.OparlObject;
import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Tolerates property values of OParl objects that can not be mapped, e.g. a URL where the
 * specification requires an embedded object, a number where a reference is expected, or a URL that
 * can not be parsed. Such values do not fail the whole object or list page:
 *
 * <ul>
 *   <li>a single value leaves the property {@code null},
 *   <li>invalid elements of a list are left out, the valid ones are kept,
 *   <li>the original value is kept as additional property of the object, see {@link
 *       OparlObject#getAdditionalProperties()}, so no information is lost and it is written back
 *       when the object is serialized.
 * </ul>
 */
public class LenientProperties extends BeanDeserializerModifier {

  private static final long serialVersionUID = 1L;

  private static final Logger LOGGER = System.getLogger(LenientProperties.class.getName());

  private static final int MAX_LOGGED_MESSAGE_LENGTH = 200;

  @Override
  public BeanDeserializerBuilder updateBuilder(
      DeserializationConfig config, BeanDescription beanDesc, BeanDeserializerBuilder builder) {
    if (!OparlObject.class.isAssignableFrom(beanDesc.getBeanClass())) {
      return builder;
    }
    List<SettableBeanProperty> replacements = new ArrayList<>();
    for (Iterator<SettableBeanProperty> it = builder.getProperties(); it.hasNext(); ) {
      replacements.add(new LenientProperty(it.next()));
    }
    for (SettableBeanProperty replacement : replacements) {
      builder.addOrReplaceProperty(replacement, true);
    }
    return builder;
  }

  /** A property that keeps values it can not map as additional property. */
  private static final class LenientProperty extends SettableBeanProperty.Delegating {

    private static final long serialVersionUID = 1L;

    LenientProperty(SettableBeanProperty delegate) {
      super(delegate);
    }

    @Override
    protected SettableBeanProperty withDelegate(SettableBeanProperty delegate) {
      return new LenientProperty(delegate);
    }

    @Override
    public void deserializeAndSet(JsonParser parser, DeserializationContext ctxt, Object instance)
        throws IOException {
      // buffered, so the value can be mapped again element by element if it fails as a whole
      // readTree clears the current token, so the original parser must not be used afterwards
      JsonNode value = parser.getCodec().readTree(parser);
      if (value == null || value.isNull() || value.isMissingNode()) {
        this.delegate.set(instance, this.map(JsonNodeFactory.instance.nullNode(), parser, ctxt));
        return;
      }
      Object mapped = this.mapOrNull(value, parser, ctxt, instance);
      if (isComplete(mapped)) {
        this.delegate.set(instance, mapped);
        return;
      }
      // the value could not be mapped, or only partly, e.g. an unparsable URL in a list
      ((OparlObject) instance).setAdditionalProperty(this.getName(), value);
      Object valid = this.validElements(value, parser, ctxt);
      if (valid != null) {
        this.delegate.set(instance, valid);
      }
    }

    @Override
    public Object deserializeSetAndReturn(
        JsonParser parser, DeserializationContext ctxt, Object instance) throws IOException {
      this.deserializeAndSet(parser, ctxt, instance);
      return instance;
    }

    /** Maps the value, or returns {@code null} and logs a warning if it can not be mapped. */
    private Object mapOrNull(
        JsonNode value, JsonParser parser, DeserializationContext ctxt, Object instance)
        throws IOException {
      try {
        return this.map(value, parser, ctxt);
      } catch (JsonProcessingException e) {
        LOGGER.log(
            Level.WARNING,
            "Ignoring invalid value of ''{0}'' in {1}, kept as additional property: {2}",
            this.getName(),
            instance.getClass().getSimpleName(),
            shortMessage(e));
        return null;
      }
    }

    private Object map(JsonNode value, JsonParser parser, DeserializationContext ctxt)
        throws IOException {
      try (JsonParser valueParser = value.traverse(parser.getCodec())) {
        valueParser.nextToken();
        return this.delegate.deserialize(valueParser, ctxt);
      }
    }

    /**
     * Maps the elements of a list one by one and returns a list of those that could be mapped, or
     * {@code null} if the value is no list or none of its elements could be mapped.
     */
    private Object validElements(JsonNode value, JsonParser parser, DeserializationContext ctxt)
        throws IOException {
      if (!value.isArray() || !this.getType().isCollectionLikeType()) {
        return null;
      }
      ArrayNode valid = JsonNodeFactory.instance.arrayNode();
      for (JsonNode element : value) {
        ArrayNode single = JsonNodeFactory.instance.arrayNode();
        single.add(element);
        try {
          if (isComplete(this.map(single, parser, ctxt))) {
            valid.add(element);
          }
        } catch (JsonProcessingException e) {
          // left out; the whole value is kept as additional property
        }
      }
      // size() instead of isEmpty(), which requires Jackson 2.10
      return valid.size() == 0 ? null : this.map(valid, parser, ctxt);
    }

    /** Whether a non-null value was mapped without losing parts of it. */
    private static boolean isComplete(Object mapped) {
      if (mapped == null) {
        return false;
      }
      if (mapped instanceof Collection<?> collection) {
        for (Object element : collection) {
          if (element == null) {
            return false;
          }
        }
      }
      return true;
    }

    private static String shortMessage(JsonProcessingException e) {
      String message = e.getOriginalMessage();
      if (message == null) {
        return e.getClass().getSimpleName();
      }
      int lineBreak = message.indexOf('\n');
      if (lineBreak >= 0) {
        message = message.substring(0, lineBreak);
      }
      if (message.length() > MAX_LOGGED_MESSAGE_LENGTH) {
        message = message.substring(0, MAX_LOGGED_MESSAGE_LENGTH) + "…";
      }
      return LogSafe.of(message);
    }
  }
}
