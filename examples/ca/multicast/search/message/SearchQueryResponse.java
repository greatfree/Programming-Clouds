package ca.multicast.search.message;

import org.greatfree.message.multicast.MulticastResponse;

// Created: 03/14/2020, Bing Li
public class SearchQueryResponse extends MulticastResponse
{
	private static final long serialVersionUID = -3094512087983725529L;
	
	private String result;

	public SearchQueryResponse(String result, String collaboratorKey)
	{
		super(SearchConfig.SEARCH_QUERY_RESPONSE, collaboratorKey);
		this.result = result;
	}

	public String getResult()
	{
		return this.result;
	}
}
