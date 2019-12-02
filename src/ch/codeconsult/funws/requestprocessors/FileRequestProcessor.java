package ch.codeconsult.funws.requestprocessors;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

import ch.codeconsult.funws.http.HttpRequest;

/** A request processor that sends the content of a file 
 *  
 * $Id: FileRequestProcessor.java,v 1.5 2006/11/21 12:47:20 bdelacretaz Exp $
 */
public class FileRequestProcessor extends BaseRequestProcessor {
  private final HttpRequest req;
  private final String basePath = System.getProperty("FileRequestProcessor.root","webroot");
  private static final Logger log = Logger.getLogger(FileRequestProcessor.class.getName());
  
  public FileRequestProcessor(HttpRequest req) {
    this.req = req;
  }
  
  public void sendResponse(Writer out) throws IOException {
    String path = req.getPath();
    if(path.endsWith("/")) {
      path += "index.html";
    }
    final File f = new File(basePath,path);
    FileReader fr = null;
    
    try {
      if(log.isLoggable(Level.INFO)) {
        log.info("Looking for file " + f.getCanonicalPath());
      }
      fr = new FileReader(f);
      writeOkResponsePreamble(out,req);
      writeHeader(out,H_CONTENT_TYPE,getContentType(f.getName()));
      writeHeader(out,H_CONTENT_LENGTH,String.valueOf(f.length()));
      out.write(HTTP_EOL);
      copy(fr,out);
      
    } finally {
      if(fr!=null) fr.close();
    }
  }
}
