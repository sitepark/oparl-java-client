package com.sitepark.oparlclient.v1.objects;

import com.sitepark.oparlclient.core.OparlObject;
import com.sitepark.oparlclient.internal.OparlDateTime;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;

public class OparlObjectV1 extends OparlObject {
  private URI id;

  private String type;

  private boolean deleted;

  @OparlDateTime private OffsetDateTime modified;

  private URI web;

  @OparlDateTime private OffsetDateTime created;

  /** Schlagworte zur optionalen Kategorisierung des Objekts. */
  private List<String> keyword;

  /**
   * Lizenz, unter der die Daten des Objekts stehen. Wird {@code license} am {@code System} oder am
   * {@code Body} verwendet, gilt sie für alle Objekte des Systems bzw. der Körperschaft, sofern das
   * einzelne Objekt keine andere angibt. In der Regel eine URL; das Schema erlaubt bei den meisten
   * Objekttypen aber beliebige Zeichenketten.
   */
  private String license;

  public URI getId() {
    return this.id;
  }

  public String getType() {
    return this.type;
  }

  /**
   * Returns whether the object has been deleted. Unlike the other properties, which are {@code
   * null} if the server did not send them, this one is {@code false} then, since the specification
   * only marks deleted objects.
   */
  public boolean isDeleted() {
    return this.deleted;
  }

  public OffsetDateTime getModified() {
    return this.modified;
  }

  public URI getWeb() {
    return this.web;
  }

  public OffsetDateTime getCreated() {
    return this.created;
  }

  public List<String> getKeyword() {
    return this.keyword;
  }

  public String getLicense() {
    return this.license;
  }

  public void setId(URI id) {
    this.id = id;
  }

  public void setType(String type) {
    this.type = type;
  }

  public void setDeleted(boolean deleted) {
    this.deleted = deleted;
  }

  public void setModified(OffsetDateTime modified) {
    this.modified = modified;
  }

  public void setWeb(URI web) {
    this.web = web;
  }

  public void setCreated(OffsetDateTime created) {
    this.created = created;
  }

  public void setKeyword(List<String> keyword) {
    this.keyword = keyword;
  }

  public void setLicense(String license) {
    this.license = license;
  }

  /**
   * Two objects are equal if they are of the same class and have the same {@code id}, the URL that
   * identifies an object in OParl. The other properties are not compared: two requests of the same
   * object are equal even if the object was modified in between; compare {@link #getModified()} to
   * detect changes. An object without {@code id} is only equal to itself.
   *
   * <p>Do not change the {@code id} while the object is in a {@code Set} or is a key of a {@code
   * Map}.
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || this.getClass() != o.getClass() || this.id == null) {
      return false;
    }
    return this.id.equals(((OparlObjectV1) o).id);
  }

  @Override
  public int hashCode() {
    return this.id != null ? this.id.hashCode() : System.identityHashCode(this);
  }

  /** Returns the class name, the {@code id} and whether the object is deleted. */
  @Override
  public String toString() {
    return getClass().getSimpleName() + "[" + this.id + (this.deleted ? ", deleted" : "") + "]";
  }
}
