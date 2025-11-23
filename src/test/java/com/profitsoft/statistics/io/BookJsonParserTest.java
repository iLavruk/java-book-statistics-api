package com.profitsoft.statistics.io;

import com.profitsoft.statistics.model.Book;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BookJsonParserTest {

  private final BookJsonParser parser = new BookJsonParser();

  @Test
  void parsesBooksWithMultipleGenres() throws Exception {
    Path file = resourcePath("sample-data/books_1.json");
    List<Book> books = new ArrayList<>();

    parser.parse(file, books::add);

    assertEquals(3, books.size());
    Book first = books.getFirst();
    assertEquals("1984", first.title());
    assertEquals("George Orwell", first.author().name());
    assertEquals(1949, first.yearPublished());
    assertEquals(List.of("Dystopian", "Political Fiction"), first.genres());
  }

  @Test
  void ignoresEmptyGenres() throws Exception {
    Path file = resourcePath("sample-data/books_1.json");
    List<Book> books = new ArrayList<>();

    parser.parse(file, books::add);

    assertFalse(books.stream().anyMatch(book -> book.genres().contains("")));
  }

  @Test
  void parsesTwoFilesIndependently() throws Exception {
    Path first = resourcePath("sample-data/books_1.json");
    Path second = resourcePath("sample-data/books_2.json");
    List<Book> books = new ArrayList<>();

    parser.parse(first, books::add);
    parser.parse(second, books::add);

    assertEquals(6, books.size());
    assertTrue(books.stream().anyMatch(book -> "Animal Farm".equals(book.title())));
    assertTrue(books.stream().anyMatch(book -> "Macbeth".equals(book.title())));
  }

  private Path resourcePath(String relative) throws URISyntaxException {
    java.net.URL resource = getClass().getClassLoader().getResource(relative);
    assertNotNull(resource, "Resource not found: " + relative);
    return Path.of(resource.toURI());
  }
}
