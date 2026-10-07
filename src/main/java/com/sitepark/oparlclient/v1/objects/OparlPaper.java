package com.sitepark.oparlclient.v1.objects;

import com.sitepark.oparlclient.core.OparlReference;
import com.sitepark.oparlclient.internal.OparlDate;
import java.time.LocalDate;
import java.util.List;

/**
 * Dieser Objekttyp dient der Abbildung von Drucksachen in der parlamentarischen Arbeit, wie zum
 * Beispiel Anfragen, Anträgen und Beschlussvorlagen. Drucksachen werden in Form einer Beratung
 * (oparl:Consultation) im Rahmen eines Tagesordnungspunkts (oparl:AgendaItem) einer Sitzung
 * (oparl:Meeting) behandelt.
 *
 * <p>Drucksachen spielen in der schriftlichen wie mündlichen Kommunikation eine besondere Rolle, da
 * in vielen Texten auf bestimmte Drucksachen Bezug genommen wird. Hierbei kommen in
 * parlamentarischen Informationssystemen in der Regel unveränderliche Kennungen der Drucksachen zum
 * Einsatz.
 */
public class OparlPaper extends OparlObjectV1 {
  /**
   * Untergeordnete Drucksachen.
   */
  private List<OparlReference<OparlPaper>> subordinatedPaper;

  /**
   * Urheber der Drucksache, falls der Urheber eine Person ist. Es können auch mehrere Personen
   * angegeben werden.
   */
  private List<OparlReference<OparlPerson>> originatorPerson;

  /**
   * Übergeordnete Drucksachen.
   */
  private List<OparlReference<OparlPaper>> superordinatedPaper;

  /**
   * Körperschaft, zu der die Drucksache gehört.
   */
  private OparlReference<OparlBody> body;

  /**
   * Titel der Drucksache.
   */
  private String name;

  /**
   * Datum, welches als Startpunkt für Fristen u.ä. verwendet ist.
   */
  @OparlDate private LocalDate date;

  /**
   * Inhaltlich verwandte Drucksachen.
   */
  private List<OparlReference<OparlPaper>> relatedPaper;

  /**
   * Alle weiteren Dateien zur Drucksache ausgenommen der gegebenenfalls in {@code mainFile}
   * angegebenen.
   */
  private List<OparlFile> auxiliaryFile;

  /**
   * Beratungen der Drucksache.
   */
  private List<OparlConsultation> consultation;

  /**
   * Die Hauptdatei zu dieser Drucksache. Beispiel: Die Drucksache repräsentiert eine
   * Beschlussvorlage und die Hauptdatei enthält den Text der Beschlussvorlage. Sollte keine
   * eindeutige Hauptdatei vorhanden sein, wird diese Eigenschaft nicht ausgegeben.
   */
  private OparlFile mainFile;

  /**
   * Federführung. Amt oder Abteilung, für die Inhalte oder Beantwortung der Drucksache
   * verantwortlich.
   */
  private List<OparlReference<OparlOrganization>> underDirectionOf;

  /**
   * Art der Drucksache, z. B. Beantwortung einer Anfrage.
   */
  private String paperType;

  /**
   * Kennung bzw. Aktenzeichen der Drucksache, mit der sie in der parlamentarischen Arbeit eindeutig
   * referenziert werden kann.
   */
  private String reference;

  /**
   * Sofern die Drucksache einen inhaltlichen Ortsbezug hat, beschreibt diese Eigenschaft den Ort in
   * Textform und/oder in Form von Geodaten.
   */
  private List<OparlLocation> location;

  /**
   * Urheber der Drucksache, falls der Urheber eine Gruppierung ist. Es können auch mehrere
   * Gruppierungen angegeben werden.
   */
  private List<OparlReference<OparlOrganization>> originatorOrganization;

  public List<OparlReference<OparlPaper>> getSubordinatedPaper() {
    return this.subordinatedPaper;
  }

  public List<OparlReference<OparlPerson>> getOriginatorPerson() {
    return this.originatorPerson;
  }

  public List<OparlReference<OparlPaper>> getSuperordinatedPaper() {
    return this.superordinatedPaper;
  }

  public OparlReference<OparlBody> getBody() {
    return this.body;
  }

  public String getName() {
    return this.name;
  }

  public LocalDate getDate() {
    return this.date;
  }

  public List<OparlReference<OparlPaper>> getRelatedPaper() {
    return this.relatedPaper;
  }

  public List<OparlFile> getAuxiliaryFile() {
    return this.auxiliaryFile;
  }

  public List<OparlConsultation> getConsultation() {
    return this.consultation;
  }

  public OparlFile getMainFile() {
    return this.mainFile;
  }

  public List<OparlReference<OparlOrganization>> getUnderDirectionOf() {
    return this.underDirectionOf;
  }

  public String getPaperType() {
    return this.paperType;
  }

  public String getReference() {
    return this.reference;
  }

  public List<OparlLocation> getLocation() {
    return this.location;
  }

  public List<OparlReference<OparlOrganization>> getOriginatorOrganization() {
    return this.originatorOrganization;
  }

  public void setSubordinatedPaper(List<OparlReference<OparlPaper>> subordinatedPaper) {
    this.subordinatedPaper = subordinatedPaper;
  }

  public void setOriginatorPerson(List<OparlReference<OparlPerson>> originatorPerson) {
    this.originatorPerson = originatorPerson;
  }

  public void setSuperordinatedPaper(List<OparlReference<OparlPaper>> superordinatedPaper) {
    this.superordinatedPaper = superordinatedPaper;
  }

  public void setBody(OparlReference<OparlBody> body) {
    this.body = body;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }

  public void setRelatedPaper(List<OparlReference<OparlPaper>> relatedPaper) {
    this.relatedPaper = relatedPaper;
  }

  public void setAuxiliaryFile(List<OparlFile> auxiliaryFile) {
    this.auxiliaryFile = auxiliaryFile;
  }

  public void setConsultation(List<OparlConsultation> consultation) {
    this.consultation = consultation;
  }

  public void setMainFile(OparlFile mainFile) {
    this.mainFile = mainFile;
  }

  public void setUnderDirectionOf(List<OparlReference<OparlOrganization>> underDirectionOf) {
    this.underDirectionOf = underDirectionOf;
  }

  public void setPaperType(String paperType) {
    this.paperType = paperType;
  }

  public void setReference(String reference) {
    this.reference = reference;
  }

  public void setLocation(List<OparlLocation> location) {
    this.location = location;
  }

  public void setOriginatorOrganization(
      List<OparlReference<OparlOrganization>> originatorOrganization) {
    this.originatorOrganization = originatorOrganization;
  }
}
