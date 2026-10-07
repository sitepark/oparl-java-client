package com.sitepark.oparlclient.v1.objects;

import com.sitepark.oparlclient.core.OparlReference;
import com.sitepark.oparlclient.internal.OparlDate;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;

/**
 * Ein Objekt vom Typ {@code oparl:File} repräsentiert eine Datei, beispielsweise eine PDF-Datei,
 * ein RTF- oder ODF-Dokument, und hält Metadaten zu der Datei sowie URLs zum Zugriff auf die Datei
 * bereit.
 *
 * <p>Objekte vom Typ {@code oparl:File} können unter anderem mit Drucksachen ({@code oparl:Paper})
 * oder Sitzungen ({@code oparl:Meeting}) in Beziehung stehen. Dies wird durch die Eigenschaft
 * {@code paper} bzw. {@code meeting} angezeigt. Mehrere Objekte vom Typ {@code oparl:File} können
 * mit einander in direkter Beziehung stehen, z.B. wenn sie den selben Inhalt in unterschiedlichen
 * technischen Formaten wiedergeben. Hierfür werden die Eigenschaften {@code masterFile} bzw. {@code
 * derivativeFile} eingesetzt. Das gezeigte Beispiel-Objekt repräsentiert eine PDF-Datei (zu
 * erkennen an der Eigenschaft {@code mimeType}) und zeigt außerdem über die Eigenschaft {@code
 * masterFile} an, von welcher anderen Datei es abgeleitet wurde. Umgekehrt <b>kann</b> über die
 * Eigenschaft {@code derivativeFile} angezeigt werden, welche Ableitungen einer Datei existieren.
 */
public class OparlFile extends OparlObjectV1 {
  /**
   * Größe der Datei in Bytes.
   */
  private Long size;

  /**
   * Datum, welches als Startpunkt für Fristen u.ä. verwendet ist.
   */
  @OparlDate private LocalDate date;

  /**
   * Datei, von der das aktuelle Objekt abgeleitet wurde. Details dazu in der allgemeinen
   * Beschreibung weiter oben.
   */
  private OparlReference<OparlFile> masterFile;

  /**
   * URL zum allgemeinen Zugriff auf die Datei. Näheres unter Dateizugriffe.
   */
  private URI accessUrl;

  /**
   * Dateiname, unter dem die Datei in einem Dateisystem gespeichert werden kann. Beispiel:
   * "einedatei.pdf". Da der Name den kompletten Unicode-Zeichenumfang nutzen kann, <b>sollten</b>
   * Clients ggfs. selbst dafür sorgen, diesen beim Speichern in ein Dateisystem den lokalen
   * Erfordernissen anzupassen.
   */
  private String fileName;

  /**
   * Rückreferenzen auf AgendaItem-Objekte. Wird nur dann ausgegeben, wenn das File-Objekt nicht als
   * eingebettetes Objekt aufgerufen wird.
   */
  private List<OparlReference<OparlAgendaItem>> agendaItem;

  /**
   * Dateien, die von dem aktuellen Objekt abgeleitet wurden. Details dazu in der allgemeinen
   * Beschreibung weiter oben.
   */
  private List<OparlReference<OparlFile>> derivativeFile;

  /**
   * Lizenz, unter der die Datei angeboten wird. Wenn diese Eigenschaft nicht verwendet wird, ist
   * der Wert von {@code license} beziehungsweise die Lizenz eines übergeordneten Objektes
   * maßgeblich. Siehe license
   */
  private URI fileLicense;

  /**
   * Rückreferenzen auf Meeting-Objekte. Wird nur dann ausgegeben, wenn das File-Objekt nicht als
   * eingebettetes Objekt aufgerufen wird.
   */
  private List<OparlReference<OparlMeeting>> meeting;

  /**
   * Externe URL, welche eine zusätzliche Zugriffsmöglichkeit bietet. Beispiel: YouTube-Video.
   */
  private URI externalServiceUrl;

  /**
   * URL zum Download der Datei. Näheres unter Dateizugriffe.
   */
  private URI downloadUrl;

