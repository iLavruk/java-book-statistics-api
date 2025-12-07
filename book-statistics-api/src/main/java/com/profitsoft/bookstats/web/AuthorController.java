package com.profitsoft.bookstats.web;

import com.profitsoft.bookstats.dto.AuthorRequest;
import com.profitsoft.bookstats.dto.AuthorResponse;
import com.profitsoft.bookstats.mapper.AuthorMapper;
import com.profitsoft.bookstats.service.AuthorService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/authors")
public class AuthorController {

  private final AuthorService authorService;
  private final AuthorMapper authorMapper;

  public AuthorController(AuthorService authorService, AuthorMapper authorMapper) {
    this.authorService = authorService;
    this.authorMapper = authorMapper;
  }

  @GetMapping
  public List<AuthorResponse> list() {
    return authorService.findAll().stream()
        .map(authorMapper::toResponse)
        .toList();
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public AuthorResponse create(@RequestBody @Valid AuthorRequest request) {
    return authorMapper.toResponse(authorService.create(request));
  }

  @PutMapping("/{id}")
  public AuthorResponse update(@PathVariable Long id, @RequestBody @Valid AuthorRequest request) {
    return authorMapper.toResponse(authorService.update(id, request));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    authorService.delete(id);
  }
}
