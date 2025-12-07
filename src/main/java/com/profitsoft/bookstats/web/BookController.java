package com.profitsoft.bookstats.web;

import com.profitsoft.bookstats.dto.BookListRequest;
import com.profitsoft.bookstats.dto.BookPageResponse;
import com.profitsoft.bookstats.dto.BookReportRequest;
import com.profitsoft.bookstats.dto.BookRequest;
import com.profitsoft.bookstats.dto.BookResponse;
import com.profitsoft.bookstats.dto.UploadResponse;
import com.profitsoft.bookstats.service.BookService;
import jakarta.validation.Valid;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/books")
public class BookController {

  private final BookService bookService;

  public BookController(BookService bookService) {
    this.bookService = bookService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public BookResponse create(@RequestBody @Valid BookRequest request) {
    return bookService.create(request);
  }

  @GetMapping("/{id}")
  public BookResponse findById(@PathVariable Long id) {
    return bookService.findById(id);
  }

  @PutMapping("/{id}")
  public BookResponse update(@PathVariable Long id, @RequestBody @Valid BookRequest request) {
    return bookService.update(id, request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    bookService.delete(id);
  }

  @PostMapping("/_list")
  public BookPageResponse list(@RequestBody @Valid BookListRequest request) {
    return bookService.list(request);
  }

  @PostMapping("/_report")
  public ResponseEntity<byte[]> report(@RequestBody BookReportRequest request) {
    List<com.profitsoft.bookstats.domain.Book> books = bookService.report(request);
    StringBuilder builder = new StringBuilder();
    builder.append("id,title,author,year_published,genres\n");
    for (com.profitsoft.bookstats.domain.Book book : books) {
      builder.append(book.getId()).append(",");
      builder.append(escapeCsv(book.getTitle())).append(",");
      builder.append(escapeCsv(book.getAuthor().getName())).append(",");
      builder.append(book.getYearPublished() == null ? "" : book.getYearPublished()).append(",");
      builder.append(escapeCsv(String.join(";", book.getGenres()))).append("\n");
    }

    byte[] body = builder.toString().getBytes(StandardCharsets.UTF_8);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.parseMediaType("text/csv"));
    headers.setContentDisposition(ContentDisposition.attachment().filename("books_report.csv").build());
    headers.set(HttpHeaders.CONTENT_LENGTH, String.valueOf(body.length));
    return new ResponseEntity<>(body, headers, HttpStatus.OK);
  }

  @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public UploadResponse upload(@RequestParam("file") MultipartFile file) {
    return bookService.upload(file);
  }

  private String escapeCsv(String value) {
    if (value == null) {
      return "";
    }
    String escaped = value.replace("\"", "\"\"");
    if (escaped.contains(",") || escaped.contains("\"") || escaped.contains("\n")) {
      return "\"" + escaped + "\"";
    }
    return escaped;
  }
}
