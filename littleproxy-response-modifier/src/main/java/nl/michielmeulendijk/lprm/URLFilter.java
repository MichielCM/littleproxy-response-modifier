package nl.michielmeulendijk.lprm;

public abstract class URLFilter {
	
	private final String condition;
	private final Object action;
	private final boolean excludeQueryString;
	
	/** Instantiates URLFilter with specified condition and action. If excludeQueryString is specified, the condition regex
	 * is matched to a URL without its query string (i.e. any GET variables specified after a URL's first question mark).
	 * @param condition				RegEx to which URLs are matched. 
	 * @param action				Object that contains actions to be executed when URL matches. Implementation depends on
	 * 								specified ResponseModifierAdapter extension.
	 * @param excludeQueryString	Boolean indicating whether or not a URL's query string should be excluded when matching
	 * 								the condition's regex.
	 */
	public URLFilter(String condition, Object action, boolean excludeQueryString) {
		this.condition = condition;
		this.action = action;
		this.excludeQueryString = excludeQueryString;
	}
	
	/** Instantiates URLFilter with specified condition and action.
	 * @param condition				RegEx to which URLs are matched. 
	 * @param action				Object that contains actions to be executed when URL matches. Implementation depends on
	 * 								specified ResponseModifierAdapter extension.
	 */
	public URLFilter(String condition, Object action) {
		this.condition = condition;
		this.action = action;
		this.excludeQueryString = false;
	}
	
	public String getCondition() {
		return this.condition;
	}
	
	public Object getAction() {
		return this.action;
	}

	public boolean isExcludeQueryString() {
		return this.excludeQueryString;
	}
}
