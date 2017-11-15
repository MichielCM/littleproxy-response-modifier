package nl.michielmeulendijk.lprm;

import java.io.InputStream;

public class XSLTURLFilter extends URLFilter {

	/** Instantiates URLFilter with specified condition and action. If excludeQueryString is specified, the condition regex
	 * is matched to a URL without its query string (i.e. any GET variables specified after a URL's first question mark).
	 * @param condition				RegEx to which URLs are matched. 
	 * @param action				InputStream that contains an XSL document with which matched URL's response body
	 * 								will be transformed.
	 * @param excludeQueryString	Boolean indicating whether or not a URL's query string should be excluded when matching
	 * 								the condition's regex.
	 */
	public XSLTURLFilter(String condition, InputStream action, boolean excludeQueryString) {
		super(condition, action, excludeQueryString);
	}
	
	/** Instantiates URLFilter with specified condition and action.
	 * @param condition				RegEx to which URLs are matched. 
	 * @param action				InputStream that contains an XSL document with which matched URL's response body
	 * 								will be transformed.
	 */
	public XSLTURLFilter(String condition, InputStream action) {
		super(condition, action);
	}

}
