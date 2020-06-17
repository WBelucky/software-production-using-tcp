package myhttp;

import java.io.IOException;

public class ZakoServerTest {
  public static void main(String[] args) {
    final var s = new ZakoServer();
    s.get("/", ctx -> {
      try {
        ctx.res.body("Hello, world").send();
      } catch (IOException e) {
        e.printStackTrace(System.err);
      }
    })
      .listenAndServe(8080);
  }
}