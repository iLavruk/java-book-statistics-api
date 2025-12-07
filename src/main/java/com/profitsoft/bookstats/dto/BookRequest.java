package com.profitsoft.bookstats.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

public class BookRequest {

  @NotBlank(message = "title is required")
  private String title;

  @NotNull(message = "authorId is required")
  private Long authorId;

  @Min(value = 1, message = "yearPublished must be positive")
  private Integer yearPublished;

  @Size(max = 10, message = "no more than 10 genres")
  private List<@Size(max = 50, message = "genre is too long") String> genres = new ArrayList<>();

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Long getAuthorId() {
    return authorId;
  }

  public void setAuthorId(Long authorId) {
    this.authorId = authorId;
  }

  public Integer getYearPublished() {
    return yearPublished;
  }

  public void setYearPublished(Integer yearPublished) {
    this.yearPublished = yearPublished;
  }

  public List<String> getGenres() {
    return genres;
  }

  public void setGenres(List<String> genres) {
    this.genres = genres;
  }
}
