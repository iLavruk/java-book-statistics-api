package com.profitsoft.bookstats.service;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.profitsoft.bookstats.domain.Author;
import com.profitsoft.bookstats.domain.Book;
import com.profitsoft.bookstats.dto.BookListRequest;
import com.profitsoft.bookstats.dto.BookPageResponse;
import com.profitsoft.bookstats.dto.BookReportRequest;
import com.profitsoft.bookstats.dto.BookRequest;
import com.profitsoft.bookstats.dto.BookResponse;
import com.profitsoft.bookstats.dto.ImportBookDto;
import com.profitsoft.bookstats.dto.UploadResponse;
import com.profitsoft.bookstats.mapper.BookMapper;
import com.profitsoft.bookstats.repository.AuthorRepository;
import com.profitsoft.bookstats.repository.BookRepository;
import com.profitsoft.bookstats.repository.specification.BookSpecifications;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class BookService {

  private final BookRepository bookRepository;
  private final AuthorRepository authorRepository;
  private final BookMapper bookMapper;
  private final ObjectMapper objectMapper;
  private final JsonFactory jsonFactory;

  public BookService(BookRepository bookRepository,
                     AuthorRepository authorRepository,
                     BookMapper bookMapper,
                     ObjectMapper objectMapper) {
    this.bookRepository = bookRepository;
    this.authorRepository = authorRepository;
    this.bookMapper = bookMapper;
    this.objectMapper = objectMapper;
    this.jsonFactory = new JsonFactory();
  }

  public BookResponse create(BookRequest request) {
    Author author = findAuthor(request.getAuthorId());
    Book book = new Book();
    bookMapper.updateEntity(book, request);
    book.setAuthor(author);
    return bookMapper.toResponse(bookRepository.save(book));
  }

  @Transactional
  public BookResponse update(Long id, BookRequest request) {
    Book book = bookRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Book not found: " + id));
    Author author = findAuthor(request.getAuthorId());
    bookMapper.updateEntity(book, request);
    book.setAuthor(author);
    return bookMapper.toResponse(bookRepository.save(book));
  }

  @Transactional
  public void delete(Long id) {
    Book book = bookRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Book not found: " + id));
    bookRepository.delete(book);
  }

  @Transactional
  public BookResponse findById(Long id) {
    Book book = bookRepository.fetchByIdWithAuthor(id)
        .orElseThrow(() -> new NotFoundException("Book not found: " + id));
    return bookMapper.toResponse(book);
  }

  @Transactional
  public BookPageResponse list(BookListRequest request) {
    int page = request.getPage() == null ? 0 : Math.max(0, request.getPage() - 1);
    int size = request.getSize() == null ? 20 : request.getSize();
    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
    Specification<Book> spec = BookSpecifications.filter(
        request.getAuthorId(),
        request.getYearPublished(),
        request.getTitle()
    );
    Page<Book> result = bookRepository.findAll(spec, pageable);
    List<com.profitsoft.bookstats.dto.BookListItem> items = result
        .map(bookMapper::toListItem)
        .getContent();
    return new BookPageResponse(items, result.getTotalPages());
  }

  @Transactional
  public List<Book> report(BookReportRequest request) {
    Specification<Book> spec = BookSpecifications.filter(
        request.getAuthorId(),
        request.getYearPublished(),
        request.getTitle()
    );
    return bookRepository.findAll(spec, Sort.by(Sort.Direction.ASC, "id"));
  }

  @Transactional
  public UploadResponse upload(MultipartFile file) {
    int imported = 0;
    int failed = 0;
    try (InputStream stream = file.getInputStream();
         JsonParser parser = jsonFactory.createParser(stream)) {
      if (parser.nextToken() != JsonToken.START_ARRAY) {
        throw new IllegalArgumentException("Expected JSON array");
      }
      while (parser.nextToken() != JsonToken.END_ARRAY) {
        ImportBookDto dto = objectMapper.readValue(parser, ImportBookDto.class);
        if (saveImported(dto)) {
          imported++;
        } else {
          failed++;
        }
      }
    } catch (IOException e) {
      throw new RuntimeException("Failed to process upload", e);
    }
    return new UploadResponse(imported, failed);
  }

  private boolean saveImported(ImportBookDto dto) {
    if (dto.getTitle() == null || dto.getTitle().isBlank() || dto.getAuthor() == null) {
      return false;
    }
    Author author = authorRepository.findByNameIgnoreCase(dto.getAuthor().trim())
        .orElse(null);
    if (author == null) {
      return false;
    }
    Book book = new Book();
    book.setAuthor(author);
    book.setTitle(dto.getTitle().trim());
    book.setYearPublished(dto.getYearPublished());
    book.setGenres(splitGenres(dto.getGenres()));
    bookRepository.save(book);
    return true;
  }

  private List<String> splitGenres(String value) {
    if (value == null || value.isBlank()) {
      return List.of();
    }
    String[] parts = value.split(",");
    List<String> genres = new ArrayList<>();
    for (String part : parts) {
      String trimmed = part.trim();
      if (!trimmed.isEmpty()) {
        genres.add(trimmed);
      }
    }
    return genres;
  }

  private Author findAuthor(Long authorId) {
    return authorRepository.findById(authorId)
        .orElseThrow(() -> new NotFoundException("Author not found: " + authorId));
  }
}
