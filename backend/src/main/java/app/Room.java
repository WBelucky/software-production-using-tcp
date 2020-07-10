package app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import myhttp.Context;
import myhttp.Status;
import util.Pair;

class PlayerManager {

}

public class Room {
  public String id = UUID.randomUUID().toString();

  private CopyOnWriteArrayList<String> playerIds = new CopyOnWriteArrayList<>();
  private final ArrayBlockingQueue<Pair<Context, Message>> queue = new ArrayBlockingQueue<>(100);

  private long updatedAt = System.currentTimeMillis();
  private Random rand = new Random();
  private ObjectMapper  mapper = new ObjectMapper();

  private HashMap<String, Player> idToPlayer = new HashMap<>();

  // do not use except from RoomManager
  public void addPlayer(final String playerId) {
    playerIds.add(playerId);
    final var p = new Player(playerId);
    this.idToPlayer.put(playerId, p);
  }

  public void removePlayer(Player player) {
    playerIds.remove(player.id);
    idToPlayer.remove(player.id);
  }

  public int getNumberOfPlayers() {
    return playerIds.size();
  }

  public CopyOnWriteArrayList<String> getPlayerIds() {
    return playerIds;
  }

  public void putContextAndMessage(final Context ctx, final Message message) {
    // 非同期で呼んでもOkにする.
    try {
      this.queue.put(new Pair<Context, Message>(ctx, message));
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  // empty means timeout
  private Message waitMessage() {
    while (true) {
        // 定期的に10分以上応答がなかったらプロセスを終了する.
      if (rand.nextInt(50) == 0) {
        final var cur = System.currentTimeMillis();
        final var timeout = (cur - this.updatedAt) > 6000000;
        this.updatedAt = cur;
        if (timeout) {
          throw new IllegalStateException();
        }
      }
      if (queue.isEmpty()) {
        continue;
      }
      final var p = queue.poll();
      final var ctx = p.first;
      final var message = p.second;
      this.idToPlayer.get(message.id).pushContext(ctx);
      return message;
    }
  }

  private Message waitMessageOfType(final String type) {
    while (true) {
      final var m = waitMessage();
      if ( type.equals(m.type) ) {
        return m;
      }
    }
  }

  private Message waitMessageWithFilter(final Predicate<Message> predicate) {
    while (true) {
      final var m = waitMessage();
      if ( predicate.test(m) ) {
        return m;
      }
    }
  }

  private void sendMessage(Player p, String type, String content) {
    var c = p.getContext();
    if (c.isEmpty()) {
      final var m = waitMessageWithFilter((mm) -> {
        return mm.id.equals(p.id);
      });
      // c will be updated
      c = p.getContext();
    }

    try {
      final var j = this.mapper.writeValueAsString(new Message(p.id, type, content));
      c.get().res.status(Status.Ok).body(j).send();
      c = p.getContext();
      final var release = this.mapper.writeValueAsString(new Message(p.id, "release", ""));
      while (!c.isEmpty()) {
        System.out.println("release");
        c.get().res.status(Status.Ok).body(release).send();
        c = p.getContext();
      }
    } catch (JsonProcessingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void process() {

    final var j1 = waitMessageOfType("join");
    final var p1 = this.idToPlayer.get(j1.id);
    p1.name = Optional.of(j1.content);
    this.sendMessage(p1, "join", this.id);

    final var j2 = waitMessageWithFilter((Message mm) ->
      !(mm.id.equals(p1.id)) && mm.type.equals("join"));
    final var p2 = this.idToPlayer.get(j2.id);
    p2.name = Optional.of(j2.content);
    this.sendMessage(p2, "match", p1.name.get());
    System.out.println("here");
    this.sendMessage(p1, "match", p2.name.get());

    while (p1.getNumeron().isEmpty() || p2.getNumeron().isEmpty()) {
      final var setNum = waitMessageOfType("set_number");
      final var p = idToPlayer.get(setNum.id);
      if (p.getNumeron().isEmpty()) {
        p.setNumeron(setNum.content);
        sendMessage(p, "set_number", setNum.content);
      }
    }
    this.sendMessage(p1, "game_start", "");
    this.sendMessage(p2, "game_start", "");

    var turn = 0;
    while (true) {
      final Player pAttack;
      final Player pTarget;
      if (turn % 2 == 0) {
        pAttack = p1;
        pTarget = p2;
      } else {
        pAttack = p2;
        pTarget = p1;
      }

      this.sendMessage(pAttack, "ask", "");
      final var ans1 = waitMessageWithFilter((m) -> m.type.equals("answer") && m.id.equals(pAttack.id));
      final var res1 = pTarget.getNumeron().orElseThrow().eatAndBite(ans1.content);
      if (res1.first == 3) {
        this.sendMessage(pAttack, "result", "win");
        this.sendMessage(pTarget, "result", "lose");
        return;
      }
      final var attackResult = ans1.content + "," + Integer.toString(res1.first) + "," + Integer.toString(res1.second);
      this.sendMessage(pAttack, "feedback", attackResult);
      this.sendMessage(pTarget, "feedback", attackResult);

      turn++;
    }


  }
}