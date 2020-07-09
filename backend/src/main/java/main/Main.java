package main;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;

import myhttp.ContentType;
import myhttp.HttpResponse;
import myhttp.HttpServer;
import com.fasterxml.jackson.databind.ObjectMapper;

import app.RoomManager;

class Hoge {
  public int id;
  public String name;

  @Override
  public String toString() {
    return "Hoge [id=" + id + ", name=" + name + "]";
  }
}


public class Main {
  public static void main(final String[] args) {

    final var roomManager = new RoomManager();

    final var s = new HttpServer();
    final String json = "{\"id\":20, \"name\":\"HOGE\"}";
    final var queue = new ArrayBlockingQueue<HttpResponse>(100);

    final ObjectMapper mapper = new ObjectMapper();
    try {
      final Hoge hoge = mapper.readValue(json, Hoge.class);
      System.out.println(hoge);
    } catch (final IOException e) {
      e.printStackTrace();
    }

    s.get("/test", ctx -> {
      queue.add(ctx.res);
      System.out.println("pushed");
    });

    s.get("/test2", ctx -> {
      try {
        final var anotherRes = queue.take();
        anotherRes.body("another").send();
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      System.out.println("take");
    });

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
        Thread.sleep(30000);
      } catch (final InterruptedException e) {
        e.printStackTrace();
      }
      ctx.res.contentType(ContentType.TextPlain).body(new Date().toString()).send();
    });

    s.listenAndServe(8080);
  }
}
