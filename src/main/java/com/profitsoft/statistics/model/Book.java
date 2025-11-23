package com.profitsoft.statistics.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Primary domain entity.
 */
public final class Book {

  private final String title;
  private final Author author;
  private final Integer yearPublished;
  private final List<String> genres;

  public Book(String title, Author author, Integer yearPublished, List<String> genres) {
    this.title = title;
    this.author = author;
    this.yearPublished = yearPublished;
    this.genres = genres == null
        ? Collections.emptyList()
        : Collections.unmodifiableList(new ArrayList<>(genres));
  }

  public String title() {
    return title;
  }

  public Author author() {
    return author;
  }

  public Integer yearPublished() {
    return yearPublished;
  }

  public List<String> genres() {
    return genres;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Book book = (Book) o;
    return Objects.equals(title, book.title)
        && Objects.equals(author, book.author)
        && Objects.equals(yearPublished, book.yearPublished)
        && Objects.equals(genres, book.genres);
  }

  @Override
  public int hashCode() {
    return Objects.hash(title, author, yearPublished, genres);
  }

  @Override
  public String toString() {
    return "Book{" +
        "title='" + title + '\'' +
        ", author=" + author +
        ", yearPublished=" + yearPublished +
        ", genres=" + genres +
        '}';
  }
}
