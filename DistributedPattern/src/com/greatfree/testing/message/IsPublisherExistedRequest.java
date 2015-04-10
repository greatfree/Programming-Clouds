package com.greatfree.testing.message;

import com.greatfree.multicast.ServerMessage;

/*
 * The request is sent by the searcher to the coordinator to retrieve whether a publisher, represented in the form of URL, is existed. It must raise an anycast retrieval on the cluster controlled by the coordinator. 11/29/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
public class IsPublisherExistedRequest extends ServerMessage
{
	private static final long serialVersionUID = 7791005060878729509L;

	// The URL to be retrieved. 11/29/2014, Bing Li
	private String url;

	/*
	 * Initialize. 11/29/2014, Bing Li
	 */
	public IsPublisherExistedRequest(String url)
	{
		super(MessageType.IS_PUBLISHER_EXISTED_REQUEST);
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
