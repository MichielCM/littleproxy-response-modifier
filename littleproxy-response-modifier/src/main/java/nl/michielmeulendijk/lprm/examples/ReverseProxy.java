package nl.michielmeulendijk.lprm.examples;

import java.util.HashMap;

import org.littleshoot.proxy.HttpFilters;
import org.littleshoot.proxy.HttpProxyServer;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import nl.michielmeulendijk.lprm.ResponseModifierSourceAdapter;
import nl.michielmeulendijk.lprm.RegExResponseModifierAdapter;
import nl.michielmeulendijk.lprm.RegExURLFilter;
import nl.michielmeulendijk.lprm.utils.Console;

@SuppressWarnings("serial")
public class ReverseProxy {
	final static String remoteHost = "http://localhost:8887";
	final static int proxyPort = 4000;
	
	public static void main(String[] args) {
		HttpProxyServer server =
    	    DefaultHttpProxyServer.bootstrap()
    	        .withPort(ReverseProxy.proxyPort)
    	        .withFiltersSource(
    	        	new ResponseModifierSourceAdapter() {
    	        		@Override
    	        	    public HttpFilters filterRequest(HttpRequest originalRequest, ChannelHandlerContext ctx) {
    	        			return new RegExResponseModifierAdapter(
    	        				originalRequest, ctx,
    	        				new RegExURLFilter(
    	        					//listen on the root URL
    	        					"^/",
    	        					new HashMap<String, String>() {{
    									//replace the title
    									put("<title>.*<\\/title>", "<title>New Title!</title>");
    								}},
    	        					true
    	        				)
    	        	        ) {
    	        	        	@Override
    	        	            public HttpResponse clientToProxyRequest(HttpObject httpObject) {
    	        	            	//set up reverse proxy
    	        	            	Console.log(originalRequest.getUri());
    	        	            	
    	        	            	if (httpObject instanceof HttpRequest) {
    	        	            		HttpRequest httpRequest = (HttpRequest) httpObject;
    	        	        			
    	        	            		//reroute only if request URI is relative to server
    	        	            		if (httpRequest.getUri().matches("^[/].*")) {
    	        	            			//redirect request to real server
    	        	            			httpRequest.setUri(
    	        	        					ReverseProxy.remoteHost.concat(httpRequest.getUri())
    	        	            			);
    	        	            		}
    	        	            	}
    	        	            	
    	        	            	return null;
    	        	            }
    	        	        };
    	        	    }
    	        	}
    	        ).start();
        
        Console.log(
        	"Proxy server listening on ".concat(
    			server.getListenAddress().getHostName()
        	).concat(":").concat(
    			String.valueOf(server.getListenAddress().getPort())
        	)
        );
        
    }
}
