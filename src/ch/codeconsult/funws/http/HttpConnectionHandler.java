package ch.codeconsult.funws.http;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import ch.codeconsult.funws.http.HttpRequest.ProtocolException;
import ch.codeconsult.funws.requestprocessors.ErrorRequestProcessor;
import ch.codeconsult.funws.requestprocessors.FileRequestProcessor;

/**
 * Process HTTP connections: read requests and delegate to request processors.
 * See http://www.w3.org/Protocols/ for the HTTP specs.
 *
 *  $Id: HttpConnectionHandler.java,v 1.11 2006/11/21 12:47:20 bdelacretaz Exp $
 */
public class HttpConnectionHandler implements Runnable, HttpConstants {
  private final Socket client;
  private HttpRequest req;
  private final BufferedReader in;
  private final Writer out;
  private String threadName;
  private static final Logger log = Logger.getLogger(HttpConnectionHandler.class.getName());

  // Timeouts for socket reads - should be made configurable
  private static final int FIRST_LINE_READ_TIMEOUT_MSEC = 5000;
  private static final int HEADER_LINE_READ_TIMEOUT_MSEC = 15000;

  /** Handle an HTTP conversation on the given Socket */
  public HttpConnectionHandler(Socket client) throws Exception {
    this.client = client;
    in = new BufferedReader(new InputStreamReader(client.getInputStream()));
    out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())));
  }

  /** Runnable: process request(s) and handle exceptions */
  public void run() {
    threadName = "Thread " + Thread.currentThread().getName();
    
    try {
      if(log.isLoggable(Level.INFO)) {
        log.info(threadName + ": connection accepted");
      }
      
      requestLoop();
      
    } catch(SocketTimeoutException ste) {
      // client disappeared? happens in keepalive, not a real problem
      if(log.isLoggable(Level.INFO)) {
        log.info("SocketTimeoutException while reading request from client, disconnecting");
      }
      
    } catch(FileNotFoundException fnfe) {
      // send 404 response
      sendError(new ErrorRequestProcessor(out,req,HTTP_NOT_FOUND_RC,HTTP_NOT_FOUND_INFO,fnfe.toString()));
      
    } catch(Exception e) {
      // For now, other exceptions are considered "internal server errors"
      if(log.isLoggable(Level.INFO)) {
        log.info(e.toString());
      }
      sendError(new ErrorRequestProcessor(out,req,HTTP_SERVER_ERROR_RC,HTTP_SERVER_ERROR_INFO,e.toString()));
      
    } finally {
      // cleanup socket
      try {
        client.close();
        if(log.isLoggable(Level.INFO)) {
          log.info(threadName + ": Connection closed");
        }
      } catch(Exception e) {
        // there's not much we can do about this one
        log.fine("Exception in client.close(): " + e);
      }
    }
  }
  
  /** Main request loop:  
   *  keep processing requests until keepalive becomes false,
   *  or until client disconnects.
   */ 
  protected void requestLoop() throws IOException {
    while(true) {
      // read first request line, GET ....
      client.setSoTimeout(FIRST_LINE_READ_TIMEOUT_MSEC);
      req = new HttpRequest();
      if(!readFirstLine()) {
        log.info("No more data from client, disconnecting");
        break;
      }
      
      // read HTTP headers (not for HTTP 0.9) 
      if(!req.getProtocolVersion().equals(VERSION_0_9)) {
        client.setSoTimeout(HEADER_LINE_READ_TIMEOUT_MSEC);
        readHeaders();
      }

      // process and send response
      sendResponse();
      
      // exit unless req object declares keepalive
      if(req.isKeepalive()) {
        log.info("Keepalive - reading next request");
      } else {
        break;
      }
    }
  }

  /** Send an error response to client, or log a message if unable to */
  private void sendError(ErrorRequestProcessor bcp) {
    try {
      bcp.sendResponse(out);
    } catch (Exception e) {
      log.warning("Exception while trying to send error response: " + e);
    }
  }

  /** Read the first HTTP request line 
   *  @return false if no more data to read
   * */
  protected boolean readFirstLine() throws ProtocolException, IOException {
    final String line = in.readLine();
    if(line==null) return false;
    req.parseFirstLine(line);
    if (log.isLoggable(Level.INFO)) {
      log.info(threadName + ": " + req.getVerb() + " " + req.getPath() + " "
          + req.getProtocolVersion());
    }
    return true;
  }

  /** Read all HTTP request headers */
  protected void readHeaders() throws ProtocolException, IOException {
    String headerLine = null;
    while ((headerLine = in.readLine()).length() > 0) {
      req.parseHeaderLine(headerLine);
    }
  }

  /** dispatch (simplistic for now) and send response to client */
  protected void sendResponse() throws IOException {
    req.startingResponse();
    if (!VERB_GET.equals(req.getVerb())) {
      throw new HttpRequest.ProtocolException(
          req.getVerb()
          + ": invalid verb, only " + VERB_GET + " is supported"
      );
    }
    new FileRequestProcessor(req).sendResponse(out);
    out.flush();
  }
}