package com.profitsoft.bookstats.dto;

import jakarta.validation.constraints.NotBlank;

public class AuthorRequest {

  @NotBlank(message = "name is required")
  private String name;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
