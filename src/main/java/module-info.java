/**
 * Java client for OParl 1.0 and 1.1 servers. The package {@code com.sitepark.oparlclient.internal}
 * is not exported.
 */
module com.sitepark.oparlclient {
  requires transitive java.net.http;
  // used directly, so declared even though databind requires them transitively
  requires transitive com.fasterxml.jackson.annotation;
  requires transitive com.fasterxml.jackson.core;
  requires transitive com.fasterxml.jackson.databind;

  exports com.sitepark.oparlclient;
  exports com.sitepark.oparlclient.core;
  exports com.sitepark.oparlclient.core.query;
  exports com.sitepark.oparlclient.v1.objects;

  // Jackson maps the objects by reflection
  opens com.sitepark.oparlclient.core to
      com.fasterxml.jackson.databind;
  opens com.sitepark.oparlclient.v1.objects to
      com.fasterxml.jackson.databind;
  opens com.sitepark.oparlclient.internal to
      com.fasterxml.jackson.databind;
}
