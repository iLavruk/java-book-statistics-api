package com.profitsoft.bookstats.dto;

import java.util.List;

public class BookResponse {

  private Long id;
  private String title;
  private Integer yearPublished;
  private List<String> genres;
  private AuthorResponse author;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
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

  public AuthorResponse getAuthor() {
    return author;
  }

  public void setAuthor(AuthorResponse author) {
    this.author = author;
  }
}
