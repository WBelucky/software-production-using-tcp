package myhttp;

import java.io.File;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HttpResponse {
  private int status = 200;
  private final Map<String, String> headers = new HashMap<>();
  private Optional<String> body = Optional.empty();
  private final OutputStream out;

  public HttpResponse(OutputStream out) {
    this.out = out;
  }
  public HttpResponse status(int status) {
    this.status = status;
    return this;
  }
  public HttpResponse header(String propName, Object value) {
    this.headers.put(propName, value.toString());
    return this;
  }
  public void sendFile(File file) {
    final var fileName = file.getName();
    final var fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1);
    // TODO: use fileExtension
    final var first =  "HTTP/1.1 " + String.valueOf(this.status) + " OK"; // TODO:
    this.header("Content-Type", "text/html");

    try {
      IOUtil.println(this.out, first);
      this.headers.forEach((k, v) -> {
          final var line = k + ": " + v;
          IOUtil.println(this.out, line);
      });
      IOUtil.println(this.out, "");
      Files.copy(file.toPath(), out);
    } catch (Exception e) {
      System.err.println("failed to send");
      e.printStackTrace(System.err);
    }
  }
  public HttpResponse body(String body) {
    this.body = Optional.of(body);
    return this;
  }
  public void send() {
    try {
      final var first = "HTTP/1.1 " + String.valueOf(this.status) + " OK"; // TODO:
      IOUtil.println(this.out, first);
      this.headers.forEach((k, v) -> {
          final var line = k + ": " + v;
          IOUtil.println(this.out, line);
      });

      if (this.body.isPresent()) {
        IOUtil.println(this.out,"");
        IOUtil.print(this.out, body.get());
      }
    } catch (Exception e) {
      System.err.println("failed to send");
      e.printStackTrace(System.err);
    }
  }
}