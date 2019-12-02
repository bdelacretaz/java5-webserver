package ch.codeconsult.funws.requestprocessors;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import ch.codeconsult.funws.ServerVersion;
import ch.codeconsult.funws.http.HttpConstants;
import ch.codeconsult.funws.http.HttpRequest;

/** Base class for request processors, common behavior
 *  and utilities.
 *  
 * $Id: BaseRequestProcessor.java,v 1.7 2006/11/21 12:47:20 bdelacretaz Exp $
 */
public abstract class BaseRequestProcessor implements HttpConstants {
  
  public abstract void sendResponse(Writer out) throws IOException;

  /** Default content type, easily recognizable to detect missing types */ 
  private static final String DEFAULT_CONTENT_TYPE = "application/x-TODO:BaseRequestProcessor_NO_CONTENT_TYPE";
  
  /** TODO: make this configurable */ 
  private final static Map<String,String> contentTypes = new HashMap<String,String>();
  static {
    contentTypes.put("txt","text/plain");
    contentTypes.put("html","text/html");
    contentTypes.put("xml","text/xml");
    contentTypes.put("jpg","image/jpeg");
    contentTypes.put("jpeg","image/jpeg");
    contentTypes.put("gif","image/gif");
    contentTypes.put("png","image/png");
    contentTypes.put("css","text/css");
    contentTypes.put("js","application/x-javascript");
  }

  /** Write a single header to out */
  protected void writeHeader(Writer out, String name,String value) throws IOException {
    out.write(name);
    out.write(": ");
    out.write(value);
    out.write(HTTP_EOL);
  }
  
  /** Must be called at the beginning of a successful response */
  protected void writeOkResponsePreamble(Writer out,HttpRequest req) throws IOException {
    writeResponseCode(out,req.getProtocolVersion(),HTTP_OK_RC,HTTP_OK_INFO);
    writeHeader(out,H_SERVER,ServerVersion.getServerVersionInfo());
    if(req.isKeepalive() && VERSION_1_0.equals(req.getProtocolVersion())) {
      writeHeader(out, H_CONNECTION, CONNECTION_KEEPALIVE);
    }
  }
  
  /** Write an HTTP response code to out */
  protected void writeResponseCode(Writer out, String protocolVersion, int code, String info) throws IOException {
    out.write(protocolVersion);
    out.write(" ");
    out.write(String.valueOf(code));
    out.write(" ");
    out.write(info);
    out.write(HTTP_EOL);
  }
  
  /** Get Content-Type for given filename, or DEFAULT_CONTENT_TYPE 
   *  if we don't have it.
   */ 
  protected String getContentType(String filename) {
    String result = DEFAULT_CONTENT_TYPE;
    
    final int pos = filename.lastIndexOf('.');
    if(pos >= 0) {
      final String ext = filename.substring(pos + 1);
      result = contentTypes.get(ext);
      if(result==null) {
        result = DEFAULT_CONTENT_TYPE + " (" + ext + ")";
      }
    }
    
    return result;
  }
  
  /** Copy the contents of r to w */
  protected void copy(Reader r,Writer w) throws IOException {
    final int BUFFER_SIZE = 16384;
    final char [] buffer = new char[BUFFER_SIZE];
    int count;
    
    while( (count=r.read(buffer)) > 0) {
      w.write(buffer,0,count);
    }
  }
}
