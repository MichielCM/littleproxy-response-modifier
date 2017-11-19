package nl.michielmeulendijk.lprm;

import java.io.InputStream;
import java.io.StringReader;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import nl.michielmeulendijk.lprm.xml.Transformer;

public class XSLTResponseModifierAdapter extends ResponseModifierAdapter {

	/** Instantiates ResponseModifierAdapter with HttpRequest, ChannelHandlerContext, and any number of URLFilters.
	 * URLFilters' action property must consist of an InputStream containing an XSLT document.
	 * @param originalRequest			LittleProxy's originalRequest.
	 * @param ctx						LittleProxy's ctx.
	 * @param urlFilters				Any number of URLFilters that will be used to match URLs and modify responses.
	 */
	public XSLTResponseModifierAdapter(HttpRequest originalRequest, ChannelHandlerContext ctx, URLFilter... urlFilters) {
		super(originalRequest, ctx, urlFilters);
	}

	/** Returns HttpObject from the proxy to the client. Overridden to check for matching URLFilters and transform regex
	 * matches with XSL accordingly.
	 */
	@Override
    public HttpObject serverToProxyResponse(HttpObject httpObject) {
		//if response contains data
        if (httpObject instanceof FullHttpResponse) {
        	URLFilter urlFilter = this.match(originalRequest);
        	//if a matching filter is found process the response
        	if (urlFilter != null) {
        		try {
        			FullHttpResponse httpResponse = (FullHttpResponse) httpObject;
            		
        			//read response body into a string
        			String content = httpResponse.content().toString(
        				this.getCharset(true)
            		);
        			
        			//clone response and return it to the browser
                    return this.cloneResponse(
                		httpResponse,
                		//transform the content with XSLT
                		Transformer.transform(
                			new StreamSource(
                				//configure string to escape ampersands
                				new StringReader(content.replaceAll("&(?!amp;)", "&amp;"))
        					),
                			new StreamSource(
                				//URLFilter's action should contain an InputStream containing an XSL document
            					(InputStream) urlFilter.getAction()
                			),
                			new URIResolver() {
                				//specify URIResolver to load documents
    							public Source resolve(String href, String base) throws TransformerException {
    								//load documents from full URLs
    								if (href.indexOf("://") > -1) {
    									return new StreamSource(href);
									//load documents locally
    								} else {
    									return new StreamSource(
    										this.getClass().getClassLoader().getResourceAsStream(href)
    									);
    								}
    							}            				
                			}
                		//escape ampersands and send response as byte array
                		).replaceAll("&amp;", "&").getBytes(this.getCharset(true))
                	);
        		} catch(TransformerException e) {
        			e.printStackTrace();
        			return httpObject;
        		}
        	} else {
        		return httpObject;
        	}
        } else {
        	return httpObject;
        }
    }
	
}
