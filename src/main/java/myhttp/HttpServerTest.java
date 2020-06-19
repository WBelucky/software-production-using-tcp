package myhttp;

import java.io.File;

public class HttpServerTest {
  public static void main(String[] args) {
    final var s = new HttpServer();

    s.get("/", ctx -> {
      final var file = new File("static/", "index.html");
      if (!file.exists() || !file.isFile()) {
        ctx.res.body("Home").send();
        return;
      }
      ctx.res.sendFile(file);
    });

    s.post("/", ctx -> {
      final var echoBody = ctx.req.bodyText.get();
      ctx.res.body("echo: " + echoBody).send();
    });
    
    s.get("/hoge", ctx -> {
      ctx.res.body("hoge").send();
    });

    s.listenAndServe(8080);
  }
}
