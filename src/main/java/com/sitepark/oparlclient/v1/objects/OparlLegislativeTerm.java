package com.sitepark.oparlclient.v1.objects;

import com.sitepark.oparlclient.core.OparlReference;
import com.sitepark.oparlclient.internal.OparlDate;
import java.time.LocalDate;

/**
 * Dieser Objekttyp dient der Beschreibung einer Wahlperiode.
 */
public class OparlLegislativeTerm extends OparlObjectV1 {
  /**
   * Rückreferenz auf die Körperschaft, welche nur dann ausgegeben werden muss, wenn das
   * LegislativeTerm-Objekt einzeln abgerufen wird, d.h. nicht Teil einer internen Ausgabe ist.
   */
  private OparlReference<OparlBody> body;

  /**
   * Der erste Tag der Wahlperiode.
   */
  @OparlDate private LocalDate startDate;

  /**
   * Nutzerfreundliche Bezeichnung der Wahlperiode.
   */
  private String name;

  /**
   * Der letzte Tag der Wahlperiode.
   */
  @OparlDate private LocalDate endDate;

  public OparlReference<OparlBody> getBody() {
    return this.body;
  }

  public LocalDate getStartDate() {
    return this.startDate;
  }

  public String getName() {
    return this.name;
  }

  public LocalDate getEndDate() {
    return this.endDate;
  }

  public void setBody(OparlReference<OparlBody> body) {
    this.body = body;
  }

  public void setStartDate(LocalDate startDate) {
    this.startDate = startDate;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setEndDate(LocalDate endDate) {
    this.endDate = endDate;
  }
}
