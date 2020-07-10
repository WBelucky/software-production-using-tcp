package myhttp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.Optional;

public class HttpRequest {
  public static final String crlf = "\r\n";

  public final Optional<String> bodyText;
  public final HttpHeader header;

  public HttpRequest(final InputStream inStream) {
    try {
      final var input = new BufferedReader(new InputStreamReader(inStream, "UTF-8"));
      this.header = new HttpHeader(this.readHeader(input));
      this.bodyText = this.readBody(input);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private String readHeader(BufferedReader input) throws IOException {

    var line = input.readLine();
    final var header = new StringBuilder();

    while (line != null && !line.isEmpty()) {
      header.append(line + crlf);
      line = input.readLine();
    }
    return header.toString();
  }

  private Optional<String> readBody(BufferedReader input) throws IOException {
    final var contentLength = this.header.getContentLength();
    if (contentLength <= 0) {
      return Optional.empty();
    }
    var chars = new char[contentLength];
    input.read(chars);
    return Optional.of(new String(chars));
  }
}