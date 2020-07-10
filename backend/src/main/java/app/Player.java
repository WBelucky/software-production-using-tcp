package app;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import myhttp.Context;
import util.Pair;

class Numeron {
  private ArrayList<Integer> numbers = new ArrayList<>(3);
  public Numeron(final String s ) {
    numbers.add(getDigit(s, 0));
    numbers.add(getDigit(s, 1));
    numbers.add(getDigit(s, 2));

    if ( numbers.get(0) == numbers.get(1) || numbers.get(0) == numbers.get(2) || numbers.get(1) == numbers.get(2)) {
      throw new IllegalArgumentException();
    }
  }
  public void changeNumber(final int index, final int num) {
    if (index < 0 || numbers.size() <=index) {
      throw new IllegalArgumentException();
    }
    numbers.set(index, num);
  }
  public Pair<Integer, Integer> eatAndBite(String s) {
    final var attack = stringToArrayList(s);
    final var notEat = new ArrayList<Integer>();
    var eat = 0;
    for(int i = 0; i < 3; i++) {
      final var a = attack.get(i);
      if(a == numbers.get(i)) {
        eat++;
      } else {
        notEat.add(a);
      }
    }
    var bite = 0;
    for (final var j: notEat) {
      if (numbers.contains(j)) {
        bite += 1;
      }
    }
    return new Pair<Integer, Integer>(eat, bite);
  }
  public int getDigit(String s, int index ) {
    final var i = (int)s.charAt(index) - (int)'0';
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

    if ( a.get(0) == a.get(1) || a.get(0) == a.get(2) || a.get(1) == a.get(2)) {
      throw new IllegalArgumentException();
    }
    return a;
  }
}

public class Player {
  private final ArrayDeque<Context> context = new ArrayDeque<>();
  final public String id;
  private Optional<Numeron> numeron = Optional.empty();
  public Optional<String> name = Optional.empty();


  public Player(String id) {
    this.id = id;
  }

  public Optional<Context> getContext() {
    if (this.context.isEmpty()) {
      return Optional.empty();
    }
    System.out.println(this.id + " poped");
    System.out.println("length " + this.context.size());
    return Optional.of(this.context.poll());
  }

  public void pushContext(Context ctx) {
    this.context.add(ctx);
    System.out.println(this.id + " pushed");
    System.out.println("length " + this.context.size());
  }

  public Optional<Numeron> getNumeron() {
    return this.numeron;
  }
  public void setNumeron(final String s) {
    this.numeron = Optional.of(new Numeron(s));
  }

}