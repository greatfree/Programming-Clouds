package org.greatfree.dsf.cluster.cs.multinode.intercast.group.clusterserver.child;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.greatfree.dsf.cs.twonode.server.AccountRegistry;
import org.greatfree.util.Time;

// Created: 04/07/2019, Bing Li
class GroupChatSession
{
	private final String groupKey;
	
	private List<GroupChatMessage> messages;
	private ReentrantLock lock;

	public GroupChatSession(String groupKey)
	{
		this.groupKey = groupKey;
		this.messages = new ArrayList<GroupChatMessage>();
		this.lock = new ReentrantLock();
	}

	public String getGroupKey()
	{
		return this.groupKey;
	}
	
	public void addMessage(String senderKey, String message)
	{
		this.lock.lock();
		this.messages.add(new GroupChatMessage(senderKey, AccountRegistry.CS().getAccount(senderKey).getUserName(), message, Calendar.getInstance().getTime()));
		this.lock.unlock();
	}

	public List<GroupChatMessage> getMessages(int n, long timespan)
	{
		List<GroupChatMessage> chats = new ArrayList<GroupChatMessage>();
		this.lock.lock();
		try
		{
			if (this.messages.size() <= n)
			{
				for (GroupChatMessage entry : this.messages)
				{
					if (entry.getTime().after(Time.getEarlyTime(timespan)))
					{
						chats.add(entry);
					}
				}
			}
			else
			{
				List<GroupChatMessage> candidates = this.messages.subList(this.messages.size() - n - 1, this.messages.size() - 1);
				for (GroupChatMessage entry : candidates)
				{
					if (entry.getTime().after(Time.getEarlyTime(timespan)))
					{
						chats.add(entry);
					}
				}
			}
			return chats;
		}
		finally
		{
			this.lock.unlock();
		}
	}
}
