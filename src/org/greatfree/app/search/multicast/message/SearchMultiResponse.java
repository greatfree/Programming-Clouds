package org.greatfree.app.search.multicast.message;

import java.util.List;

import org.greatfree.message.multicast.MulticastResponse;

// Created: 09/28/2018, Bing Li
public class SearchMultiResponse extends MulticastResponse
{
	private static final long serialVersionUID = 5625954759817199362L;

	private List<Page> pages;
	
	public SearchMultiResponse(List<Page> pages, String collaboratorKey)
	{
//		super(SearchMessageType.SEARCH_MULTI_REQUEST, Tools.generateUniqueKey(), collaboratorKey);
//		super(SearchMessageType.SEARCH_MULTI_REQUEST, collaboratorKey);
		super(SearchMessageType.SEARCH_MULTI_RESPONSE, collaboratorKey);
		this.pages = pages;
	}

	public List<Page> getPages()
	{
		return this.pages;
	}
}
