package org.greatfree.app.search.dip.multicast.message;

import org.greatfree.message.ServerMessage;

// Created: 09/28/2018, Bing Li
public class SearchRequest extends ServerMessage
{
	private static final long serialVersionUID = 1202141048954735483L;
	
	private String userKey;
	private String query;

	public SearchRequest(String userKey, String query)
	{
		super(SearchMessageType.SEARCH_REQUEST);
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
