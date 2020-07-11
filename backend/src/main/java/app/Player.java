package app;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

import myhttp.Context;
import util.Pair;

class Numeron {
  private ArrayList<Integer> numbers = new ArrayList<>(3);

  public Numeron(final String s) {
    numbers.add(getDigit(s, 0));
    numbers.add(getDigit(s, 1));
    numbers.add(getDigit(s, 2));

    if (numbers.get(0) == numbers.get(1) || numbers.get(0) == numbers.get(2) || numbers.get(1) == numbers.get(2)) {
      throw new IllegalArgumentException();
    }
  }

  public void changeNumber(final int index, final int num) {
    if (index < 0 || numbers.size() <= index) {
      throw new IllegalArgumentException();
    }
    numbers.set(index, num);
  }

  public Pair<Integer, Integer> eatAndBite(String s) {
    final var attack = stringToArrayList(s);
    final var notEat = new ArrayList<Integer>();
    var eat = 0;
    for (int i = 0; i < 3; i++) {
      final var a = attack.get(i);
      if (a == numbers.get(i)) {
        eat++;
      } else {
        notEat.add(a);
      }
    }
    var bite = 0;
    for (final var j : notEat) {
      if (numbers.contains(j)) {
        bite += 1;
      }
    }
    return new Pair<Integer, Integer>(eat, bite);
  }

  public int getDigit(String s, int index) {
    final var i = (int) s.charAt(index) - (int) '0';
    if (i < 0 || 10 <= i) {
      throw new IllegalArgumentException();
    }
    return i;
  }

  private ArrayList<Integer> stringToArrayList(String s) {
    final var a = new ArrayList<Integer>();
    a.add(getDigit(s, 0));
    a.add(getDigit(s, 1));
    a.add(getDigit(s, 2));

    if (a.get(0) == a.get(1) || a.get(0) == a.get(2) || a.get(1) == a.get(2)) {
      throw new IllegalArgumentException();
    }
    return a;
  }
}

public class Player {
  private long updatedAt = System.currentTimeMillis();
  private Random rand = new Random();
  private Optional<Context> context = Optional.empty();
  final public String id;
  private Optional<Numeron> numeron = Optional.empty();
  public Optional<String> name = Optional.empty();
  private final ArrayBlockingQueue<Pair<Context, Message>> queue = new ArrayBlockingQueue<>(100);

  public Player(String id) {
    this.id = id;
  }

  public void putContextAndMessage(final Context ctx, final Message message) {
    // 非同期で呼んでもOkにする.
    try {
      this.queue.put(new Pair<Context, Message>(ctx, message));
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public void updateContext(Context ctx) {
    this.context = Optional.of(ctx);
  }

  public Optional<Context> relaseContext() {
    final var ret = this.context;
    this.context = Optional.empty();
    return ret;
  }

  public Optional<Numeron> getNumeron() {
    return this.numeron;
  }

  public void setNumeron(final String s) {
    this.numeron = Optional.of(new Numeron(s));
  }

  public boolean sendMessageWhenHasContext(final String type, final String content) {
    final var c = this.relaseContext();
    if (c.isEmpty()) {
      return false;
    }
    c.get().res.body(new Message(this.id, type, content).toString()).send();
    return true;
  }

  // don't use without sendMessage
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
        try {
          Thread.sleep(100);
        } catch (InterruptedException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        continue;
      }
      final var p = queue.poll();
      final var ctx = p.first;
      final var m = p.second;
      this.updateContext(ctx);
      System.out.println("ok get1");
      return m;
    }
  }

  public Message waitInput() {
    this.sendMessageWhenHasContext("wait_input", "");
    return this.waitMessage();
  }

  public Message waitInputOfType(final String type) {
    System.out.println("start type waiting " + type);
    while (true) {
      final var m = this.waitInput();
      System.out.println("type " + m.type);
      System.out.println("expected " + type);
      if (type.equals(m.type)) {
        System.out.println("ok get2");
        return m;
      }
    }
  }

  public Message waitInputWithFilter(final java.util.function.Predicate<Message> predicate) {
    while (true) {
      final var m = waitInput();
      try {
        if (predicate.test(m)) {
          return m;
        }
      } catch (Throwable e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

  public void sendMessage(final String type, final String content) {
    boolean ok = this.sendMessageWhenHasContext(type, content);
    while (!ok) {
      this.waitMessage();
      ok = this.sendMessageWhenHasContext(type, content);
    }
  }
}