package com.greatfree.testing.message;

import com.greatfree.multicast.AnycastResponse;

/*
 * This is a response to the anycast request, IsPublisherExistedAnycastRequest. 11/29/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
public class IsPublisherExistedAnycastResponse extends AnycastResponse
{
	private static final long serialVersionUID = -1149369827314333148L;

	// The flag to indicate whether the URL is existed. 11/29/2014, Bing Li
	private boolean isExisted;

	/*
	 * Initialize. 11/29/2014, Bing Li
	 */
	public IsPublisherExistedAnycastResponse(boolean isExisted, String collaboratorKey)
	{
		super(MessageType.IS_PUBLISHER_EXISTED_ANYCAST_RESPONSE, collaboratorKey);
		this.isExisted = isExisted;
	}

	/*
	 * Expose the flag. 11/29/2014, Bing Li
	 */
	public boolean isExisted()
	{
		return this.isExisted;
	}
}
