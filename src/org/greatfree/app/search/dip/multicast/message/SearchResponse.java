package org.greatfree.app.search.dip.multicast.message;

import java.util.List;

import org.greatfree.message.ServerMessage;

// Created: 09/28/2018, Bing Li
public class SearchResponse extends ServerMessage
{
	private static final long serialVersionUID = -8865987629018093279L;
	
	private List<SearchMultiResponse> responses;

	public SearchResponse(List<SearchMultiResponse> responses)
	{
		super(SearchMessageType.SEARCH_RESPONSE);
		this.responses = responses;
	}

	public List<SearchMultiResponse> getResponses()
	{
		return this.responses;
	}
}
