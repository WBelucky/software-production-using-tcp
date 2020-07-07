package myhttp;

public enum Status {
  Ok("200 OK"),
  NotFound("404 Not Found")
  ;

  public final String text;

  private Status(String text) {
    this.text = text;
  }

  @Override
  public String toString() {
    return this.text;
  }
}