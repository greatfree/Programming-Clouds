package org.greatfree.framework.streaming.message;

import org.greatfree.message.ServerMessage;

// Created: 03/21/2020, Bing Li
public class SearchRequest extends ServerMessage
{
	private static final long serialVersionUID = 1339299855213157716L;

	private String query;

	public SearchRequest(String query)
	{
		super(StreamMessageType.SEARCH_REQUEST);
		this.query = query;
	}

	public String getQuery()
	{
		return this.query;
	}
}
