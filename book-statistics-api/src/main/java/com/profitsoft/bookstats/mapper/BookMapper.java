package com.profitsoft.bookstats.mapper;

import com.profitsoft.bookstats.domain.Book;
import com.profitsoft.bookstats.dto.AuthorResponse;
import com.profitsoft.bookstats.dto.BookListItem;
import com.profitsoft.bookstats.dto.BookRequest;
import com.profitsoft.bookstats.dto.BookResponse;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {

  public void updateEntity(Book entity, BookRequest request) {
    entity.setTitle(request.getTitle());
    entity.setYearPublished(request.getYearPublished());
    entity.setGenres(cleanGenres(request.getGenres()));
  }

  private List<String> cleanGenres(List<String> genres) {
    if (genres == null) {
      return List.of();
    }
    List<String> cleaned = new ArrayList<>();
    for (String genre : genres) {
      if (genre != null && !genre.isBlank()) {
        cleaned.add(genre.trim());
      }
    }
    return cleaned;
  }

  public BookResponse toResponse(Book book) {
    BookResponse response = new BookResponse();
    response.setId(book.getId());
    response.setTitle(book.getTitle());
    response.setYearPublished(book.getYearPublished());
    response.setGenres(book.getGenres());
    AuthorResponse author = new AuthorResponse(book.getAuthor().getId(), book.getAuthor().getName());
    response.setAuthor(author);
    return response;
  }

  public BookListItem toListItem(Book book) {
    BookListItem item = new BookListItem();
    item.setId(book.getId());
    item.setTitle(book.getTitle());
    item.setYearPublished(book.getYearPublished());
    item.setAuthorName(book.getAuthor().getName());
    return item;
  }
}
