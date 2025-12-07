package com.profitsoft.bookstats.dto;

public class BookReportRequest {

  private Long authorId;
  private Integer yearPublished;
  private String title;

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
}
