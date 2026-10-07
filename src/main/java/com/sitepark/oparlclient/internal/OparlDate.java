package com.sitepark.oparlclient.internal;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a {@code LocalDate} property as OParl {@code date}. It is read tolerantly and written as
 * {@code yyyy-mm-dd}, with any {@code ObjectMapper}, see {@link OparlTimeSerialization}.
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonSerialize(using = OparlTimeSerialization.DateSerializer.class)
@JsonDeserialize(using = OparlTimeSerialization.DateDeserializer.class)
public @interface OparlDate {}
