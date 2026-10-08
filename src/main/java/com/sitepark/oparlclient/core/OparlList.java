package com.sitepark.oparlclient.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * One page of an external object list, e.g. all meetings of a body. A server may split a list
 * into pages; {@link #all()} and {@link #stream()} iterate over the elements of all pages and fetch
 * further pages on demand:
 *
 * <pre>{@code
 * OparlList<OparlMeeting> meetings = body.getMeeting().get();
 * for (OparlMeeting meeting : meetings.all()) {
 *   // ...
 * }
 * }</pre>
 *
 * @param <T> the type of the elements, e.g. {@code OparlMeeting}
 */
public class OparlList<T> extends OparlObject {

  private static final Logger LOGGER = System.getLogger(OparlList.class.getName());

  private List<T> data = new ArrayList<T>();

  private Pagination pagination = new Pagination();

  private Links<T> links = new Links<T>();

  @JsonIgnore private Set<URI> sourceUris = Collections.emptySet();

  public void setData(List<T> data) {
    this.data = data;
  }

  /** Returns the elements of this page only, see {@link #all()} for all pages. */
  public List<T> getData() {
    return data;
  }

  public void setPagination(Pagination pagination) {
    this.pagination = pagination;
  }

  /** Returns information about the number of elements and pages, as far as the server sends it. */
  public Pagination getPagination() {
    return pagination;
  }

  public void setLinks(Links<T> links) {
    this.links = links;
  }

  /** Returns the links to other pages of the list. */
  public Links<T> getLinks() {
    return links;
  }

  /**
   * Returns the URLs this page was loaded from: the requested URL and, after a redirect, the final
   * URL. Empty if the page was not loaded by an {@code OparlClient}.
   */
  @JsonIgnore
  public Set<URI> getSourceUris() {
    return sourceUris;
  }

  /** Set by the {@code OparlClient} after loading the page; not meant to be called otherwise. */
  @JsonIgnore
  public void setSourceUris(Set<URI> sourceUris) {
    this.sourceUris =
        sourceUris != null
            ? Collections.unmodifiableSet(new LinkedHashSet<>(sourceUris))
            : Collections.emptySet();
  }

  /** Returns whether there is a following page, i.e. this page has a {@code next} link. */
  public boolean hasNextPage() {
    return this.links != null && this.links.getNext() != null;
  }

  /**
   * Fetches the following page asynchronously.
   *
   * @return the following page, or a future failed with a {@link NoSuchElementException} if this
   *     is the last page
   */
  public CompletableFuture<OparlList<T>> fetchNextPageAsync() {
    if (!this.hasNextPage()) {
      return CompletableFuture.failedFuture(new NoSuchElementException("This is the last page"));
    }
    return this.links.getNext().getAsync();
  }

  /**
   * Fetches the following page and waits for it, see {@link OparlFutures#join} for the thrown
   * exceptions.
   *
   * @throws NoSuchElementException if this is the last page
   */
  public OparlList<T> fetchNextPage() {
    return OparlFutures.join(this.fetchNextPageAsync());
  }

  /**
   * Returns the elements of this page and all following pages, e.g. for use in a for-each loop.
   * Following pages are fetched while iterating, the next one as soon as iterating over the
   * current page starts.
   *
   * <p>If a page can not be fetched, the iteration fails with the {@link OparlException} of that
   * page, e.g. an {@link OparlHttpException}; {@link OparlException#getUri()} is the URL of the
   * page. Empty pages are skipped, and the iteration ends if a page links to a page that has
   * already been visited.
   */
  public Iterable<T> all() {
    return PageIterator::new;
  }

  /**
   * Returns a sequential stream of the elements of this page and all following pages. Following
   * pages are fetched lazily, see {@link #all()}.
   *
   * @throws OparlException while consuming the stream, if a page can not be fetched
   */
  public Stream<T> stream() {
    return StreamSupport.stream(
        Spliterators.spliteratorUnknownSize(new PageIterator(), Spliterator.ORDERED), false);
  }

  private final class PageIterator implements Iterator<T> {

    private OparlList<T> currentPage = OparlList.this;

    private int index;

    private boolean nextPageRequested;

    private CompletableFuture<OparlList<T>> nextPage;

    private URI nextPageUri;

    private final Set<URI> visitedPages = new HashSet<>();

    PageIterator() {
      this.visitedPages.addAll(urisOf(this.currentPage));
    }

    @Override
    public boolean hasNext() {
      this.advance();
      return this.currentPage != null;
    }

    @Override
    public T next() {
      if (!this.hasNext()) {
        throw new NoSuchElementException();
      }
      T element = dataOf(this.currentPage).get(this.index);
      this.index++;
      return element;
    }

    /** Moves on to the next page that has elements left, or to {@code null} if there is none. */
    @SuppressWarnings("PMD.NullAssignment") // null marks that the next page is not requested yet
    private void advance() {
      while (this.currentPage != null) {
        this.requestNextPage();
        if (this.index < dataOf(this.currentPage).size()) {
          return;
        }
        this.currentPage = this.awaitNextPage();
        this.index = 0;
        this.nextPageRequested = false;
        this.nextPage = null;
        this.nextPageUri = null;
      }
    }

    private void requestNextPage() {
      if (this.nextPageRequested) {
        return;
      }
      this.nextPageRequested = true;
      Links<T> links = this.currentPage.getLinks();
      OparlReference<OparlList<T>> next = links != null ? links.getNext() : null;
      if (next == null) {
        return;
      }
      if (this.visitedPages.contains(next.getUri())) {
        LOGGER.log(
            Level.WARNING, "Stopping pagination, page {0} has already been visited", next.getUri());
        return;
      }
      this.nextPageUri = next.getUri();
      this.nextPage = next.getAsync();
    }

    private OparlList<T> awaitNextPage() {
      if (this.nextPage == null) {
        return null;
      }
      // a failed page is reported with its own exception, e.g. an OparlHttpException
      OparlList<T> page = OparlFutures.join(this.nextPage);
      if (page == null) {
        return null;
      }
      // the requested URL was new, but it may have redirected to a page that was already visited
      Set<URI> pageUris = urisOf(page);
      pageUris.remove(this.nextPageUri);
      if (!Collections.disjoint(this.visitedPages, pageUris)) {
        LOGGER.log(
            Level.WARNING,
            "Stopping pagination, page {0} leads to a page that has already been visited",
            this.nextPageUri);
        return null;
      }
      this.visitedPages.add(this.nextPageUri);
      this.visitedPages.addAll(pageUris);
      return page;
    }
  }

  private static <T> List<T> dataOf(OparlList<T> page) {
    return page.getData() != null ? page.getData() : Collections.emptyList();
  }

  /** All known URLs of the given page: the URLs it was loaded from and its {@code self} link. */
  private static Set<URI> urisOf(OparlList<?> page) {
    Set<URI> uris = new HashSet<>(page.getSourceUris());
    if (page.getLinks() != null && page.getLinks().getSelf() != null) {
      uris.add(page.getLinks().getSelf().getUri());
    }
    return uris;
  }

  @Override
  public String toString() {
    StringBuilder string =
        new StringBuilder(64).append("OparlList[").append(dataOf(this).size()).append(" elements");
    if (!this.sourceUris.isEmpty()) {
      string.append(", uri=").append(this.sourceUris.iterator().next());
    }
    if (this.hasNextPage()) {
      string.append(", next=").append(this.links.getNext().getUri());
    }
    return string.append(']').toString();
  }

  /**
   * Information about the number of elements and pages. All values are optional; values not sent
   * by the server are {@code null}.
   */
  public static class Pagination extends OparlObject {

    private Integer totalElements;

    private Integer elementsPerPage;

    private Integer currentPage;

    private Integer totalPages;

    /** Returns the total number of elements; may change until the following pages are fetched. */
    public Integer getTotalElements() {
      return totalElements;
    }

    public void setTotalElements(Integer totalElements) {
      this.totalElements = totalElements;
    }

    /** Returns the number of elements per page; the same for all pages except the last one. */
    public Integer getElementsPerPage() {
      return elementsPerPage;
    }

    public void setElementsPerPage(Integer elementsPerPage) {
      this.elementsPerPage = elementsPerPage;
    }

    /** Returns the number of this page. */
    public Integer getCurrentPage() {
      return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
      this.currentPage = currentPage;
    }

    /** Returns the total number of pages. */
    public Integer getTotalPages() {
      return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
      this.totalPages = totalPages;
    }
  }

  /**
   * Links to other pages of the list. Only {@code next} is mandatory, on all pages except the last
   * one; all other links may be {@code null}.
   */
  public static class Links<T> extends OparlObject {

    private OparlReference<OparlList<T>> first;

    private OparlReference<OparlList<T>> prev;

    private OparlReference<OparlList<T>> self;

    private OparlReference<OparlList<T>> next;

    private OparlReference<OparlList<T>> last;

    private URI web;

    /** Returns the first page. */
    public OparlReference<OparlList<T>> getFirst() {
      return first;
    }

    public void setFirst(OparlReference<OparlList<T>> first) {
      this.first = first;
    }

    /** Returns the previous page. */
    public OparlReference<OparlList<T>> getPrev() {
      return prev;
    }

    public void setPrev(OparlReference<OparlList<T>> prev) {
      this.prev = prev;
    }

    /** Returns the canonical URL of this page. */
    public OparlReference<OparlList<T>> getSelf() {
      return self;
    }

    public void setSelf(OparlReference<OparlList<T>> self) {
      this.self = self;
    }

    /** Returns the next page, or {@code null} on the last page. */
    public OparlReference<OparlList<T>> getNext() {
      return next;
    }

    public void setNext(OparlReference<OparlList<T>> next) {
      this.next = next;
    }

    /** Returns the last page. */
    public OparlReference<OparlList<T>> getLast() {
      return last;
    }

    public void setLast(OparlReference<OparlList<T>> last) {
      this.last = last;
    }

    /** Returns the URL of a website showing this page, e.g. in the council information system. */
    public URI getWeb() {
      return web;
    }

    public void setWeb(URI web) {
      this.web = web;
    }
  }
}
