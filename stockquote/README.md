# Stock Quote Sample

This sample shows a Stock Quote service. Incoming messages are routed via
WS-Addressing, and SOAP message content is handled using JAXB2. Additionally, 
this sample uses the HTTP server built into Java 6. 

## Running the Server

The server can be built and run using the following command from
within the ``server`` folder.

	```sh
	$ ./gradlew runServer
	```

## Running the Client(s)

There is a separate ``client`` directory containing clients that connect to the
server. You can run these clients by using the following command from within
each of client subdirectories:

	```sh
	$ gradle runClient
	```
		
## License

[Spring Web Services] is released under version 2.0 of the [Apache License].

[Spring Web Services]: https://projects.spring.io/spring-ws
[Apache License]: http://www.apache.org/licenses/LICENSE-2.0