package ch.codeconsult.funws.http;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/** Model of an HTTP request, stores everything that we
 *  need to know about the request.
 *  
 *  For a real project we'd use the servlet interfaces,
 *  but this is good enough for now. 
 *  
 *  $Id: HttpRequest.java,v 1.9 2006/11/21 12:47:20 bdelacretaz Exp $
 */
public class HttpRequest implements HttpConstants {
  private String verb = VERB_GET;
  private String path = "";
  private String protocolVersion = VERSION_0_9;
  private boolean keepalive;
  private final Map <String,String> headers = new HashMap<String,String>();
  
  /** Indicates an error in protocol, in decoding headers, etc. */
  static class ProtocolException extends IOException {
    ProtocolException(String reason) { super(reason); }
  }
  
  /** Parse the first line of an HTTP request: GET ... HTTP/... */
  public void parseFirstLine(String firstRequestLine) throws ProtocolException {
    if(firstRequestLine==null) {
      throw new ProtocolException("Null first line");
    }
    final StringTokenizer tk = new StringTokenizer(firstRequestLine," ");
    
    if(!tk.hasMoreTokens()) throw new ProtocolException("Missing HTTP verb");
    verb = tk.nextToken();
    
    if(!tk.hasMoreTokens()) throw new ProtocolException("Missing request path");
    path = tk.nextToken();
    
    // no protocol given means 0.9, else we handle 1.0 or 1.1
    if(!tk.hasMoreTokens()) {
      protocolVersion = VERSION_0_9;
    } else {
      final String proto = tk.nextToken();
      if(VERSION_1_0.equals(proto)) {
        protocolVersion = VERSION_1_0;
      } else if(VERSION_1_1.equals(proto)) {
        protocolVersion = VERSION_1_1;
      } else {
        throw new ProtocolException("Invalid protocol version '" + proto + "'");
      }
    }
  }
  
  /** Parse one line of HTTP headers and store them in this object  */
  public boolean parseHeaderLine(String line) throws ProtocolException {
    boolean result = false;
    final int pos = line.indexOf(":");
    if(pos > 0) {
      final String name = line.substring(0,pos).trim();
      final String value = line.substring(pos + 1).trim();
      result = true;
      headers.put(name,value);
    } else {
      throw new ProtocolException("Bad header line format '" + line + "'");
    }
    return result;
  }

  /** MUST be called before sending response: 
   *  decide if keep-alive must be used, and compute other stuff
   *  if needed.
   */
  public void startingResponse() throws ProtocolException {
    // Keepalive rules differ between HTTP versions
    // see http://www.io.com/~maus/HttpKeepAlive.html for
    // an explanation (and the HTTP specs of course).
    //
    // This is a basic implementation that will need to
    // be improved: apparently keepalive is only valid
    // when the Content-Length header is specified, needs
    // clarification.
    if(VERSION_1_0.equals(protocolVersion)) {
      keepalive = CONNECTION_KEEPALIVE.equals(headers.get(H_CONNECTION));
      
    } else if(VERSION_1_1.equals(protocolVersion)) {
      keepalive = !CONNECTION_CLOSE.equals(headers.get(H_CONNECTION));
      
    } else {
      keepalive = false;
    }
    
    // Check that client provided all required headers
    if(VERSION_1_1.equals(protocolVersion)) {
      if(headers.get(H_HOST) == null) {
        throw new ProtocolException(H_HOST + " header is required for " + VERSION_1_1);
      }
    }
  }

  public String getPath() {
    return path;
  }

  public String getProtocolVersion() {
    return protocolVersion;
  }

  public String getVerb() {
    return verb;
  }
  
  public boolean isKeepalive() {
    return keepalive;
  }
}
