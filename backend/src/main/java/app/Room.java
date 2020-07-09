package app;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import myhttp.Context;
import util.Pair;

public class Room {

  private AtomicInteger playerCount = new AtomicInteger(0);
  private final ArrayBlockingQueue<Pair<Context, Message>> queue = new ArrayBlockingQueue<>(100);

  public int getNumberOfPlayers() {
    return playerCount.get();
  }

  public void sendMessage(final Context ctx, final Message message) {
    // 非同期で呼んでもOkにする.
    try {
      this.queue.put(new Pair<Context, Message>(ctx, message));
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  // スレッドプールに入れて, while文で回す
  // end => return false
  public boolean process() {
    final var message = queue.poll();
    // do somthing whit message
    return true;
  }
}