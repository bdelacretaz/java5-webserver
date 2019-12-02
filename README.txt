FunWS: minimal HTTP webserver
------------------------------

Requirements
------------
To compile and run FunWS you need:

  JDK 1.5
  ant 
  	Only needed to recompile the code.
  	Tested with and 1.6.5 but should work with any recent version.
  
Compile and run
---------------
If you received a compiled jar, start it with

  java -cp build/dist/funws.2006-11-16.jar ch.codeconsult.funws.StartServer 9876 20

Otherwise, run "ant" in the directory that contains the build.xml file.

Once the server starts, connect to http://localhost:9876/ from a browser.

Limitations
-----------
Only a minimal subset of HTTP is implemented, adherence to the specs has not
been extensively checked.

Javadoc
-------------
If the javadoc has been generated it is found at build/javadoc/index.html .

To generate it, use "ant javadoc".
