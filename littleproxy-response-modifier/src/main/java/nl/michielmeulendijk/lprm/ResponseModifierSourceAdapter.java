package nl.michielmeulendijk.lprm;

import org.littleshoot.proxy.HttpFiltersSourceAdapter;

public abstract class ResponseModifierSourceAdapter extends HttpFiltersSourceAdapter {
	
	/* (non-Javadoc)
	 * @see org.littleshoot.proxy.HttpFiltersSourceAdapter#getMaximumRequestBufferSizeInBytes()
	 */
	@Override
    public int getMaximumRequestBufferSizeInBytes() {
        return 10 * 1024 * 1024;
    }
	
	/* (non-Javadoc)
	 * @see org.littleshoot.proxy.HttpFiltersSourceAdapter#getMaximumResponseBufferSizeInBytes()
	 */
	@Override
    public int getMaximumResponseBufferSizeInBytes() {
        return 10 * 1024 * 1024;
    }
	
}
