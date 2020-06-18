package myhttp;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class IOUtil {
  private IOUtil() {}
  public static final Charset UTF8 = StandardCharsets.UTF_8;
  public static final String crlf = "\r\n";

  public static void println(OutputStream out, String line) {
    print(out, line + crlf);
  }

  public static void print(OutputStream out, String line) {
    System.out.println(line);
    try {
      out.write(line.getBytes(UTF8));
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}