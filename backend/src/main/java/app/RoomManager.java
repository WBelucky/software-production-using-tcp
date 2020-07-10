package app;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;

public class RoomManager {
  private final int maxPlayers = 2;
  private final Map<String, Room> idToRoom = new ConcurrentHashMap<>();
  private final PriorityBlockingQueue<Room> queue = new PriorityBlockingQueue<Room>(100,
      (final Room a, final Room b) -> {
        return a.getNumberOfPlayers() - b.getNumberOfPlayers();
      });
  private final ExecutorService service = Executors.newCachedThreadPool();

  private Room createRoom() {
    final var room = new Room();
    service.execute(() -> {
      try {
        room.process();
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        for (final var userId: room.getPlayerIds()) {
          idToRoom.remove(userId);
        }
      }
    });
    return room;
  }

  // 新しいルームかqueueからpopされたルームを返す.
  private Room searchAndEnterRoom(final String userId) {
    while (!queue.isEmpty() && queue.peek().getNumberOfPlayers() >= maxPlayers) {
      final var r = queue.poll();
      System.out.println("queue empty or full pop" + r.getNumberOfPlayers());
    }
    final var room = queue.isEmpty() ? createRoom() : queue.poll();
    room.addPlayer(userId);
    // queueに入れ直す
    queue.put(room);
    // どの部屋に入るべきかを残しておく.
    idToRoom.put(userId, room);
    return room;
  }

  public Room getRoom(final String userId) {
    final Room room;
    if (idToRoom.containsKey(userId)) {
      room = idToRoom.get(userId);
    } else {
      room = this.searchAndEnterRoom(userId);
    }
    System.out.println("room size" + idToRoom.size());
    return room;
  }
}