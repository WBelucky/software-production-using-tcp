package main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.ReentrantReadWriteLock;

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

class Player {
  private int id;
  public int getId() {
    return this.id;
  }
}

class Message {
  public int from;
  public String type;
  public String text;
}

class Room {
  private final ArrayList<Player> players = new ArrayList<>();
  private final ArrayBlockingQueue<Message> queue = new ArrayBlockingQueue<>(100);

  public int getNumberOfPlayers() {
    return this.players.size();
  }

  public void sendMessage(final Message message) {
    // 非同期で呼んでもOkにする.
    this.queue.add(message);
  }

  // スレッドプールに入れて, while文で回す
  public boolean update() {
    final var message = queue.poll();
  }

}

class RoomManager {
  private final int maxPlayers = 2;
  private final Map<Integer, Room> idToRoom = new ConcurrentHashMap<>();
  private final PriorityBlockingQueue<Room> queue = new PriorityBlockingQueue<Room>(100,
      (final Room a, final Room b) -> {
        return a.getNumberOfPlayers() - b.getNumberOfPlayers();
      }
  );

  private Room searchEnterableRoom() {
    while (!queue.isEmpty() && queue.peek().getNumberOfPlayers() >= maxPlayers) {
      queue.poll();
    }
    if (queue.isEmpty()) {
      return new Room();
    }
    return queue.poll();
  }

  public Room getRoom(final Player player) {
    final var room = idToRoom.getOrDefault(player.getId(), this.searchEnterableRoom() );
    idToRoom.put(player.getId(), room);
    return room;
  }
  public void exitRoom(final Player player) {
    idToRoom.remove(player.getId());
  }
}

public class Main {
  public static void main(final String[] args) {

    final var roomManager = new RoomManager();
    roomManager.getRoom(player)

    final var s = new HttpServer();
    final String json = "{\"id\":20, \"name\":\"HOGE\"}";

    final ObjectMapper mapper = new ObjectMapper();
    try {
      final Hoge hoge = mapper.readValue(json, Hoge.class);
      System.out.println(hoge);
    } catch (final IOException e) {
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
        Thread.sleep(30000);
      } catch (final InterruptedException e) {
        e.printStackTrace();
      }
      ctx.res.contentType(ContentType.TextPlain).body(new Date().toString()).send();
    });

    s.listenAndServe(8080);
  }
}
