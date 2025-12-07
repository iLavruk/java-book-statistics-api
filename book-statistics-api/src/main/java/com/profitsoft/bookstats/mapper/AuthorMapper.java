package com.profitsoft.bookstats.mapper;

import com.profitsoft.bookstats.domain.Author;
import com.profitsoft.bookstats.dto.AuthorResponse;
import org.springframework.stereotype.Component;

@Component
public class AuthorMapper {

  public AuthorResponse toResponse(Author author) {
    if (author == null) {
      return null;
    }
    return new AuthorResponse(author.getId(), author.getName());
  }
}
