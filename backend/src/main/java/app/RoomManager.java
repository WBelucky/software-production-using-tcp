package app;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;

public class RoomManager {
  private final int maxPlayers = 2;
  private final Map<Integer, Room> idToRoom = new ConcurrentHashMap<>();
  private final PriorityBlockingQueue<Room> queue = new PriorityBlockingQueue<Room>(100,
      (final Room a, final Room b) -> {
        return a.getNumberOfPlayers() - b.getNumberOfPlayers();
      });
  private final ExecutorService service = Executors.newCachedThreadPool();

  private Room createRoom() {
    final var room = new Room();
    service.execute(() -> {
      try {
        while(room.process()) {}
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
    return room;
  }

  private Room searchEnterableRoom() {
    while (!queue.isEmpty() && queue.peek().getNumberOfPlayers() >= maxPlayers) {
      queue.poll();
    }
    if (queue.isEmpty()) {
      return createRoom();
    }
    return queue.poll();
  }

  public Room getRoom(final int userId) {
    final var room = idToRoom.getOrDefault(userId, this.searchEnterableRoom());
    idToRoom.put(userId, room);
    return room;
  }

  public void exitRoom(final int userId) {
    idToRoom.remove(userId);
  }
}