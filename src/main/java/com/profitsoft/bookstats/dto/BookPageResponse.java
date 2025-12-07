package com.profitsoft.bookstats.dto;

import java.util.List;

public class BookPageResponse {

  private List<BookListItem> list;
  private int totalPages;

  public BookPageResponse() {
  }

  public BookPageResponse(List<BookListItem> list, int totalPages) {
    this.list = list;
    this.totalPages = totalPages;
  }

  public List<BookListItem> getList() {
    return list;
  }

  public void setList(List<BookListItem> list) {
    this.list = list;
  }

  public int getTotalPages() {
    return totalPages;
  }

  public void setTotalPages(int totalPages) {
    this.totalPages = totalPages;
  }
}