  /**
   * Reine Text-Wiedergabe des Dateiinhalts, sofern dieser in Textform wiedergegeben werden kann.
   */
  private String text;

  /**
   * Rückreferenzen auf Paper-Objekte. Wird nur dann ausgegeben, wenn das File-Objekt nicht als
   * eingebettetes Objekt aufgerufen wird.
   */
  private List<OparlReference<OparlPaper>> paper;

  /**
   * MIME-Type der Datei (vgl. RFC2046: <a
   * href="http://tools.ietf.org/html/rfc2046">http://tools.ietf.org/html/rfc2046</a>).
   */
  private String mimeType;

  /**
   * [Veraltet] SHA1-Prüfsumme des Dateiinhalts in Hexadezimal-Schreibweise. Sollte nicht mehr
   * verwendet werden, da sha1 als unsicher gilt. Stattdessen sollte {@code sha512checksum}
   * verwendet werden.
   */
  private String sha1Checksum;

  /**
   * Ein zur Anzeige für Endnutzer bestimmter Name für dieses Objekt. Leerzeichen <b>dürfen</b>
   * enthalten sein, Datei-Endungen wie ".pdf" <b>sollten nicht</b> enthalten sein.
   */
  private String name;

  /**
   * SHA512-Prüfsumme des Dateiinhalts in Hexadezimal-Schreibweise.
   */
  private String sha512Checksum;

  public Long getSize() {
    return this.size;
  }

  public LocalDate getDate() {
    return this.date;
  }

  public OparlReference<OparlFile> getMasterFile() {
    return this.masterFile;
  }

  public URI getAccessUrl() {
    return this.accessUrl;
  }

  public String getFileName() {
    return this.fileName;
  }

  public List<OparlReference<OparlAgendaItem>> getAgendaItem() {
    return this.agendaItem;
  }

  public List<OparlReference<OparlFile>> getDerivativeFile() {
    return this.derivativeFile;
  }

  public URI getFileLicense() {
    return this.fileLicense;
  }

  public List<OparlReference<OparlMeeting>> getMeeting() {
    return this.meeting;
  }

  public URI getExternalServiceUrl() {
    return this.externalServiceUrl;
  }

  public URI getDownloadUrl() {
    return this.downloadUrl;
  }

  public String getText() {
    return this.text;
  }

  public List<OparlReference<OparlPaper>> getPaper() {
    return this.paper;
  }

  public String getMimeType() {
    return this.mimeType;
  }

  public String getSha1Checksum() {
    return this.sha1Checksum;
  }

  public String getName() {
    return this.name;
  }

  public String getSha512Checksum() {
    return this.sha512Checksum;
  }

  public void setSize(Long size) {
    this.size = size;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }

  public void setMasterFile(OparlReference<OparlFile> masterFile) {
    this.masterFile = masterFile;
  }

  public void setAccessUrl(URI accessUrl) {
    this.accessUrl = accessUrl;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public void setAgendaItem(List<OparlReference<OparlAgendaItem>> agendaItem) {
    this.agendaItem = agendaItem;
  }

  public void setDerivativeFile(List<OparlReference<OparlFile>> derivativeFile) {
    this.derivativeFile = derivativeFile;
  }

  public void setFileLicense(URI fileLicense) {
    this.fileLicense = fileLicense;
  }

  public void setMeeting(List<OparlReference<OparlMeeting>> meeting) {
    this.meeting = meeting;
  }

  public void setExternalServiceUrl(URI externalServiceUrl) {
    this.externalServiceUrl = externalServiceUrl;
  }

  public void setDownloadUrl(URI downloadUrl) {
    this.downloadUrl = downloadUrl;
  }

  public void setText(String text) {
    this.text = text;
  }

  public void setPaper(List<OparlReference<OparlPaper>> paper) {
    this.paper = paper;
  }

  public void setMimeType(String mimeType) {
    this.mimeType = mimeType;
  }

  public void setSha1Checksum(String sha1Checksum) {
    this.sha1Checksum = sha1Checksum;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setSha512Checksum(String sha512Checksum) {
    this.sha512Checksum = sha512Checksum;
  }
}
