package nl.michielmeulendijk.lprm;

import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.littleshoot.proxy.HttpFiltersAdapter;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpHeaders.Names;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;

public abstract class ResponseModifierAdapter extends HttpFiltersAdapter {

	protected final URLFilter[] urlFilters;
	
	/** Instantiates ResponseModifierAdapter with HttpRequest, ChannelHandlerContext, and any number of URLFilters.
	 * @param originalRequest			LittleProxy's originalRequest.
	 * @param ctx						LittleProxy's ctx.
	 * @param urlFilters				Any number of URLFilters that will be used to match URLs and modify responses.
	 */
	public ResponseModifierAdapter(HttpRequest originalRequest, ChannelHandlerContext ctx, URLFilter... urlFilters) {
		super(originalRequest, ctx);
		this.urlFilters = urlFilters;
	}
	
	/** Returns cloned FullHttpResponse.
	 * @param response					FullHttpResponse to be cloned.
	 * @return							Cloned FullHttpResponse.
	 */
	protected FullHttpResponse cloneResponse(FullHttpResponse response) {
		return this.cloneResponse(response, response.getStatus(), null, null);
	}
	
	/** Returns FullHttpResponse with cloned headers and new content. Note that Content-Length is recalculated.
	 * @param response					FullHttpResponse whose status and headers are cloned.
	 * @param content					Byte array that serves as the response body of the cloned response.
	 * @return							FullHttpResponse with cloned headers and new body.
	 */
	protected FullHttpResponse cloneResponse(FullHttpResponse response, byte[] content) {
		return this.cloneResponse(response, response.getStatus(), null, content);
	}
	
	/** Returns FullHttpResponse with cloned headers and new content. Note that Content-Length is recalculated.
	 * @param response					FullHttpResponse whose status and headers are cloned.
	 * @param mimeType					String that serves as the mimeType for the cloned response.
	 * @param content					Byte array that serves as the response body of the cloned response.
	 * @return							FullHttpResponse with cloned headers and new body.
	 */
	protected FullHttpResponse cloneResponse(FullHttpResponse response, String mimeType, byte[] content) {
		return this.cloneResponse(response, response.getStatus(), mimeType, content);
	}
	
	/** Returns FullHttpResponse with cloned headers and new content. Note that Content-Length is recalculated.
	 * @param response					FullHttpResponse whose status and headers are cloned.
	 * @param status					HttpResponseStatus indicating the status returned (OK, 404, etc.).
	 * @return							FullHttpResponse with cloned headers and new body.
	 */
	protected FullHttpResponse cloneResponse(FullHttpResponse response, HttpResponseStatus status) {
		return this.cloneResponse(response, status, null, null);
	}
	
	/** Returns FullHttpResponse with cloned headers and new content. Note that Content-Length is recalculated.
	 * @param response					FullHttpResponse whose status and headers are cloned.
	 * @param status					HttpResponseStatus indicating the status returned (OK, 404, etc.).
	 * @param mimeType					String that serves as the mimeType for the cloned response.
	 * @param content					Byte array that serves as the response body of the cloned response.
	 * @return							FullHttpResponse with cloned headers and new body.
	 */
	protected FullHttpResponse cloneResponse(FullHttpResponse response, HttpResponseStatus status, String mimeType, byte[] content) {
		//create new FullHttpResponse with similar protocol, status and optionally content
		FullHttpResponse clonedResponse = new DefaultFullHttpResponse(
			response.getProtocolVersion(),
			status,
			(content == null) ? response.content() : Unpooled.wrappedBuffer(
				content
			)
		);
		
		//clone headers
		Iterator<Entry<String, String>> headers = response.headers().iterator();
    	while (headers.hasNext()) {
    		Entry<String, String> header = headers.next();
    		
    		//clone all headers except content-length if new content is provided, or content-type if new mimetype is provided
    		if (
    			!(header.getKey().equalsIgnoreCase("Content-Length") && content != null)
    			&& !(header.getKey().equalsIgnoreCase("Content-Type") && mimeType != null)
    		) {
    			HttpHeaders.addHeader(clonedResponse, header.getKey(), header.getValue());
    		}
    	}
		
    	//set content length specifically if new content is provided
		if (content != null) {
			HttpHeaders.setContentLength(clonedResponse, content.length);
		}
		
		//set content type specifically if new mimetype is provided
		if (mimeType != null) {
			HttpHeaders.setHeader(clonedResponse, "Content-Type", mimeType);
		}
    	
    	return clonedResponse;
	}
	
	/** Return URLFilter whose regex condition matches the URL requested in httpRequest.
	 * @param httpRequest		HttpRequest whose URL is matched.
	 * @return					First URLFilter that matches URL.
	 */
	protected URLFilter match(HttpRequest httpRequest) {
		//iterate through URL filters
		for (URLFilter urlFilter : this.urlFilters) {
			//if query string should be excluded, remove all text after question mark
			if (urlFilter.isExcludeQueryString()
				&& httpRequest.getUri().indexOf("?") > -1) {
				//check for regex match
				if (httpRequest.getUri().substring(0, httpRequest.getUri().indexOf("?")).matches(urlFilter.getCondition())) {
					return urlFilter;
				}
			//else run filter against complete url
			} else {
				//check for regex match
				if (httpRequest.getUri().matches(urlFilter.getCondition())) {
					return urlFilter;
				}
			}
		}
		
		return null;
	}

	/** Returns charset of response body.
	 * @return						Charset of response body.
	 */
	public Charset getCharset() {
		return this.getCharset(false);
	}
	
	/** Returns charset of response body.
	 * @param defaultCharset		Boolean indicating whether or not the system's default charset should be returned
	 * 								if no charset is specified.
	 * @return						Charset of response body.
	 */
	public Charset getCharset(boolean defaultCharset) {
		//check for Content-Type header
    	if (HttpHeaders.getHeader(originalRequest, Names.CONTENT_TYPE) != null) {;
			//if Content-Type contains a charset specification
			if (HttpHeaders.getHeader(originalRequest, Names.CONTENT_TYPE).matches(".*charset=.*")) {
				//extract charset value through regex
				Matcher matcher = Pattern.compile("charset=(.*?)(?:,|$)").matcher(
					HttpHeaders.getHeader(originalRequest, Names.CONTENT_TYPE)
				);
				
				if (matcher.find()) {
					return Charset.forName(matcher.group(1));
				}
			}
		}
		
    	//if no charset is specified, return default charset if requested
		return (defaultCharset ? Charset.defaultCharset() : null);
	}
	
}
