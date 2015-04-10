package com.greatfree.testing.message;

import java.util.Set;

import com.greatfree.multicast.ServerMessage;

/*
 * This is a response to the request, SearchKeywordResponse. 11/29/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
public class SearchKeywordResponse extends ServerMessage
{
	private static final long serialVersionUID = -341670699036102757L;

	// The keyword retrieved. 11/29/2014, Bing Li
	private String keyword;
	// The links which include the keyword. 11/29/2014, Bing Li
	private Set<String> links;

	/*
	 * Initialize. 11/29/2014, Bing Li
	 */
	public SearchKeywordResponse(String keyword, Set<String> links)
	{
		super(MessageType.SEARCH_KEYWORD_RESPONSE);
		this.keyword = keyword;
		this.links = links;
	}

	/*
	 * Expose the keyword. 11/29/2014, Bing Li
	 */
	public String getKeyword()
	{
		return this.keyword;
	}

	/*
	 * Expose the links. 11/29/2014, Bing Li
	 */
	public Set<String> getLinks()
	{
		return this.links;
	}
}
