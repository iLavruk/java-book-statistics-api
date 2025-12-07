package com.profitsoft.bookstats.dto;

public class UploadResponse {

  private int imported;
  private int failed;

  public UploadResponse() {
  }

  public UploadResponse(int imported, int failed) {
    this.imported = imported;
    this.failed = failed;
  }

  public int getImported() {
    return imported;
  }

  public void setImported(int imported) {
    this.imported = imported;
  }

  public int getFailed() {
    return failed;
  }

  public void setFailed(int failed) {
    this.failed = failed;
  }
}
