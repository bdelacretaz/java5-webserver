package ch.codeconsult.funws.http;

/** Minimal set of HTTP protocol constants.
 *  Will need to be expanded for a real server! 
 *
 *  $Id: HttpConstants.java,v 1.8 2006/11/21 12:47:20 bdelacretaz Exp $
 */
public interface HttpConstants {
  String VERSION_0_9 = "HTTP/0.9";
  String VERSION_1_0 = "HTTP/1.0";
  String VERSION_1_1 = "HTTP/1.1";
  
  String H_CONTENT_LENGTH = "Content-Length";
  String H_CONTENT_TYPE = "Content-Type";
  String H_CONNECTION = "Connection";
  String H_HOST = "Host";
  String H_SERVER = "Server";

  String CONNECTION_CLOSE = "close"; 
  String CONNECTION_KEEPALIVE = "Keep-Alive"; 
  
  String HTTP_EOL = "\r\n";
  
  String VERB_GET = "GET";
  
  int HTTP_OK_RC = 200;
  String HTTP_OK_INFO = "OK";
  
  int HTTP_NOT_FOUND_RC = 404;
  String HTTP_NOT_FOUND_INFO = "Not Found";
  
  int HTTP_SERVER_ERROR_RC = 500;
  String HTTP_SERVER_ERROR_INFO = "Internal Server Error";
}
