package app;

public class Message {
  public String id;
  public String type;
  public String content;
  public Message(String id, String type, String content) {
    this.id = id;
    this.type = type;
    this.content = content;
  }
  public Message() {}
}