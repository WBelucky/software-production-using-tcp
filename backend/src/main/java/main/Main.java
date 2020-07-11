package main;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;

import myhttp.ContentType;
import myhttp.HttpResponse;
import myhttp.HttpServer;

import app.Message;
import app.RoomManager;

class Hoge {
  public int id;
  public String name;

  public Hoge(int id, String name) {
    this.id = id;
    this.name = name;
  }
  public Hoge() {}

  @Override
  public String toString() {
    return "Hoge [id=" + id + ", name=" + name + "]";
  }
}


public class Main {
  public static void main(final String[] args) {
    final var port = Integer.parseInt(args.length > 1  ? args[1] : "8080");
    final var roomManager = new RoomManager();

    final var s = new HttpServer();
    final var queue = new ArrayBlockingQueue<HttpResponse>(100);

    // json test

    s.post("/api/game", ctx -> {
      final var body = ctx.req.bodyText;
      if (body.isEmpty()) {
        ctx.res.body("error: has no body").send();
        return;
      }
      Message message = new Message(body.get());
      if (message.id.equals("none")) {
        final var id = UUID.randomUUID().toString();
        message =  new Message(id, message.type, message.content);
        ctx.res.body(message.toString()).send();
        return;
      }
      final var room = roomManager.getRoom(message.id);

      room.putContextAndMessage(ctx, message);
    });

    s.get("/test", ctx -> {
      queue.add(ctx.res);
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

    // long polling test
    s.get("/subscribe", ctx -> {
      try {
        Thread.sleep(30000);
      } catch (final InterruptedException e) {
        e.printStackTrace();
      }
      ctx.res.contentType(ContentType.TextPlain).body(new Date().toString()).send();
    });

    s.listenAndServe(port);
  }
}
