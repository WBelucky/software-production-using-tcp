package myhttp;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HttpResponse {
  private Status status = Status.Ok;
  private final Map<String, String> headers = new HashMap<>();
  private Optional<String> body = Optional.empty();
  private final Socket socket;

  public HttpResponse(Socket socket) {
    this.socket = socket;
  }

  public HttpResponse status(final Status status) {
    this.status = status;
    return this;
  }

  public HttpResponse header(final String propName, final Object value) {
    this.headers.put(propName, value.toString());
    return this;
  }

  public HttpResponse contentType(final ContentType contentType) {
    this.headers.put("Content-Type", contentType.toString());
    return this;
  }

  public void sendFile(final File file) {
    final var fileName = file.getName();
    final var fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1);
    final var first = "HTTP/1.1 " + String.valueOf(this.status);
    this.contentType(ContentType.toContentType(fileExtension));

    try (final var out = this.socket.getOutputStream();) {
      IOUtil.println(out, first);
      this.headers.forEach((k, v) -> {
        final var line = k + ": " + v;
        IOUtil.println(out, line);
      });
      IOUtil.println(out, "");
      Files.copy(file.toPath(), out);
    } catch (final Exception e) {
      System.err.println("failed to send");
      e.printStackTrace(System.err);
    } finally {
      try {
        this.socket.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public HttpResponse body(final String body) {
    this.body = Optional.of(body);
    return this;
  }

  public void send() {
    try (final var out = this.socket.getOutputStream();) {
      final var first = "HTTP/1.1 " + String.valueOf(this.status);
      IOUtil.println(out, first);
      this.headers.forEach((k, v) -> {
        final var line = k + ": " + v;
        IOUtil.println(out, line);
      });

      if (this.body.isPresent()) {
        IOUtil.println(out, "");
        IOUtil.print(out, body.get());
      }
    } catch (final Exception e) {
      System.err.println("failed to send");
      e.printStackTrace(System.err);
    } finally {
      try {
        this.socket.close();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }
}