package com.greatfree.testing.message;

import java.util.HashMap;

import com.greatfree.multicast.BroadcastRequest;

/*
 * This is a broadcast request to raise the system to retrieve keyword among the cluster of memory nodes in a broadcast way. 11/29/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
public class SearchKeywordBroadcastRequest extends BroadcastRequest
{
	private static final long serialVersionUID = -5910485952080467801L;

	// The keyword to be retrieved. 11/29/2014, Bing Li
	private String keyword;

	/*
	 * Initialize. This is initialized for the node which has no children. 11/29/2014, Bing Li
	 */
	public SearchKeywordBroadcastRequest(String keyword, String key, String collaboratorKey)
	{
		super(MessageType.SEARCH_KEYWORD_BROADCAST_REQUEST, key, collaboratorKey);
		this.keyword = keyword;
	}

	/*
	 * Initialize. This is initialized for the node which has children. 11/29/2014, Bing Li
	 */
	public SearchKeywordBroadcastRequest(String keyword, String key, String collaboratorKey, HashMap<String, String> children)
	{
		super(MessageType.SEARCH_KEYWORD_BROADCAST_REQUEST, key, collaboratorKey, children);
		this.keyword = keyword;
	}

	/*
	 * Expose the keyword. 11/29/2014, Bing Li
	 */
	public String getKeyword()
	{
		return this.keyword;
	}
}
