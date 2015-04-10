package com.greatfree.testing.memory;

import java.util.HashMap;

import com.greatfree.multicast.ChildMulticastMessageCreatable;
import com.greatfree.testing.message.SearchKeywordBroadcastRequest;
import com.greatfree.util.Tools;

/*
 * The creator initiates the instance of SearchKeywordBroadcastRequest that is needed by the multicastor. 11/29/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
public class SearchKeywordBroadcastRequestCreator implements ChildMulticastMessageCreatable<SearchKeywordBroadcastRequest>
{
	@Override
	public SearchKeywordBroadcastRequest createInstanceWithChildren(SearchKeywordBroadcastRequest msg, HashMap<String, String> children)
	{
		return new SearchKeywordBroadcastRequest(msg.getKeyword(), Tools.generateUniqueKey(), msg.getCollaboratorKey(), children);
	}
}
