# Anhttpserver library

## anhttpserver library (acronym for "Another HTTP Server") is a lightweight wrapper on top of Java 6 HTTP Server for easy functional testing

### Example

         HttpServer server = new DefaultHttpServer();
         server.start();

         server.addHandler("/index", new StringHandlerAdapter() {
             public byte[] getResponseAsString(HttpRequestContext httpRequestContext) throws IOException {
                 return "Hello world";
             }
         });
   
 
		now point your favorite browser to http://localhost:8000/index - you should see "Hello world"
		
### Using as a maven dependency

    <dependency>
        <groupId>anhttpserver</groupId>
        <artifactId>anhttpserver</artifactId>
        <version>0.2.7</version>
    </dependency>

NOTE: you need to add a maven repository to your pom.xml

    <repositories>
        <repository>
            <id>sprilukin-releases</id>
            <url>https://raw.github.com/sprilukin/mvn-repo/master/releases</url>
        </repository>
    </repositories>

