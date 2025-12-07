package com.profitsoft.bookstats.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.profitsoft.bookstats.IntegrationTestBase;
import com.profitsoft.bookstats.domain.Author;
import com.profitsoft.bookstats.dto.BookListRequest;
import com.profitsoft.bookstats.dto.BookRequest;
import com.profitsoft.bookstats.repository.AuthorRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class BookControllerIT extends IntegrationTestBase {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private AuthorRepository authorRepository;

  @Test
  @Transactional
  void shouldCreateReadUpdateAndDeleteBook() throws Exception {
    Long authorId = authorRepository.findByNameIgnoreCase("George Orwell")
        .map(Author::getId)
        .orElseThrow();

    BookRequest create = new BookRequest();
    create.setTitle("Test Book");
    create.setAuthorId(authorId);
    create.setYearPublished(2020);

    String created = mockMvc.perform(post("/api/books")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(create)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", notNullValue()))
        .andExpect(jsonPath("$.author.id", is(authorId.intValue())))
        .andReturn().getResponse().getContentAsString();

    long id = objectMapper.readTree(created).get("id").asLong();

    mockMvc.perform(get("/api/books/{id}", id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title", is("Test Book")))
        .andExpect(jsonPath("$.author.id", is(authorId.intValue())));

    BookRequest update = new BookRequest();
    update.setTitle("Updated Book");
    update.setAuthorId(authorId);
    update.setYearPublished(2021);

    mockMvc.perform(put("/api/books/{id}", id)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(update)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title", is("Updated Book")))
        .andExpect(jsonPath("$.yearPublished", is(2021)));

    mockMvc.perform(delete("/api/books/{id}", id))
        .andExpect(status().isNoContent());
  }

  @Test
  @Transactional
  void shouldListWithPagination() throws Exception {
    Long authorId = authorRepository.findByNameIgnoreCase("Jane Austen")
        .map(Author::getId)
        .orElseThrow();

    BookRequest book = new BookRequest();
    book.setTitle("List Book");
    book.setAuthorId(authorId);
    book.setYearPublished(2001);

    mockMvc.perform(post("/api/books")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(book)))
        .andExpect(status().isCreated());

    BookListRequest listRequest = new BookListRequest();
    listRequest.setPage(1);
    listRequest.setSize(10);

    mockMvc.perform(post("/api/books/_list")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(listRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalPages", greaterThanOrEqualTo(1)))
        .andExpect(jsonPath("$.list[0].title", notNullValue()));
  }

  @Test
  @Transactional
  void shouldGenerateCsvReport() throws Exception {
    Long authorId = authorRepository.findByNameIgnoreCase("George Orwell")
        .map(Author::getId)
        .orElseThrow();
    BookRequest book = new BookRequest();
    book.setTitle("Report Book");
    book.setAuthorId(authorId);
    book.setYearPublished(2022);
    mockMvc.perform(post("/api/books")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(book)))
        .andExpect(status().isCreated());

    mockMvc.perform(post("/api/books/_report")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{}"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith("text/csv"))
        .andExpect(content().string(containsString("Report Book")));
  }

  @Test
  @Transactional
  void shouldImportFromJsonFile() throws Exception {
    String json = """
        [
          {"title":"Imported 1","author":"George Orwell","year_published":1950,"genres":"Dystopian"},
          {"title":"Imported 2","author":"Jane Austen","year_published":1800,"genres":"Romance"}
        ]
        """;

    MockMultipartFile file = new MockMultipartFile(
        "file",
        "books.json",
        MediaType.APPLICATION_JSON_VALUE,
        json.getBytes()
    );

    mockMvc.perform(multipart("/api/books/upload").file(file))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.imported", is(2)))
        .andExpect(jsonPath("$.failed", is(0)));
  }

  @Test
  void shouldReturn404ForMissingBook() throws Exception {
    long missingId = 999999L;
    mockMvc.perform(get("/api/books/{id}", missingId))
        .andExpect(status().isNotFound());

    BookRequest update = new BookRequest();
    update.setTitle("Any");
    update.setAuthorId(1L);

    mockMvc.perform(put("/api/books/{id}", missingId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(update)))
        .andExpect(status().isNotFound());

    mockMvc.perform(delete("/api/books/{id}", missingId))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldValidateBookRequest() throws Exception {
    BookRequest invalid = new BookRequest();
    invalid.setTitle(" "); // blank
    invalid.setAuthorId(null); // missing

    mockMvc.perform(post("/api/books")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalid)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldCountFailedUploadWhenAuthorMissing() throws Exception {
    String json = """
        [
          {"title":"Bad","author":"Unknown Author","year_published":2000,"genres":"Drama"}
        ]
        """;

    MockMultipartFile file = new MockMultipartFile(
        "file",
        "books.json",
        MediaType.APPLICATION_JSON_VALUE,
        json.getBytes()
    );

    mockMvc.perform(multipart("/api/books/upload").file(file))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.imported", is(0)))
        .andExpect(jsonPath("$.failed", is(1)));
  }
}
