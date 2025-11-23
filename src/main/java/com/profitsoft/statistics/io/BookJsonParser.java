package com.profitsoft.statistics.io;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.profitsoft.statistics.model.Author;
import com.profitsoft.statistics.model.Book;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Streams JSON files and converts each object to a {@link Book}.
 */
public class BookJsonParser {

  private final JsonFactory jsonFactory = JsonFactory.builder().build();

  public void parse(Path file, Consumer<Book> consumer) throws IOException {
    try (InputStream input = Files.newInputStream(file);
         JsonParser parser = jsonFactory.createParser(input)) {
      if (parser.nextToken() != JsonToken.START_ARRAY) {
        throw new IOException("Expected JSON array in file: " + file);
      }
      while (parser.nextToken() != JsonToken.END_ARRAY) {
        if (parser.currentToken() == JsonToken.START_OBJECT) {
          Book book = readBook(parser);
          consumer.accept(book);
        } else {
          parser.skipChildren();
        }
      }
    }
  }

  private Book readBook(JsonParser parser) throws IOException {
    String title = null;
    String authorName = null;
    Integer yearPublished = null;
    String rawGenres = null;

    while (parser.nextToken() != JsonToken.END_OBJECT) {
      String fieldName = parser.currentName();
      if (fieldName == null) {
        parser.skipChildren();
        continue;
      }
      JsonToken valueToken = parser.nextToken();
      switch (fieldName) {
        case "title" -> title = parser.getValueAsString();
        case "author" -> authorName = parser.getValueAsString();
        case "year_published" -> yearPublished = parseYear(parser, valueToken);
        case "genres", "genre" -> rawGenres = parser.getValueAsString();
        default -> parser.skipChildren();
      }
    }

    List<String> genres = parseGenres(rawGenres);
    return new Book(title, new Author(authorName), yearPublished, genres);
  }

  private Integer parseYear(JsonParser parser, JsonToken valueToken) throws IOException {
    if (valueToken == JsonToken.VALUE_NUMBER_INT) {
      return parser.getIntValue();
    }
    if (valueToken == JsonToken.VALUE_STRING) {
      try {
        return Integer.parseInt(parser.getValueAsString());
      } catch (NumberFormatException ignored) {
        return null;
      }
    }
    return null;
  }

  private List<String> parseGenres(String rawGenres) {
    if (rawGenres == null || rawGenres.isBlank()) {
      return List.of();
    }
    String[] pieces = rawGenres.split(",");
    List<String> genres = new ArrayList<>();
    for (String piece : pieces) {
      String value = piece.trim();
      if (!value.isEmpty()) {
        genres.add(value);
      }
    }
    return genres;
  }
}
