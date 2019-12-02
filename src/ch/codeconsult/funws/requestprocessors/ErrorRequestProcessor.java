package ch.codeconsult.funws.requestprocessors;

import java.io.IOException;
import java.io.Writer;

import ch.codeconsult.funws.http.HttpRequest;

/** A request processor that sends an error response 
 *  
 * $Id: ErrorRequestProcessor.java,v 1.3 2006/11/21 12:47:20 bdelacretaz Exp $
 */
public class ErrorRequestProcessor extends BaseRequestProcessor {

  private final int errorCode;
  private final String info;
  private final String body;
  private final HttpRequest req;
  
  /** @param info written on the HTTP result code line
   *  @param body plain text body of the response
   */
  public ErrorRequestProcessor(Writer out,HttpRequest req,int errorCode,String info,String body) {
    this.errorCode = errorCode;
    this.info = info;
    this.body = body;
    this.req = req;
  }
  
  /** BaseRequestProcessor requirement */
  public void sendResponse(Writer out) throws IOException {
    writeResponseCode(out,req.getProtocolVersion(),errorCode,info);
    writeHeader(out, H_CONTENT_TYPE, getContentType(".txt"));
    out.write(HTTP_EOL);
    out.write(body);
    out.write(HTTP_EOL);
    out.flush();
  }
}
