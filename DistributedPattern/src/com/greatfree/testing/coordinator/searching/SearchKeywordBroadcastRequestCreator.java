package com.greatfree.testing.coordinator.searching;

import java.util.HashMap;

import com.greatfree.multicast.RootBroadcastRequestCreatable;
import com.greatfree.testing.message.SearchKeywordBroadcastRequest;
import com.greatfree.util.Tools;

/*
 * This creator aims to create requests of SearchKeywordBroadcastRequest, in the root broadcast requester. 11/29/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
public class SearchKeywordBroadcastRequestCreator implements RootBroadcastRequestCreatable<SearchKeywordBroadcastRequest, String>
{
	/*
	 * Create the broadcast request for the node which has children. 11/29/2014, Bing Li
	 */
	@Override
	public SearchKeywordBroadcastRequest createInstanceWithChildren(String keyword, String collaboratorKey, HashMap<String, String> childrenMap)
	{
		return new SearchKeywordBroadcastRequest(keyword, Tools.generateUniqueKey(), collaboratorKey, childrenMap);
	}

	/*
	 * Create the broadcast request for the node which has no children. 11/29/2014, Bing Li
	 */
	@Override
	public SearchKeywordBroadcastRequest createInstanceWithoutChildren(String keyword, String collaboratorKey)
	{
		return new SearchKeywordBroadcastRequest(keyword, Tools.generateUniqueKey(), collaboratorKey);
	}
}
