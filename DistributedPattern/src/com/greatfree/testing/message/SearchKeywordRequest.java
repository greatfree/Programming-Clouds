package com.greatfree.testing.message;

import com.greatfree.multicast.ServerMessage;

/*
 * This is a search request to be sent to the coordinator. It must raise a broadcast retrieval within the cluster under the control of the coordinator. 11/29/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
public class SearchKeywordRequest extends ServerMessage
{
	private static final long serialVersionUID = 983929368948090771L;

	// The keyword to be retrieved. 11/29/2014, Bing Li
	private String keyword;

	/*
	 * Initialize the request. 11/29/2014, Bing Li
	 */
	public SearchKeywordRequest(String keyword)
	{
		super(MessageType.SEARCH_KEYWORD_REQUEST);
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
