# Weather Forecast Sample

This sample shows how to use Spring Web Services to connect to the [CDYNE Weather Service].
It uses JAXB to generate classes from the service's WSDL, and the `WebServiceTemplate` to
send the request to and receive the response from the service.

You can run the client by using the following command from within this directory:

	```sh
	$ gradle runClient
	```
		
## License

[Spring Web Services] is released under version 2.0 of the [Apache License].

[Spring Web Services]: https://projects.spring.io/spring-ws
[Apache License]: http://www.apache.org/licenses/LICENSE-2.0
[CDYNE Weather Service]: https://wiki.cdyne.com/index.php/CDYNE_Weather