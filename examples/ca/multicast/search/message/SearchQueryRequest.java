package ca.multicast.search.message;

import org.greatfree.message.multicast.MulticastRequest;

// Created: 03/14/2020, Bing Li
public class SearchQueryRequest extends MulticastRequest
{
	private static final long serialVersionUID = 4957027936977879844L;
	
	private String query;

	public SearchQueryRequest(String query)
	{
		super(SearchConfig.SEARCH_QUERY_REQUEST);
		this.query = query;
	}

	public String getQuery()
	{
		return this.query;
	}
}
