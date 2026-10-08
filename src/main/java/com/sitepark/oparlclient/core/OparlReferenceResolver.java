package com.sitepark.oparlclient.core;

import com.fasterxml.jackson.core.type.TypeReference;
import java.net.URI;
import java.util.concurrent.CompletableFuture;

/** Requests the object behind a URL; implemented by the {@code OparlClient}. */
// the method is generic, so the interface can not be implemented by a lambda
@SuppressWarnings("PMD.ImplicitFunctionalInterface")
public interface OparlReferenceResolver {

  /**
   * Requests the object at the given URL and maps it to the given type.
   *
   * @param uri the URL of the object
   * @param typeReference the type to map the object to
   * @return the object
   */
  public <R> CompletableFuture<R> getAsync(URI uri, TypeReference<R> typeReference);
}
