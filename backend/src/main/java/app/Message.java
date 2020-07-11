package app;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Message {
  public String id;
  public String type;
  public String content;
  public Message(String id, String type, String content) {
    this.id = id;
    this.type = type;
    this.content = content;
  }
  public Message(String json) {
    Pattern p = Pattern.compile("\\{\"id\":\"(.*)\",\"type\":\"(.*)\",\"content\":\"(.*)\"\\}");
    Matcher m = p.matcher(json);
    if (m.matches()) {
      this.id = m.group(1);
      this.type = m.group(2);
      this.content = m.group(3);
      return;
    }
  }
  @Override
  public String toString() {
    return "{\"id\":\"" + id + "\",\"type\":\"" + type + "\",\"content\":\"" + content + "\"}";
  }
}