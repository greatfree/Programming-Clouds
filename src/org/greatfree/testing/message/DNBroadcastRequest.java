package org.greatfree.testing.message;

import java.util.HashMap;

import org.greatfree.message.abandoned.BroadcastRequest;

/*
 * This is a broadcast request to cause the system to do something, such as retrieving by keywords, among the nodes of the cluster in a broadcast way. 11/29/2014, Bing Li
 */

// Created: 11/25/2016, Bing Li
public class DNBroadcastRequest extends BroadcastRequest
{
	private static final long serialVersionUID = 1384894874089312930L;

	private String request;

	/*
	 * Initialize. This is initialized for the node which has no children. 11/29/2014, Bing Li
	 */
	public DNBroadcastRequest(String request, String key, String collaboratorKey)
	{
		super(MessageType.BROADCAST_REQUEST, key, collaboratorKey);
		this.request = request;
	}

	/*
	 * Initialize. This is initialized for the node which has children. 11/29/2014, Bing Li
	 */
	public DNBroadcastRequest(String request, String key, String collaboratorKey, HashMap<String, String> children)
	{
		super(MessageType.BROADCAST_REQUEST, key, collaboratorKey);
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
