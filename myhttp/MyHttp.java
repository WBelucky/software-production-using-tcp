package myhttp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.util.Optional;

public class MyHttp {
  public static void main(final String[] args) throws Exception {
    System.out.println("start >>>");
    try (
      final var server = new ServerSocket(8080);
      final var socket = server.accept();
      final var input = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
    ) {
      // get header
      var line = input.readLine();
      final var header = new StringBuilder();

      var contentLength = 0;

      while (line != null && !line.isEmpty()) {
        if (line.startsWith("Content-Length")) {
          contentLength = Integer.parseInt(line.split(":")[1].trim());
        }

        header.append(line + "\n");
        line = input.readLine();
      }


      // contentLengthに示されたByte数だけBodyを読み取る.
      if (contentLength <= 0) {
        // TODO: POST だったら, 400 bad request を返す
        System.out.println(header);
        System.out.println("<<end");
        return;
      }

      final var chars = new char[contentLength];
      input.read(chars);
      final var body = new String(chars);

      // get body
      System.out.println(header);
      System.out.println(body);
    }
    System.out.println("<<end");
  }
}