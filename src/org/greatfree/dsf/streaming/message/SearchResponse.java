package org.greatfree.dsf.streaming.message;

import java.util.List;

import org.greatfree.message.ServerMessage;

// Created: 03/21/2020, Bing Li
public class SearchResponse extends ServerMessage
{
	private static final long serialVersionUID = -7840862798034079194L;
	
	private List<String> results;

	public SearchResponse(List<String> results)
	{
		super(StreamMessageType.SEARCH_RESPONSE);
		this.results = results;
	}

	public List<String> getResults()
	{
		return this.results;
	}
}
