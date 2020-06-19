package myhttp;

import java.io.File;
import java.io.IOException;

public class ZakoServerTest {
  public static void main(String[] args) {
    final var s = new ZakoServer();
    s.get("/", ctx -> {
      final var file = new File("static/", "index.html");
      if (!file.exists() || !file.isFile()) {
        try {
          ctx.res.body("Home").send();
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        return;
      }
      try {
        ctx.res.sendFile(file);
      } catch (IOException e) {
        e.printStackTrace(System.err);
      }
    })
      .listenAndServe(8080);
  }
}
