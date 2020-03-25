package org.greatfree.testing.message;

import org.greatfree.message.abandoned.BroadcastResponse;

/*
 * This is a response to the anycast request, AnycastRequest. 11/29/2014, Bing Li
 */

// Created: 11/27/2016, Bing Li
public class DNAnycastResponse extends BroadcastResponse
{
	private static final long serialVersionUID = -7389303915428845034L;
	
	private String response;

	public DNAnycastResponse(String response, String key, String collaboratorKey)
	{
		super(MessageType.ANYCAST_RESPONSE, key, collaboratorKey);
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
