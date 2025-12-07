package com.profitsoft.bookstats.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ImportBookDto {

  private String title;
  private String author;

  @JsonProperty("year_published")
  private Integer yearPublished;

  private String genres;

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public Integer getYearPublished() {
    return yearPublished;
  }

  public void setYearPublished(Integer yearPublished) {
    this.yearPublished = yearPublished;
  }

  public String getGenres() {
    return genres;
  }

  public void setGenres(String genres) {
    this.genres = genres;
  }
}
