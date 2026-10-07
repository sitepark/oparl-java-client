package com.sitepark.oparlclient.v1.objects;

import com.sitepark.oparlclient.core.OparlList;
import com.sitepark.oparlclient.core.OparlReference;
import com.sitepark.oparlclient.internal.OparlDate;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;

/**
 * Dieser Objekttyp dient dazu, Gruppierungen von Personen abzubilden, die in der parlamentarischen
 * Arbeit eine Rolle spielen. Dazu zählen in der Praxis insbesondere Fraktionen und Gremien.
 */
public class OparlOrganization extends OparlObjectV1 {
  /**
   * Mitgliedschaften dieser Gruppierung.
   */
  private List<OparlReference<OparlMembership>> membership;

  /**
   * Offizielle (lange) Form des Namens der Gruppierung.
   */
  private String name;

  /**
   * URL auf eine externe Objektliste mit den Sitzungen dieser Gruppierung. Invers zur Eigenschaft
   * {@code organization} der Klasse {@code oparl:Meeting}
   */
  private OparlReference<OparlList<OparlMeeting>> meeting;

  /**
   * URL auf eine externe Objektliste mit den Beratungen dieser Gruppierung. Invers zur Eigenschaft
   * {@code organization} der Klasse {@code oparl:Consultation}
   */
  private OparlReference<OparlList<OparlConsultation>> consultation;

  /**
   * Grobe Kategorisierung der Gruppierung. Mögliche Werte sind "Gremium", "Partei", "Fraktion",
   * "Verwaltungsbereich", "externes Gremium", "Institution" und "Sonstiges".
   */
  private String organizationType;

  /**
   * Gründungsdatum der Gruppierung. Kann z. B. das Datum der konstituierenden Sitzung sein.
   */
  @OparlDate private LocalDate startDate;

  /**
   * URL einer eventuellen übergeordneten Gruppierung.
   */
  private OparlReference<OparlOrganization> subOrganizationOf;

  /**
   * Positionen, die für diese Gruppierung vorgesehen sind.
   */
  private List<String> post;

  /**
   * Allgemeine Website der Gruppierung.
   */
  private URI website;

  /**
   * Datum des letzten Tages der Existenz der Gruppierung.
   */
  @OparlDate private LocalDate endDate;

  /**
   * Ort, an dem die Organisation beheimatet ist
   */
  private OparlLocation location;

  /**
   * Der Name der Gruppierung als Kurzform.
   */
  private String shortName;

  /**
   * Körperschaft, zu der diese Gruppierung gehört.
   */
  private OparlReference<OparlBody> body;

  /**
   * Externer OParl Body, der dieser Organisation entspricht. Diese Eigenschaft ist dafür gedacht
   * auf eventuelle konkretere OParl-Schnittstellen zu verweisen. Ein Beispiel hierfür wäre eine
   * Stadt, die sowohl ein übergreifendes parlamentarisches Informationssystem, als auch
   * bezirksspezifische Systeme hat.
   */
  private OparlReference<OparlBody> externalBody;

  /**
   * Die Art der Gruppierung. In Frage kommen z.B. "Parlament", "Ausschuss", "Beirat",
   * "Projektbeirat", "Kommission", "AG", "Verwaltungsrat", "Fraktion" oder "Partei". Die Angabe
   * <b>sollte</b> möglichst präzise erfolgen. Außerdem <b>sollten</b> Abkürzungen vermieden werden.
   * Für die höchste demokratische Instanz in der Kommune <b>sollte</b> immer der Begriff
   * "Parlament" verwendet werden, nicht "Rat" oder "Hauptausschuss".
   */
  private String classification;

  public List<OparlReference<OparlMembership>> getMembership() {
    return this.membership;
  }

  public String getName() {
    return this.name;
  }

  public OparlReference<OparlList<OparlMeeting>> getMeeting() {
    return this.meeting;
  }

  public OparlReference<OparlList<OparlConsultation>> getConsultation() {
    return this.consultation;
  }

  public String getOrganizationType() {
    return this.organizationType;
  }

  public LocalDate getStartDate() {
    return this.startDate;
  }

  public OparlReference<OparlOrganization> getSubOrganizationOf() {
    return this.subOrganizationOf;
  }

  public List<String> getPost() {
    return this.post;
  }

  public URI getWebsite() {
    return this.website;
  }

  public LocalDate getEndDate() {
    return this.endDate;
  }

  public OparlLocation getLocation() {
    return this.location;
  }

  public String getShortName() {
    return this.shortName;
  }

  public OparlReference<OparlBody> getBody() {
    return this.body;
  }

  public OparlReference<OparlBody> getExternalBody() {
    return this.externalBody;
  }

  public String getClassification() {
    return this.classification;
  }

  public void setMembership(List<OparlReference<OparlMembership>> membership) {
    this.membership = membership;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setMeeting(OparlReference<OparlList<OparlMeeting>> meeting) {
    this.meeting = meeting;
  }

  public void setConsultation(OparlReference<OparlList<OparlConsultation>> consultation) {
    this.consultation = consultation;
  }

  public void setOrganizationType(String organizationType) {
    this.organizationType = organizationType;
  }

  public void setStartDate(LocalDate startDate) {
    this.startDate = startDate;
  }

  public void setSubOrganizationOf(OparlReference<OparlOrganization> subOrganizationOf) {
    this.subOrganizationOf = subOrganizationOf;
  }

  public void setPost(List<String> post) {
    this.post = post;
  }

  public void setWebsite(URI website) {
    this.website = website;
  }

  public void setEndDate(LocalDate endDate) {
    this.endDate = endDate;
  }

  public void setLocation(OparlLocation location) {
    this.location = location;
  }

  public void setShortName(String shortName) {
    this.shortName = shortName;
  }

  public void setBody(OparlReference<OparlBody> body) {
    this.body = body;
  }

  public void setExternalBody(OparlReference<OparlBody> externalBody) {
    this.externalBody = externalBody;
  }

  public void setClassification(String classification) {
    this.classification = classification;
  }
}
