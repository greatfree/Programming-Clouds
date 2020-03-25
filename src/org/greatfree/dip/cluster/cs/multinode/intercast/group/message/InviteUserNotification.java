package org.greatfree.dip.cluster.cs.multinode.intercast.group.message;

import org.greatfree.message.multicast.container.IntercastNotification;

// Created: 04/07/2019, Bing Li
public class InviteUserNotification extends IntercastNotification
{
	private static final long serialVersionUID = -4997224364385133928L;
	
	private String groupKey;
	private String userKey;
	
	private String senderName;

	public InviteUserNotification(String groupKey, String userKey, String senderName)
	{
		super(groupKey, userKey, GroupChatApplicationID.INVITE_USER_NOTIFICATION);
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
