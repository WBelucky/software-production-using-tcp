package myhttp;

public class Context {
  public final HttpRequest req;
  public final HttpResponse res;

  Context(HttpRequest req, HttpResponse res) {
    this.req = req;
    this.res = res;
  }
}