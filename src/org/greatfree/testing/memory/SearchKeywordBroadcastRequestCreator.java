package org.greatfree.testing.memory;

import java.util.HashMap;

import org.greatfree.multicast.ChildMulticastMessageCreatable;
import org.greatfree.testing.message.SearchKeywordBroadcastRequest;
import org.greatfree.util.Tools;

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
