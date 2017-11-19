# LittleProxy Response Modifier
_Extension for LittleProxy that makes it easy to edit responses before forwarding them to the client._

There are many requests for how to set up [LittleProxy](https://github.com/adamfisk/LittleProxy) as a reverse proxy, and especially how to edit responses before forwarding them to the client. This extension allows you to:
* Quickly set up a LittleProxy implementation that intercepts and edits server responses.
* Edit responses through regular expressions or XSLT (or through your own implementation).

Check out the __ReverseProxy__ example to see how to set up this extension with a reverse proxy.

## Installation (Maven)
### Repository
```xml
<dependency>
	<groupId>nl.michielmeulendijk</groupId>
	<artifactId>lprm</artifactId>
	<version>1.0.1</version>
</dependency>
```

### Dependencies
```xml
<!-- original LittleProxy dependency -->
<dependency>
	<groupId>org.littleshoot</groupId>
	<artifactId>littleproxy</artifactId>
	<version>1.1.2</version>
</dependency>

<!-- Xalan is required if XSLTResponseModifierAdapter is used -->
<dependency>
	<groupId>xalan</groupId>
	<artifactId>xalan</artifactId>
	<version>2.7.2</version>
</dependency>
```
## How to use
Use the repository as follows:
* Add and override __ResponseModifierSourceAdapter__ as __FiltersSource__
* Override __filterRequest__ to return an class implementing __ResponseModifierAdapter__. Out of the box __RegExResponseModifierAdapter__ and __XSLTResponseModifierAdapter__ are available.
* The class implementing __ResponseModifierAdapter__ accepts as final argument any number of __URLFilter__ classes. Use the appropriate subclass to set up your filters. All filters contain a condition (i.e. a RegEx string to which URLs will be matched) and an action (i.e. an object that determines how the response will be edited).

An example of use with __RegExResponseModifierAdapter__:
```java
HttpProxyServer server =
	DefaultHttpProxyServer.bootstrap()
		.withPort(8080)
		.withFiltersSource(
			new ResponseModifierSourceAdapter() {
				@Override
				public HttpFilters filterRequest(HttpRequest originalRequest, ChannelHandlerContext ctx) {
					return new RegExResponseModifierAdapter(
						originalRequest, ctx,
						//set up URL filter
						new RegExURLFilter(
							//any URL matching this regex will invoke the filter's action 
							"^/testpage.html",
							//all HashMap entries contain a regex (key) that will be replaced with another
							//string (value) throughout the response text
							new HashMap<String, String>() {{
								//replace the title
								put("<title>.*<\\/title>", "<title>New Title!</title>");
								//append a paragraph to the body
								put("<body>", "<body><p>Hello world!</p>");
							}}
						)
					)
				}
			}
		).start()
  ```
  An example of use with __XSLTResponseModifierAdapter__:
  ```java
  HttpProxyServer server =
	DefaultHttpProxyServer.bootstrap()
		.withPort(8080)
		.withFiltersSource(
			new ResponseModifierSourceAdapter() {
				@Override
				public HttpFilters filterRequest(HttpRequest originalRequest, ChannelHandlerContext ctx) {
					return new XSLTResponseModifierAdapter(
						originalRequest, ctx,
						//set up URL filter
						new XSLTURLFilter(
							//any URL matching this regex will invoke the filter's action 
							"^/testpage.html",
							//an InputStream containing the XSL document with which the response will be transformed
							this.getClass().getClassLoader().getResourceAsStream("transform-testpage.xsl"),
							true
						)
					)
				}
			}
		).start()
  ```
__XSLTResponseModifierAdapter__ uses Xalan to perform its XSL transformations. Make sure you include it as a dependency if you go this route.

## Configuration
The code overrides __serverToProxyResponse__, checks if the request URL matches any __URLFilters__, and changes them if required. If you need to intercept the response after any of the filters, I recommend using __proxyToClientResponse__ so as not to get in the way of the response modifier.

By default, __getMaximum(Request/Response)BufferSizeInBytes__ returns +/- 10 MB buffers. If you need requests or responses larger than that, make sure to override them.

For an example of how to use the response modifier with a reverse proxy (including overriding example), check out the ReverseProxy class.
