package org.greatfree.framework.cluster.cs.multinode.intercast.group.message;

import org.greatfree.message.multicast.container.IntercastNotification;

// Created: 04/07/2019, Bing Li
public class RemoveUserNotification extends IntercastNotification
{
	private static final long serialVersionUID = -4354689471648389155L;
	
	private String groupKey;
	private String userKey;

	private String senderName;
	
	public RemoveUserNotification(String groupKey, String userKey, String senderName)
	{
		super(groupKey, userKey, GroupChatApplicationID.REMOVE_USER_NOTIFICATION);
		this.groupKey = groupKey;
		this.userKey = userKey;

		this.senderName = senderName;
	}

	public String getGroupKey()
	{
		return this.groupKey;
	}
	
	public String getUserKey()
	{
		return this.userKey;
	}
	
	public String getSenderName()
	{
		return this.senderName;
	}
}
