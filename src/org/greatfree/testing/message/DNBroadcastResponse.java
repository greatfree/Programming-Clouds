package org.greatfree.testing.message;

import org.greatfree.message.abandoned.BroadcastResponse;

/*
 * This is a response to the broadcast request, DNBroadcastRequest. 11/29/2014, Bing Li
 */

// Created: 11/25/2016, Bing Li
public class DNBroadcastResponse extends BroadcastResponse
{
	private static final long serialVersionUID = 3342416600396929398L;
	
	private String response;

	/*
	 * Initialize. 11/29/2014, Bing Li
	 */
	public DNBroadcastResponse(String response, String key, String collaboratorKey)
	{
		super(MessageType.BROADCAST_RESPONSE, key, collaboratorKey);
		this.response = response;
	}

	/*
	 * Expose the response. 11/29/2014, Bing Li
	 */
	public String getResponse()
	{
		return this.response;
	}
}
