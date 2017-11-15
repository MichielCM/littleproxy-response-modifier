package nl.michielmeulendijk.lprm;

import java.util.HashMap;
import java.util.Map.Entry;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import nl.michielmeulendijk.lprm.URLFilter;

@SuppressWarnings("unchecked")
public class RegExResponseModifierAdapter extends ResponseModifierAdapter {

	/** Instantiates ResponseModifierAdapter with HttpRequest, ChannelHandlerContext, and any number of URLFilters.
	 * URLFilters' action property must consist of a HashMap<String, String> that contains a regex and its replacement.
	 * E.g. new URLFilter("^/.*", 
			new HashMap<String, String>() {{
				put("<title>.*<\\/title>", "<title>New Title!</title>");
				put("<body>", "<body><p>Hello world!</p>");
			}}
		)
	 * @param originalRequest			LittleProxy's originalRequest.
	 * @param ctx						LittleProxy's ctx.
	 * @param urlFilters				Any number of URLFilters that will be used to match URLs and modify responses.
	 */
	public RegExResponseModifierAdapter(HttpRequest originalRequest, ChannelHandlerContext ctx, URLFilter... urlFilters) {
		super(originalRequest, ctx, urlFilters);
	}
	
	/** Returns HttpObject from the proxy to the client. Overridden to check for matching URLFilters and change
	 * regex matches accordingly.
	 */
	@Override
    public HttpObject proxyToClientResponse(HttpObject httpObject) {
    	//if response contains data
        if (httpObject instanceof FullHttpResponse) {
        	URLFilter urlFilter = this.match(originalRequest);
        	//if a matching filter is found process the response
        	if (urlFilter != null) {
        		FullHttpResponse httpResponse = (FullHttpResponse) httpObject;
        		
        		//read response body into a string
    			String content = httpResponse.content().toString(
    				this.getCharset(true)
        		);
        		
    			//cast filter action to a HashMap with regex/replacement pairs
        		HashMap<String, String> actions = (HashMap<String, String>) urlFilter.getAction();
        		//iterate through HashMap entries in the order they were added
        		for (Entry<String, String> action : actions.entrySet()) {
        			//swap all regex matches found in the response body with the specified replacement
        			content = content.replaceAll(action.getKey(), action.getValue());
        		}
            	
        		//clone the response, add the new content, and send it to the browser
                return this.cloneResponse(
                	httpResponse,
                	content.getBytes(this.getCharset(true))
                );
        	} else {
        		return httpObject;
        	}
        } else {
        	return httpObject;
        }
    }

}
