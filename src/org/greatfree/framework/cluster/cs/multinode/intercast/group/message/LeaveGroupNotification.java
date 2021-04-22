package org.greatfree.framework.cluster.cs.multinode.intercast.group.message;

import org.greatfree.message.multicast.container.IntercastNotification;

// Created: 04/07/2019, Bing Li
public class LeaveGroupNotification extends IntercastNotification
{
	private static final long serialVersionUID = 651732730814332019L;
	
	private String userKey;
	private String groupKey;

	private String senderName;

	public LeaveGroupNotification(String userKey, String groupKey, String senderName)
	{
		super(userKey, groupKey, GroupChatApplicationID.LEAVE_GROUP_NOTIFICATION);
		this.userKey = userKey;
		this.groupKey = groupKey;

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
	
	public String getSenderName()
	{
		return this.senderName;
	}
}
