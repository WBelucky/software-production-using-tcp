package myhttp;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.concurrent.ExecutorService;

public class ZakoServer {
  private final ExecutorService service = Executors.newCachedThreadPool();
  private final List<RouteTask> routeTasks = new ArrayList<RouteTask>();

  public void listenAndServe(final int port) {
    try (final var s = new ServerSocket(port)) {
      while (true) {
        this.process(s);
      }
    } catch (Exception e) {
      e.printStackTrace(System.err);
    }
  }

  public ZakoServer get(final String path, final Consumer<Context> procedure) {
    this.routeTasks.add(new RouteTask("GET", path, procedure));
    return this;
  }

  public ZakoServer post(final String path, final Consumer<Context> procedure) {
    this.routeTasks.add(new RouteTask("POST", path, procedure));
    return this;
  }

  public ZakoServer staticFilePath(final String path) {
    return this;
  }

  private void process(final ServerSocket s) throws IOException {
    final var socket = s.accept();
    // this.service.execute(() -> {
      try (
        final var in = socket.getInputStream();
        final var outputStream = socket.getOutputStream();
      ) {

        System.out.println("first");
        System.out.println(socket.isClosed()); 
        final var req = new HttpRequest(in);

        final var header = req.header;
        final var method = header.method;
        final var path = header.path;
        // System.out.println(header.text);


        System.out.println("before-if");
        System.out.println(socket.isClosed()); 
        if ("GET".equals(method)) {
          System.out.println("kita");
          // setFileで指定したところをstaticファイルにする
          final var file = new File("static/", header.path);
          if (file.exists() && file.isFile()) {
            new HttpResponse(outputStream).sendFile(file);
            return;
          }
        }
        System.out.println("after-if");
        System.out.println(socket.isClosed()); 
        final var task = this.routeTasks.stream().filter(t -> t.method.equals(method) && t.path.equals(path))
          .findFirst();
        if (task.isEmpty()) {
          // TODO: Not Found
          return;
        }
        System.out.println("before");
        System.out.println(socket.isClosed()); 
        task.get().procedure.accept(new Context(req, new HttpResponse(outputStream)));
        System.out.println("after");
        System.out.println(socket.isClosed()); 

      } catch (IOException e) {
        throw new UncheckedIOException(e);
      } finally {
        try {
          socket.close();
        } catch (IOException e) {
          e.printStackTrace(System.err);
        }
      }
    // });
  }
}