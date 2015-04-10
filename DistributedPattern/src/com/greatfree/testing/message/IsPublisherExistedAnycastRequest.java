package com.greatfree.testing.message;

import java.util.HashMap;

import com.greatfree.multicast.AnycastRequest;

/*
 * This is an anycast request to raise the system to retrieve URL among the cluster of memory nodes in an anycast way. 11/29/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
public class IsPublisherExistedAnycastRequest extends AnycastRequest
{
	private static final long serialVersionUID = -2127692519947528923L;

	// The URL to be retrieved. 11/29/2014, Bing Li
	private String url;

	/*
	 * Initialize. This is initialized for the node which has no children. 11/29/2014, Bing Li
	 */
	public IsPublisherExistedAnycastRequest(String url, String key, String collaboratorKey)
	{
		super(MessageType.IS_PUBLISHER_EXISTED_ANYCAST_REQUEST, key, collaboratorKey);
		this.url = url;
	}

	/*
	 * Initialize. This is initialized for the node which has children. 11/29/2014, Bing Li
	 */
	public IsPublisherExistedAnycastRequest(String url, String key, String collaboratorKey, HashMap<String, String> childrenServerMap)
	{
		super(MessageType.IS_PUBLISHER_EXISTED_ANYCAST_REQUEST, key, collaboratorKey, childrenServerMap);
		this.url = url;
	}

	/*
	 * Expose the URL. 11/29/2014, Bing Li
	 */
	public String getURL()
	{
		return this.url;
	}
}
