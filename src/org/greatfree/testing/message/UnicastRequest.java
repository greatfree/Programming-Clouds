package org.greatfree.testing.message;

import java.util.HashMap;

import org.greatfree.message.abandoned.BroadcastRequest;

/*
 * This is a unicast request to cause the system to do something, such as retrieving by keywords, from a particular node of the cluster in a unicast way. 11/29/2014, Bing Li
 */

// Created: 11/25/2016, Bing Li
public class UnicastRequest extends BroadcastRequest
{
	private static final long serialVersionUID = 2118106280596495949L;
	
	private String request;

	/*
	 * Initialize. This is initialized for the node which has no children. 11/29/2014, Bing Li
	 */
	public UnicastRequest(String request, String key, String collaboratorKey)
	{
		super(MessageType.UNICAST_REQUEST, key, collaboratorKey);
		this.request = request;
	}

	/*
	 * Initialize. This is initialized for the node which has children. 11/29/2014, Bing Li
	 */
	public UnicastRequest(String request, String key, String collaboratorKey, HashMap<String, String> children)
	{
		super(MessageType.UNICAST_REQUEST, key, collaboratorKey);
		this.request = request;
	}

	/*
	 * Expose the request. 11/29/2014, Bing Li
	 */
	public String getRequest()
	{
		return this.request;
	}
}
