package com.sitepark.oparlclient.v1.objects;

import com.sitepark.oparlclient.core.OparlList;
import com.sitepark.oparlclient.core.OparlReference;
import java.net.URI;
import java.util.List;

/**
 * Ein {@code oparl:System}-Objekt repräsentiert eine OParl-Schnittstelle für eine bestimmte
 * OParl-Version. Es ist außerdem der Startpunkt für Clients beim Zugriff auf einen Server.
 *
 * <p>Möchte ein Server mehrere zueinander inkompatible OParl-Versionen unterstützen, dann
 * <b>muss</b> der Server für jede Version eine eigenen OParl-Schnittstelle mit einem eigenen {@code
 * System}-Objekt ausgeben.
 */
public class OparlSystem extends OparlObjectV1 {
  /**
   * URL der Website des Softwareanbieters, von dem die OParl-Server-Software stammt.
   */
  private URI vendor;

  /**
   * Name der Ansprechpartnerin bzw. des Ansprechpartners oder der Abteilung, die über die in {@code
   * contactEmail} angegebene Adresse erreicht werden kann.
   */
  private String contactName;

  /**
   * Die URL der OParl-Spezifikation, die von diesem Server unterstützt wird. Aktuell kommt hier nur
   * ein Wert in Frage. Mit zukünftigen OParl-Versionen kommen weitere mögliche URLs hinzu. Wert:
   * {@code https://schema.oparl.org/1.1/}
   */
  private String oparlVersion;

  /**
   * URL zu Informationen über die auf dem System genutzte OParl-Server-Software
   */
  private URI product;

  /**
   * URL der Website des parlamentarischen Informationssystems
   */
  private URI website;

  /**
   * Dient der Angabe von System-Objekten mit anderen OParl-Versionen.
   */
  private List<OparlReference<OparlSystem>> otherOparlVersions;

  /**
   * Link zur Objektliste mit allen Körperschaften, die auf dem System existieren.
   */
  private OparlReference<OparlList<OparlBody>> body;

  /**
   * Nutzerfreundlicher Name für das System, mit dessen Hilfe Nutzerinnen und Nutzer das System
   * erkennen und von anderen unterscheiden können.
   */
  private String name;

  /**
   * E-Mail-Adresse für Anfragen zur OParl-API. Die Angabe einer E-Mail-Adresse dient sowohl
   * NutzerInnen wie auch Entwicklerinnen von Clients zur Kontaktaufnahme mit dem Betreiber.
   */
  private String contactEmail;

  public URI getVendor() {
    return this.vendor;
  }

  public String getContactName() {
    return this.contactName;
  }

  public String getOparlVersion() {
    return this.oparlVersion;
  }

  public URI getProduct() {
    return this.product;
  }

  public URI getWebsite() {
    return this.website;
  }

  public List<OparlReference<OparlSystem>> getOtherOparlVersions() {
    return this.otherOparlVersions;
  }

  public OparlReference<OparlList<OparlBody>> getBody() {
    return this.body;
  }

  public String getName() {
    return this.name;
  }

  public String getContactEmail() {
    return this.contactEmail;
  }

  public void setVendor(URI vendor) {
    this.vendor = vendor;
  }

  public void setContactName(String contactName) {
    this.contactName = contactName;
  }

  public void setOparlVersion(String oparlVersion) {
    this.oparlVersion = oparlVersion;
  }

  public void setProduct(URI product) {
    this.product = product;
  }

  public void setWebsite(URI website) {
    this.website = website;
  }

  public void setOtherOparlVersions(List<OparlReference<OparlSystem>> otherOparlVersions) {
    this.otherOparlVersions = otherOparlVersions;
  }

  public void setBody(OparlReference<OparlList<OparlBody>> body) {
    this.body = body;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setContactEmail(String contactEmail) {
    this.contactEmail = contactEmail;
  }
}
