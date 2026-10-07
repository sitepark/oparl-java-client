package com.sitepark.oparlclient.v1.objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sitepark.oparlclient.core.OparlReference;
import com.sitepark.oparlclient.internal.OparlDateTime;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Tagesordnungspunkte sind die Bestandteile von Sitzungen ({@code oparl:Meeting}). Jeder
 * Tagesordnungspunkt widmet sich inhaltlich einem bestimmten Thema, wozu in der Regel auch die
 * Beratung bestimmter Drucksachen gehört.
 *
 * <p>Die Beziehung zwischen einem Tagesordnungspunkt und einer Drucksache wird über ein Objekt vom
 * Typ {@code oparl:Consultation} hergestellt, das über die Eigenschaft {@code consultation}
 * referenziert werden kann.
 */
public class OparlAgendaItem extends OparlObjectV1 {
  /**
   * Falls in diesem Tagesordnungspunkt ein Beschluss gefasst wurde, kann hier ein Text angegeben
   * werden. Das ist besonders dann in der Praxis relevant, wenn der gefasste Beschluss (z. B. durch
   * Änderungsantrag) von der Beschlussvorlage abweicht.
   */
  private String resolutionText;

  /**
   * Neu in OParl 1.1: Die Position des Tagesordnungspunkts in der Sitzung, wenn alle
   * Tagesordnungspunkte von 0 an durchgehend numeriert werden. Diese Nummer entspricht der Position
   * in {@code Meeting:agendaItem}
   */
  private Integer order;

  /**
   * Datum und Uhrzeit des Anfangszeitpunkts des Tagesordnungspunktes. Bei zukünftigen
   * Tagesordnungspunkten ist dies der geplante Zeitpunkt, bei einem stattgefundenen <b>kann</b> es
   * der tatsächliche Startzeitpunkt sein.
   */
  @OparlDateTime private OffsetDateTime start;

  /**
   * Beratung, die diesem Tagesordnungspunkt zugewiesen ist.
   */
  private OparlReference<OparlConsultation> consultation;

  /**
   * Gliederungs-"Nummer" des Tagesordnungspunktes. Eine beliebige Zeichenkette, wie z. B. "10.",
   * "10.1", "C", "c)" o. ä. Die Reihenfolge wird nicht dadurch, sondern durch die Reihenfolge der
   * TOPs im {@code agendaItem}-Attribut von {@code oparl:Meeting} festgelegt, <b>sollte</b>
   * allerdings zu dieser identisch sein.
   */
  private String number;

  /**
   * Weitere Dateianhänge zum Tagesordnungspunkt.
   */
  private List<OparlFile> auxiliaryFile;

  /**
   * Falls in diesem Tagesordnungspunkt ein Beschluss gefasst wurde, kann hier eine Datei angegeben
   * werden. Das ist besonders dann in der Praxis relevant, wenn der gefasste Beschluss (z. B. durch
   * Änderungsantrag) von der Beschlussvorlage abweicht.
   */
  private OparlFile resolutionFile;

  /**
   * Kennzeichnet, ob der Tagesordnungspunkt zur Behandlung in öffentlicher Sitzung vorgesehen
   * ist/war. Es wird ein Wahrheitswert ({@code true} oder {@code false}) erwartet.
   */
  @JsonProperty("public")
  private Boolean publicField;

  /**
   * Das Thema des Tagesordnungspunktes.
   */
  private String name;

  /**
   * Rückreferenz auf das Meeting, welches nur dann ausgegeben werden muss, wenn das
   * agendaItem-Objekt einzeln abgerufen wird, d.h. nicht Teil einer internen Ausgabe ist.
   */
  private OparlReference<OparlMeeting> meeting;

  /**
   * Endzeitpunkt des Tagesordnungspunktes als Datum/Uhrzeit. Bei zukünftigen Tagesordnungspunkten
   * ist dies der geplante Zeitpunkt, bei einer stattgefundenen <b>kann</b> es der tatsächliche
   * Endzeitpunkt sein.
   */
  @OparlDateTime private OffsetDateTime end;

  /**
   * Kategorische Information darüber, welches Ergebnis die Beratung des Tagesordnungspunktes
   * erbracht hat, in der Bedeutung etwa "Unverändert beschlossen" oder "Geändert beschlossen".
   */
  private String result;

  public String getResolutionText() {
    return this.resolutionText;
  }

  public Integer getOrder() {
    return this.order;
  }

  public OffsetDateTime getStart() {
    return this.start;
  }

  public OparlReference<OparlConsultation> getConsultation() {
    return this.consultation;
  }

  public String getNumber() {
    return this.number;
  }

  public List<OparlFile> getAuxiliaryFile() {
    return this.auxiliaryFile;
  }

  public OparlFile getResolutionFile() {
    return this.resolutionFile;
  }

  /**
   * Kennzeichnet, ob der Tagesordnungspunkt zur Behandlung in öffentlicher Sitzung vorgesehen
   * ist/war (JSON-Property {@code public}).
   */
  public Boolean getPublic() {
    return this.publicField;
  }

  public String getName() {
    return this.name;
  }

  public OparlReference<OparlMeeting> getMeeting() {
    return this.meeting;
  }

  public OffsetDateTime getEnd() {
    return this.end;
  }

  public String getResult() {
    return this.result;
  }

  public void setResolutionText(String resolutionText) {
    this.resolutionText = resolutionText;
  }

  public void setOrder(Integer order) {
    this.order = order;
  }

  public void setStart(OffsetDateTime start) {
    this.start = start;
  }

  public void setConsultation(OparlReference<OparlConsultation> consultation) {
    this.consultation = consultation;
  }

  public void setNumber(String number) {
    this.number = number;
  }

  public void setAuxiliaryFile(List<OparlFile> auxiliaryFile) {
    this.auxiliaryFile = auxiliaryFile;
  }

  public void setResolutionFile(OparlFile resolutionFile) {
    this.resolutionFile = resolutionFile;
  }

  public void setPublic(Boolean publicField) {
    this.publicField = publicField;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setMeeting(OparlReference<OparlMeeting> meeting) {
    this.meeting = meeting;
  }

  public void setEnd(OffsetDateTime end) {
    this.end = end;
  }

  public void setResult(String result) {
    this.result = result;
  }
}
