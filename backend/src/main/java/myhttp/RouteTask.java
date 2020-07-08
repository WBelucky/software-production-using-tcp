package myhttp;

import java.util.function.Consumer;

public class RouteTask {
  public final String path;
  public final Consumer<Context> procedure;
  public final String method;

  RouteTask(final String method, final String path, final Consumer<Context> procedure) {
    this.path = path;
    this.procedure = procedure;
    this.method = method;
  }
}