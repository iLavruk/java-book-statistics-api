package com.profitsoft.statistics.util;

import com.profitsoft.statistics.model.Book;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Attributes supported for statistics calculation.
 */
public enum Attribute {
  AUTHOR("author") {
    @Override
    public Collection<String> extractValues(Book book) {
      if (book.author() == null) {
        return List.of();
      }
      return List.of(book.author().name());
    }
  },
  YEAR_PUBLISHED("year_published") {
    @Override
    public Collection<String> extractValues(Book book) {
      if (book.yearPublished() == null) {
        return List.of();
      }
      return List.of(book.yearPublished().toString());
    }
  },
  GENRES("genres") {
    @Override
    public Collection<String> extractValues(Book book) {
      return book.genres();
    }
  };

  private final String inputName;

  Attribute(String inputName) {
    this.inputName = inputName;
  }

  public String inputName() {
    return inputName;
  }

  public abstract Collection<String> extractValues(Book book);

  public static Optional<Attribute> fromInput(String attribute) {
    if (attribute == null) {
      return Optional.empty();
    }
    String normalized = attribute.trim().toLowerCase(Locale.ROOT);
    for (Attribute value : values()) {
      if (value.inputName.equals(normalized)) {
        return Optional.of(value);
      }
    }
    if ("genre".equals(normalized)) {
      return Optional.of(GENRES);
    }
    if ("year".equals(normalized) || "yearpublished".equals(normalized)) {
      return Optional.of(YEAR_PUBLISHED);
    }
    return Optional.empty();
  }
}
