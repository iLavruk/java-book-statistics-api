package com.profitsoft.bookstats.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.profitsoft.bookstats.IntegrationTestBase;
import com.profitsoft.bookstats.dto.AuthorRequest;
import com.profitsoft.bookstats.dto.BookRequest;
import com.profitsoft.bookstats.repository.AuthorRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class AuthorControllerIT extends IntegrationTestBase {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private AuthorRepository authorRepository;

  @Test
  void shouldListSeededAuthors() throws Exception {
    mockMvc.perform(get("/api/authors"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(3)));
  }

  @Test
  void shouldCreateUpdateAndDeleteAuthor() throws Exception {
    AuthorRequest create = new AuthorRequest();
    create.setName("New Author");

    String response = mockMvc.perform(post("/api/authors")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(create)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name", is("New Author")))
        .andReturn().getResponse().getContentAsString();

    long id = objectMapper.readTree(response).get("id").asLong();

    AuthorRequest update = new AuthorRequest();
    update.setName("Updated Author");

    mockMvc.perform(put("/api/authors/{id}", id)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(update)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name", is("Updated Author")));

    mockMvc.perform(delete("/api/authors/{id}", id))
        .andExpect(status().isNoContent());
  }

  @Test
  void shouldRejectDuplicateAuthor() throws Exception {
    AuthorRequest request = new AuthorRequest();
    request.setName("George Orwell");

    mockMvc.perform(post("/api/authors")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isConflict());
  }

  @Test
  void shouldRejectInvalidAuthorRequest() throws Exception {
    AuthorRequest request = new AuthorRequest();
    request.setName(" ");

    mockMvc.perform(post("/api/authors")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldFailToDeleteAuthorWithBooks() throws Exception {
    long authorId = authorRepository.findByNameIgnoreCase("George Orwell")
        .orElseThrow()
        .getId();

    BookRequest book = new BookRequest();
    book.setTitle("Linked Book");
    book.setAuthorId(authorId);
    book.setYearPublished(2000);

    mockMvc.perform(post("/api/books")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(book)))
        .andExpect(status().isCreated());

    mockMvc.perform(delete("/api/authors/{id}", authorId))
        .andExpect(status().isConflict());
  }
}
