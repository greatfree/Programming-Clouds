package org.greatfree.app.search.dip.multicast.message;

import org.greatfree.message.multicast.MulticastRequest;

// Created: 09/28/2018, Bing Li
public class SearchMultiRequest extends MulticastRequest
{
	private static final long serialVersionUID = -3267411146848218329L;
	
	private String userKey;
	private String query;

	public SearchMultiRequest(String userKey, String query)
	{
		super(SearchMessageType.SEARCH_MULTI_REQUEST);
		this.userKey = userKey;
		this.query = query;
	}
	
	public String getUserKey()
	{
		return this.userKey;
	}

	public String getQuery()
	{
		return this.query;
	}
}
