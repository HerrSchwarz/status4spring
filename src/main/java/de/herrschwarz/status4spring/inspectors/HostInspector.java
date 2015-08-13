package de.herrschwarz.status4spring.inspectors;

import static java.lang.String.format;
import static java.lang.invoke.MethodHandles.lookup;
import static java.util.Locale.ENGLISH;
import static java.util.Locale.ROOT;
import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import java.io.IOException;
import java.net.Socket;

public class HostInspector implements HealthInspector {

  private String name;
  private String host;
  private int port;

  private static final Logger LOG = getLogger(lookup().lookupClass());

  public HostInspector(String name, String host, int port) {
    this.name = name;
    this.host = host;
    this.port = port;
  }

  public InspectionResult inspect() {
    Socket socket = null;
    boolean reachable = false;
    String message = format(ROOT, "host: %s:%d", host, port);
    try {
      socket = SocketFactory.createSocket(host, port);
      reachable = true;
    } catch (IOException e) {
      message = format(ENGLISH, "Could not reach host %s on port %d", host, port);
      LOG.error(message, e);
    } finally {
      if (socket != null) {
        try {
          socket.close();
        } catch (IOException e) {
          message = format(ENGLISH, "Error closing socket: %s on port %d", host, port);
          LOG.error(message, e);
        }
      }
    }

    return new InspectionResult(name, reachable, message);
  }

  public String getName() {
    return name;
  }
}
