package ca.multicast.search.message;

import java.util.List;

import org.greatfree.message.ServerMessage;

// Created: 03/14/2020, Bing Li
public class ClientSearchQueryResponse extends ServerMessage
{
	private static final long serialVersionUID = -2590447910633009200L;
	
	private List<SearchQueryResponse> results;

	public ClientSearchQueryResponse(List<SearchQueryResponse> results)
	{
		super(SearchConfig.CLIENT_SEARCH_QUERY_RESPONSE);
		this.results = results;
	}

	public List<SearchQueryResponse> getResults()
	{
		return this.results;
	}
}
