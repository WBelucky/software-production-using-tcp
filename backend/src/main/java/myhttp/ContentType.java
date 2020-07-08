package myhttp;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

public enum ContentType {
  TextPlain("text/plain", "txt"),
  TextHTML("text/html", "html,htm"),
  TextCSS("text/css", "css"),
  ApplicationJavaScript("application/javascript", "js"),
  ApplicationJSON("application/json", "json"),
  ImageJPEG("image/jpeg", "jpg,jpeg"),
  ImagePNG("image/png", "png"),
  ;

  private static final Map<String, ContentType> contentTypes = new HashMap<>();

  static {
    Stream.of(ContentType.values())
      .forEach(contentType -> {
        contentType.extensions.forEach(extension -> {
          contentTypes.put(extension, contentType);
        });
      });
  }

  public final String text;
  public final Set<String> extensions = new HashSet<>();

  private ContentType(final String text, final String extensions) {
    this.text = text;
    this.extensions.addAll(Arrays.asList(extensions.split(",")));
  }

  @Override
  public String toString() {
    return this.text;
  }

  public static ContentType toContentType(final String extension) {
    Objects.requireNonNull(extension);
    return contentTypes.getOrDefault(extension, TextPlain);
  }
}
