package org.greatfree.dip.cluster.cs.multinode.intercast.group.clusterserver.child;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// Created: 04/07/2019, Bing Li
class PublicChatSessions
{
	// In the case of group chatting, the session key is identical to the group key. 04/07/2019, Bing Li
	private Map<String, GroupChatSession> chats;
	
	private PublicChatSessions()
	{
	}
	
	/*
	 * A singleton definition. 04/17/2017, Bing Li
	 */
	private static PublicChatSessions instance = new PublicChatSessions();
	
	public static PublicChatSessions HUNGARY()
	{
		if (instance == null)
		{
			instance = new PublicChatSessions();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	/*
	 * Dispose the free client pool. 04/17/2017, Bing Li
	 */
	public void dispose()
	{
		this.chats.clear();
		this.chats = null;
	}

	/*
	 * Initialize explicitly. 04/23/2017, Bing Li
	 */
	public void init()
	{
		this.chats = new ConcurrentHashMap<String, GroupChatSession>();
	}

	public void addMessage(String groupKey, String senderKey, String message)
	{
		if (!this.chats.containsKey(groupKey))
		{
			this.chats.put(groupKey, new GroupChatSession(groupKey));
		}
		this.chats.get(groupKey).addMessage(senderKey, message);
	}

	public List<GroupChatMessage> getMessages(String groupKey, int n, long timespan)
	{
		if (this.chats.containsKey(groupKey))
		{
			return this.chats.get(groupKey).getMessages(n, timespan);
		}
		return null;
	}
}
