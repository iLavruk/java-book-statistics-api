package com.profitsoft.bookstats.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class BookListRequest {

  private Long authorId;
  private Integer yearPublished;
  private String title;

  @Min(value = 1, message = "page must be at least 1")
  private Integer page = 1;

  @Min(value = 1, message = "size must be at least 1")
  @Max(value = 100, message = "size too big")
  private Integer size = 20;

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

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Integer getPage() {
    return page;
  }

  public void setPage(Integer page) {
    this.page = page;
  }

  public Integer getSize() {
    return size;
  }

  public void setSize(Integer size) {
    this.size = size;
  }
}
