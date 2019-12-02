package ch.codeconsult.funws;

import ch.codeconsult.funws.server.HttpServer;

/** Start the web server on specified socket 
 *  $Id: StartServer.java,v 1.5 2006/11/21 12:47:20 bdelacretaz Exp $
 */
public class StartServer {

  public static void main(String[] args) {
    try {
      if(args.length < 1) {
        usage();
        System.exit(1);
      }
      int port = integerArg(args,0);
      int nThreads = integerArg(args, 1);
      System.err.println("Starting HTTP server on port " + port);
      new HttpServer(port,nThreads).serverLoop();
      System.exit(0);
      
    } catch(Exception e) {
      fatal(e);
    }
  }

  private static void usage() {
    System.err.println("Usage: StartServer <port number> <number of threads>");
  }
  
  private static void fatal(Exception e) {
    System.err.println("Fatal error:");
    usage();
    e.printStackTrace();
    System.exit(1);
  }
  
  private static int integerArg(String [] args, int index) {
    String val = null;
    int result = 0;
    
    try {
      val = args[index];
      result = Integer.parseInt(val);
    } catch(Exception e) {
      fatal(new Exception("Invalid value " + val + " for integer parameter " + index));
    }
    
    return result;
  }
  
}
