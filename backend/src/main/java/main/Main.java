package main;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import myhttp.ContentType;
import myhttp.HttpServer;
import com.fasterxml.jackson.databind.ObjectMapper;

class Hoge {
  public int id;
  public String name;

  @Override
  public String toString() {
    return "Hoge [id=" + id + ", name=" + name + "]";
  }
}

public class Main {
  public static void main(String[] args) {
    final var s = new HttpServer();
    String json = "{\"id\":20, \"name\":\"HOGE\"}";

    ObjectMapper mapper = new ObjectMapper();
    try {
      Hoge hoge = mapper.readValue(json, Hoge.class);
      System.out.println(hoge);
    } catch (IOException e) {
      e.printStackTrace();
    }

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

    s.get("/subscribe", ctx -> {
      try {
        Thread.sleep(4000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      ctx.res.contentType(ContentType.TextPlain).body(new Date().toString()).send();
    });

    s.listenAndServe(8080);
  }
}
