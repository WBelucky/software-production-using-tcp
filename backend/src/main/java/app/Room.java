package app;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;

import myhttp.Context;

public class Room {
  public String id = UUID.randomUUID().toString();

  private CopyOnWriteArrayList<String> playerIds = new CopyOnWriteArrayList<>();

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
    final var p = this.idToPlayer.get(message.id);
    p.putContextAndMessage(ctx, message);
  }

  public void process() {
    while (playerIds.size() != 2) {
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    final var p1 = idToPlayer.get((playerIds.get(0)));
    final var p2 = idToPlayer.get((playerIds.get(1)));

    final var j1 = CompletableFuture.supplyAsync(() -> p1.waitInputOfType("join"));
    final var j2 = CompletableFuture.supplyAsync(() -> p2.waitInputOfType("join"));
    final var cfs = CompletableFuture.allOf(j1, j2).whenComplete((ret, ex) -> {
      if (ex != null) {
        System.err.println(ex);
      }
      try {
        j1.get();
        j2.get();
        System.out.println("kokora");
        p1.sendMessage("match", p2.id);
        System.out.println("kokora");
        p2.sendMessage("match", p1.id);
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (ExecutionException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      System.out.println("koko");
    });
    System.out.println("koko2");



    // final var j1 = waitMessageOfType("join");
    // final var p1 = this.idToPlayer.get(j1.id);
    // p1.name = Optional.of(j1.content);
    // this.sendMessage(p1, "join", this.id);

    // final var j2 = waitMessageWithFilter((Message mm) ->
    //   !(mm.id.equals(p1.id)) && mm.type.equals("join"));
    // final var p2 = this.idToPlayer.get(j2.id);
    // p2.name = Optional.of(j2.content);
    // this.sendMessage(p2, "match", p1.name.get());
    // System.out.println("here");
    // this.sendMessage(p1, "match", p2.name.get());

    // while (p1.getNumeron().isEmpty() || p2.getNumeron().isEmpty()) {
    //   final var setNum = waitMessageOfType("set_number");
    //   final var p = idToPlayer.get(setNum.id);
    //   if (p.getNumeron().isEmpty()) {
    //     p.setNumeron(setNum.content);
    //     sendMessage(p, "set_number", setNum.content);
    //   }
    // }
    // this.sendMessage(p1, "game_start", "");
    // this.sendMessage(p2, "game_start", "");

    // var turn = 0;
    // while (true) {
    //   final Player pAttack;
    //   final Player pTarget;
    //   if (turn % 2 == 0) {
    //     pAttack = p1;
    //     pTarget = p2;
    //   } else {
    //     pAttack = p2;
    //     pTarget = p1;
    //   }

    //   this.sendMessage(pAttack, "ask", "");
    //   final var ans1 = waitMessageWithFilter((m) -> m.type.equals("answer") && m.id.equals(pAttack.id));
    //   final var res1 = pTarget.getNumeron().orElseThrow().eatAndBite(ans1.content);
    //   if (res1.first == 3) {
    //     this.sendMessage(pAttack, "result", "win");
    //     this.sendMessage(pTarget, "result", "lose");
    //     return;
    //   }
    //   final var attackResult = ans1.content + "," + Integer.toString(res1.first) + "," + Integer.toString(res1.second);
    //   this.sendMessage(pAttack, "feedback", attackResult);
    //   this.sendMessage(pTarget, "feedback", attackResult);

    //   turn++;
    // }


  }
}