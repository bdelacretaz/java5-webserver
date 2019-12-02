package ch.codeconsult.funws.server;

import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import ch.codeconsult.funws.http.HttpConnectionHandler;

/** The server main loop: accept connections and delegate to handlers 
 *  
 *  $Id: HttpServer.java,v 1.3 2006/11/21 12:47:20 bdelacretaz Exp $
 */
public class HttpServer {
  
  private final ServerSocket socket;
  private final ExecutorService pool;

  private static final Logger log = Logger.getLogger(HttpServer.class.getName());
  
  public HttpServer(int port, int threadPoolSize) throws Exception {
    socket = new ServerSocket(port);
    pool = Executors.newFixedThreadPool(threadPoolSize);
    
    log.info("port=" + port + ", thread pool size=" + threadPoolSize);
  }
  
  public void serverLoop() throws Exception {
    try {
      while(true) {
        pool.execute(new HttpConnectionHandler(socket.accept()));
      }
    } finally {
      pool.shutdown();
    }
  }
}
