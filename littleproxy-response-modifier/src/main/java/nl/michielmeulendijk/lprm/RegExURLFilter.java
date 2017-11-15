package nl.michielmeulendijk.lprm;

import java.util.HashMap;

public class RegExURLFilter extends URLFilter {

	/** Instantiates URLFilter with specified condition and action. If excludeQueryString is specified, the condition regex
	 * is matched to a URL without its query string (i.e. any GET variables specified after a URL's first question mark).
	 * @param condition				RegEx to which URLs are matched. 
	 * @param action				HashMap that contains two strings: the key is used as the RegEx that will be replaced
	 * 								in the response body, the value as its replacement.
	 * @param excludeQueryString	Boolean indicating whether or not a URL's query string should be excluded when matching
	 * 								the condition's regex.
	 */
	public RegExURLFilter(String condition, HashMap<String, String> action, boolean excludeQueryString) {
		super(condition, action, excludeQueryString);
	}
	
	/** Instantiates URLFilter with specified condition and action.
	 * @param condition				RegEx to which URLs are matched. 
	 * @param action				HashMap that contains two strings: the key is used as the RegEx that will be replaced
	 * 								in the response body, the value as its replacement.
	 */
	public RegExURLFilter(String condition, HashMap<String, String> action) {
		super(condition, action);
	}

}
