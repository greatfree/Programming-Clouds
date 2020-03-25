package org.greatfree.dip.cluster.cs.multinode.intercast.group.message;

import org.greatfree.message.multicast.container.Request;

// Created: 04/05/2019, Bing Li
public class PollGroupChatRequest extends Request
{
	private static final long serialVersionUID = -5424894510668078175L;

	private String userKey;
	private String groupKey;
	private int messageCount;
	private long timespan;

	private String senderName;

	public PollGroupChatRequest(String userKey, String groupKey, int messageCount, long timespan, String senderName)
	{
//		super(userKey, MulticastMessageType.UNICAST_REQUEST, GroupChatApplicationID.POLL_GROUP_CHAT_REQUEST);
		super(userKey, GroupChatApplicationID.POLL_GROUP_CHAT_REQUEST);
		this.userKey = userKey;
		this.groupKey = groupKey;
		this.messageCount = messageCount;
		this.timespan = timespan;

		this.senderName = senderName;
	}

	public String getUserKey()
	{
		return this.userKey;
	}
	
	public String getGroupKey()
	{
		return this.groupKey;
	}
	
	public int getMessageCount()
	{
		return this.messageCount;
	}
	
	public long getTime()
	{
		return this.timespan;
	}
	
	public String getSenderName()
	{
		return this.senderName;
	}
}
