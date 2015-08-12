package de.herrschwarz.status4spring.inspectors;

import java.io.IOException;
import java.net.Socket;

public class SocketFactory {

  public static Socket createSocket(String host, int port) throws IOException {
    return new Socket(host, port);
  }
}
