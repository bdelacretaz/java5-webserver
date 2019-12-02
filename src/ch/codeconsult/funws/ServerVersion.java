package ch.codeconsult.funws;

/** Define this server's version
 *  
 *  $Id: ServerVersion.java,v 1.2 2006/11/21 12:47:20 bdelacretaz Exp $
 */
public class ServerVersion {
  public static String getServerVersionInfo() {
    // Keep this value in sync with the version.info found in build.xml
    return "codeconsult FunWS 2006-11-16";
  }
}
