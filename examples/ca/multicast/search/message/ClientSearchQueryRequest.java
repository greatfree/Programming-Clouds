package ca.multicast.search.message;

import org.greatfree.message.ServerMessage;

// Created: 03/14/2020, Bing Li
public class ClientSearchQueryRequest extends ServerMessage
{
	private static final long serialVersionUID = 9213600118569411509L;
	
	private String query;

	public ClientSearchQueryRequest(String query)
	{
		super(SearchConfig.CLIENT_SEARCH_QUERY_REQUEST);
		this.query = query;
	}

	public String getQuery()
	{
		return this.query;
	}
}
