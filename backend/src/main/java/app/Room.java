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
    final var afterJoin = CompletableFuture.allOf(j1, j2).whenComplete((res, ex) -> {
      if (ex != null) {
        System.err.println(ex);
      }
      try {
        final var m1 = j1.get();
        final var m2 = j2.get();
        p1.sendMessage("set_number", m2.content);
        p2.sendMessage("set_number", m1.content);
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (ExecutionException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      System.out.println("koko");
    })
    .thenCompose(s -> {
      System.out.println("here");
      final var setNum1 = CompletableFuture.supplyAsync(() -> p1.waitInputOfType("set_number"));
      final var setNum2 = CompletableFuture.supplyAsync(() -> p2.waitInputOfType("set_number"));
      return CompletableFuture.allOf(setNum1, setNum2)
        .whenComplete((res, ex) -> {
          if (ex != null) {
            System.err.println(ex);
          }
          try {
            final var num1 = setNum1.get();
            final var num2 = setNum2.get();
            p1.setNumeron(num1.content);
            p1.sendMessage("set_number", num1.content);
            p2.setNumeron(num2.content);
            p2.sendMessage("set_number", num2.content);
          } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
      });
    }).whenComplete((res, ex) -> {
      if (ex != null) {
        System.err.println(ex);
      }
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

        pAttack.sendMessage("attack", "");
        final var ans1 = pAttack.waitInputOfType("attack");
        final var res1 = pTarget.getNumeron().orElseThrow().eatAndBite(ans1.content);
        if (res1.first == 3) {
          pAttack.sendMessage("result", "win");
          pTarget.sendMessage("result", "lose");
          return;
        }
        final var attackResult = ans1.content + "," + Integer.toString(res1.first) + "," + Integer.toString(res1.second);
        pAttack.sendMessage("feedback", attackResult);
        pTarget.sendMessage("feedback", attackResult);
        turn++;
      }
    });

    // TODO: onComplete
    System.out.println("end");
  }
}