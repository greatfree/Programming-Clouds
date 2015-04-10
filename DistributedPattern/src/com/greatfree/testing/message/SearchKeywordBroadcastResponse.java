package com.greatfree.testing.message;

import java.util.Set;

import com.greatfree.multicast.BroadcastResponse;

/*
 * This is a response to the broadcast request, SearchKeywordBroadcastRequest. 11/29/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
public class SearchKeywordBroadcastResponse extends BroadcastResponse
{
	private static final long serialVersionUID = 727494758786064640L;

	// The retrieved results, hyperlinks. 11/29/2014, Bing Li
	private Set<String> links;

	/*
	 * Initialize. 11/29/2014, Bing Li
	 */
	public SearchKeywordBroadcastResponse(Set<String> links, String key, String collaboratorKey)
	{
		super(MessageType.SEARCH_KEYWORD_BROADCAST_RESPONSE, key, collaboratorKey);
		this.links = links;
	}

	/*
	 * Expose the links. 11/29/2014, Bing Li
	 */
	public Set<String> getLinks()
	{
		return this.links;
	}
}
