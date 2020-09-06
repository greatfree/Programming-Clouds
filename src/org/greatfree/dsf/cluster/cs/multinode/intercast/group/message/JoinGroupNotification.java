package org.greatfree.dsf.cluster.cs.multinode.intercast.group.message;

import org.greatfree.message.multicast.container.IntercastNotification;

// Created: 04/02/2019, Bing Li
public class JoinGroupNotification extends IntercastNotification
{
	private static final long serialVersionUID = -7831628388719654453L;
	
	private String userKey;
	private String groupKey;

	private String senderName;

	public JoinGroupNotification(String userKey, String groupKey, String senderName)
	{
		super(userKey, groupKey, GroupChatApplicationID.JOIN_GROUP_NOTIFICATION);
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
