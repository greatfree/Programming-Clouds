package org.greatfree.testing.message;

import org.greatfree.message.abandoned.BroadcastResponse;

/*
 * This is a response to the unicast request, UnicastRequest. 11/29/2014, Bing Li
 */

// Created: 11/25/2016, Bing Li
public class UnicastResponse extends BroadcastResponse
{
	private static final long serialVersionUID = -4663723139172920583L;
	
	private String response;

	/*
	 * Initialize. 11/29/2014, Bing Li
	 */
	public UnicastResponse(String response, String key, String collaboratorKey)
	{
		super(MessageType.UNICAST_RESPONSE, key, collaboratorKey);
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
