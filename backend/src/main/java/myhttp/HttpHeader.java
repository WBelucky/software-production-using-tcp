package myhttp;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.stream.Stream;

public class HttpHeader {
  public final String text;
  public final String method;
  public final String path;

  public HttpHeader(final String text) throws UnsupportedEncodingException {
    this.text = text;
    final var first = this.text.split(IOUtil.crlf)[0];
    final var words = first.split("\\s+", 3);
    this.method = words[0].toUpperCase();
    this.path = URLDecoder.decode(words[1], "UTF-8");
  }
  public int getContentLength() {
    return Stream.of(this.text.split(IOUtil.crlf))
      .filter(l -> l.startsWith("Content-Length"))
      .map(l -> l.split(":")[1].trim())
      .mapToInt(Integer::parseInt)
      .findFirst().orElse(0);
  }

}