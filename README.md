# OParl Java Client

A Java client for [OParl](https://oparl.org) 1.0 and 1.1, the standard interface of German council
information systems (Ratsinformationssysteme). It maps all OParl object types to Java classes,
resolves references between them, iterates over paginated lists and supports the incremental
update mechanism of the specification.

- Typed classes for all twelve OParl object types, checked against the official schema
- References are resolved lazily, lists are paginated transparently
- Asynchronous (`CompletableFuture`) and synchronous API
- Filters (`created_since`, `modified_since`, `omit_internal`, `limit`) with correct encoding
- Robust against common server quirks: redirects, invalid values like unparsable dates or URLs
  instead of embedded objects, vendor-specific properties

## Requirements

- Java 21 or newer
- [Jackson](https://github.com/FasterXML/jackson) 2.9 or newer (pulled in as dependency; if your
  application manages the Jackson version itself, any version from 2.9 on works)

```xml
<dependency>
  <groupId>com.sitepark</groupId>
  <artifactId>oparl-java-client</artifactId>
  <version>2.0.0</version>
</dependency>
```

## Quick start

```java
OparlClient client = new OparlClient();

// the system object is the entry point of every OParl endpoint
OparlSystem system = client.get("https://oparl.example.org/", OparlSystem.class);

// a system lists its bodies (usually one municipality)
OparlBody body = system.getBody().get().getData().get(0);

// meetings of that body created since 2024-01-21; further pages are fetched while iterating
ZonedDateTime since = ZonedDateTime.of(2024, 1, 21, 0, 0, 0, 0, ZoneId.of("Europe/Berlin"));
OparlList<OparlMeeting> meetings =
    body.getMeeting().withQueryParams(Created.since(since)).get();

for (OparlMeeting meeting : meetings.all()) {
  System.out.println(meeting.getName() + " " + meeting.getStart());
}
```

## Usage

### Creating a client

`new OparlClient()` creates a client with sensible defaults. Use the builder to configure it:

```java
OparlClient client = OparlClient.builder()
    .requestTimeout(Duration.ofSeconds(30))
    .userAgent("my-app/1.0 (+https://example.org/contact)")
    .build();
```

| Option                   | Default                                                                          |
|--------------------------|----------------------------------------------------------------------------------|
| `httpClient`             | a `java.net.http.HttpClient` that follows redirects                              |
| `requestTimeout`         | 10 seconds per request, including redirects and reading the response             |
| `userAgent`              | `oparl-java-client/<version>`                                                    |
| `objectMapperCustomizer` | none; called after the client has registered its own deserializers               |

Every request is sent with `Accept: application/json`. A `userAgent` that names your application
and a way to contact you helps the operators of OParl servers.

If you pass your own `HttpClient`, it is used as is. Configure
`followRedirects(HttpClient.Redirect.NORMAL)` in that case, since OParl servers may redirect to
their canonical URLs.

A client is thread-safe and should be reused for all requests.

### Resolving objects

Every request exists in a synchronous and an asynchronous variant; the asynchronous one ends in
`Async`:

```java
// asynchronous, returns a CompletableFuture
CompletableFuture<OparlBody> future = client.getAsync(bodyUrl, OparlBody.class);

// synchronous, waits for the result and throws the actual exception
OparlBody body = client.get(bodyUrl, OparlBody.class);
```

The synchronous `get()` throws the cause directly, e.g. an `OparlHttpException`, instead of a
`CompletionException`. `OparlFutures.join(future)` does the same for any future of this library.

If the type of an object is not known in advance, `getAny()` returns the matching subclass of
`OparlObjectV1`, determined by its `type` property:

```java
OparlObjectV1 object = client.getAny(someUrl);
if (object instanceof OparlMeeting) {
  // ...
}
```

Objects of vendor-specific or unknown types are returned as plain `OparlObjectV1`, with their
properties available via `getAdditionalProperties()`.

### References

OParl objects refer to each other by URL. These URLs are wrapped in an `OparlReference`, which
knows the type of the referenced object and resolves it on demand:

```java
OparlConsultation consultation = client.get(consultationUrl, OparlConsultation.class);
OparlReference<OparlMeeting> reference = consultation.getMeeting();

URI meetingUrl = reference.getUri();           // only the URL, no request
OparlMeeting meeting = reference.get();        // synchronous
reference.getAsync().thenAccept(m -> ...);      // asynchronous
```

Lists of references, e.g. the originators of a paper, are `List<OparlReference<…>>` as well:

```java
for (OparlReference<OparlPerson> originator : paper.getOriginatorPerson()) {
  OparlPerson person = originator.get();
}
```

When an object is serialized with Jackson, a reference is written as its URL, just like in the
original JSON.

### Lists and pagination

Lists of objects, e.g. all meetings of a body, are returned as `OparlList`. The server may split
them into pages; an `OparlList` is one page.

```java
OparlList<OparlBody> page = system.getBody().get();

page.getData();                              // the elements of this page
page.getPagination().getTotalElements();     // optional, depending on the server

if (page.hasNextPage()) {
  OparlList<OparlBody> next = page.fetchNextPage();
}

// all elements of all pages; further pages are fetched while iterating
for (OparlBody body : page.all()) {
  // ...
}

// or as a stream, fetching pages lazily
List<String> names = page.stream().map(OparlBody::getName).collect(Collectors.toList());
```

If a page can not be fetched, `all()` and `stream()` fail with the exception of that page, e.g. an
`OparlHttpException` whose `getUri()` is the URL of the page, so a partial result is never mistaken
for the complete list. Empty pages are skipped, and iteration stops if a server links back to a
page that has already been visited.

### Filters

Lists can be filtered with the URL parameters defined by the specification:

```java
ZonedDateTime date = ZonedDateTime.of(2024, 8, 16, 0, 0, 0, 0, ZoneId.of("Europe/Berlin"));

OparlList<OparlPaper> papers = body.getPaper()
    .withQueryParams(Modified.since(date), OmitInternal.TRUE, Limit.of(100))
    .get();
```

| Class          | URL parameter                         |
|----------------|---------------------------------------|
| `Created`      | `created_since`, `created_until`      |
| `Modified`     | `modified_since`, `modified_until`    |
| `OmitInternal` | `omit_internal`                       |
| `Limit`        | `limit` (servers may ignore it)       |
| `QueryParam`   | any other parameter                   |

The specification requires a full date-time including the time zone, e.g.
`2024-08-16T00:00:00+02:00`. `Created` and `Modified` therefore accept `OffsetDateTime`,
`ZonedDateTime` and `Instant`. Pass all values unencoded; the client URL-encodes them.

### Keeping a local copy up to date

OParl 1.1 defines an update mechanism: after an initial import, request only the objects modified
since the last run. With `modified_since`, the server also returns deleted objects, marked with
`deleted`, so they can be removed locally.

```java
Instant lastRun = ...;
Instant thisRun = Instant.now(); // taken before the run, so no change during the run is missed

// a safety margin covers clock differences between client and server
Instant since = lastRun.minus(Duration.ofMinutes(5));

for (OparlPaper paper : body.getPaper()
    .withQueryParams(Modified.since(since), OmitInternal.TRUE)
    .get()
    .all()) {
  if (paper.isDeleted()) {
    repository.delete(paper.getId());
  } else {
    repository.save(paper);
  }
}
lastRun = thisRun;
```

`lastRun` is only advanced after a complete run: if a page can not be fetched, `all()` throws an
exception and the next run starts again from the previous point in time.

### Vendor-specific properties

OParl servers may add properties with a vendor prefix, e.g. `"BeispielHersteller:faxNumber"`.
These and all other properties that are not mapped to a field are kept as Jackson `JsonNode`s and
are written back on serialization:

```java
JsonNode faxNumber = person.getAdditionalProperty("BeispielHersteller:faxNumber");
Map<String, JsonNode> all = person.getAdditionalProperties();
```

Values that do not match the type required by the specification do not fail the whole response,
e.g. URLs where embedded objects are required, a number where a reference is expected, or a date
that can not be parsed. Invalid elements are left out of a list, an invalid single value leaves the
property `null`, and a warning is logged. The original value is kept as additional property under
the same name, so it is not lost and is written back on serialization:

```java
List<OparlMembership> memberships = person.getMembership(); // only the valid embedded objects
JsonNode original = person.getAdditionalProperty("membership"); // e.g. ["https://…/membership/1"]
```

A single value where a list is required, e.g. `"email": "info@example.org"`, is read as a list
with that value.

### Error handling

Every failed request is reported as an unchecked `OparlException`. `getUri()` returns the URL of
the failed request, `getCause()` the underlying exception, if any.

| Exception                  | Cause                                                                  |
|----------------------------|------------------------------------------------------------------------|
| `OparlHttpException`       | status code other than 2xx; contains the OParl error object, if sent  |
| `OparlParseException`      | the response is no valid JSON or does not match the requested type    |
| `OparlConnectionException` | the server could not be reached or closed the connection              |
| `OparlTimeoutException`    | the request took longer than the request timeout (a connection error) |
| `OparlException`           | other errors, e.g. an invalid URL, an empty response or a non-http URL |

Asynchronous methods complete the future exceptionally, with one of these exceptions as cause of
the `CompletionException`; the synchronous methods throw it directly. The same exceptions are
thrown while iterating over the pages of a list.

```java
try {
  OparlBody body = client.get(bodyUrl, OparlBody.class);
} catch (OparlTimeoutException e) {
  // try again later
} catch (OparlHttpException e) {
  log(e.getStatusCode() + " for " + e.getUri());
} catch (OparlException e) {
  log(e.getMessage());
}
```

### Object types

| Class                  | OParl type                                                                              |
|------------------------|-----------------------------------------------------------------------------------------|
| `OparlObjectV1`        | common base class (`id`, `type`, `created`, `modified`, `deleted`, `keyword`, `license`, `web`) |
| `OparlSystem`          | [`oparl:System`](https://dev.oparl.org/spezifikation/1.1#entity-system)                 |
| `OparlBody`            | [`oparl:Body`](https://dev.oparl.org/spezifikation/1.1#entity-body)                     |
| `OparlLegislativeTerm` | [`oparl:LegislativeTerm`](https://dev.oparl.org/spezifikation/1.1#entity-legislativeterm) |
| `OparlOrganization`    | [`oparl:Organization`](https://dev.oparl.org/spezifikation/1.1#entity-organization)     |
| `OparlPerson`          | [`oparl:Person`](https://dev.oparl.org/spezifikation/1.1#entity-person)                 |
| `OparlMembership`      | [`oparl:Membership`](https://dev.oparl.org/spezifikation/1.1#entity-membership)         |
| `OparlMeeting`         | [`oparl:Meeting`](https://dev.oparl.org/spezifikation/1.1#entity-meeting)               |
| `OparlAgendaItem`      | [`oparl:AgendaItem`](https://dev.oparl.org/spezifikation/1.1#entity-agendaitem)         |
| `OparlPaper`           | [`oparl:Paper`](https://dev.oparl.org/spezifikation/1.1#entity-paper)                   |
| `OparlConsultation`    | [`oparl:Consultation`](https://dev.oparl.org/spezifikation/1.1#entity-consultation)     |
| `OparlFile`            | [`oparl:File`](https://dev.oparl.org/spezifikation/1.1#entity-file)                     |
| `OparlLocation`        | [`oparl:Location`](https://dev.oparl.org/spezifikation/1.1#entity-location)             |

`OparlTypes` maps the `type` URLs of OParl 1.0 and 1.1 to these classes.

Almost all properties are optional in OParl, so every getter returns `null` if the server did not
send the value, also for numbers and booleans (`Integer getOrder()`, `Boolean getCancelled()`). The
only exception is `isDeleted()`, which is `false` then, since the specification only marks deleted
objects.

Dates (`date`) are mapped to `LocalDate`, points in time (`date-time`) to `OffsetDateTime`, keeping
the offset sent by the server. Values that do not follow the specification are read tolerantly: a
date-time without offset is interpreted in German time (`Europe/Berlin`), a date where a date-time
is expected as start of that day; values that can not be parsed at all are read as `null`,
logged and kept as additional property. When an object is serialized with Jackson, these values are written in the format of the
specification, with any `ObjectMapper`; properties without value are left out, as the
specification recommends.

## Security

The client follows every URL a server returns: references, pagination links and redirects, and it
reads responses of any size into memory. It is meant for OParl servers you trust. If your
application lets users enter the endpoint, restrict the hosts it may reach, e.g. with a proxy or
network rules, since a malicious server could otherwise make it request internal hosts (SSRF).

## Logging

The library logs via `System.Logger` of the JDK and has no logging dependency. Without further
configuration, the output goes to `java.util.logging`: warnings, e.g. about invalid URLs or dates
the client ignored, are printed to the console, debug messages are suppressed. To route the output
to the logging framework of your application, add its bridge:

| Framework | Bridge |
|---|---|
| Log4j 2 | `org.apache.logging.log4j:log4j-jpl` |
| SLF4J / Logback | `org.slf4j:slf4j-jdk-platform-logging` |

The logger names are the class names, e.g. `com.sitepark.oparlclient.OparlClient`.

## Building

Building requires JDK 21 or newer.

```sh
mvn verify          # build, run the tests and all checks
mvn spotless:apply  # format the code
```

`mvn verify` fails on compiler warnings, formatting issues (Spotless), findings of PMD and
SpotBugs, and a line coverage below 60 % in any package (JaCoCo).

`SchemaCoverageTest` and `SchemaValuesTest` check the object classes against the OParl 1.1 schema
in `src/test/resources/schema/1.1`: every property must be mapped with a matching type, and every
value must arrive at its getter.

## License

[MIT](LICENSE.md). The OParl schema files in `src/test/resources/schema/1.1`, used for testing only,
are licensed under [CC BY-SA 4.0](https://creativecommons.org/licenses/by-sa/4.0/) by the OParl
authors.
