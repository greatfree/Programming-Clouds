package org.greatfree.dip.cluster.cs.multinode.intercast.group.message;

import org.greatfree.message.multicast.container.IntercastRequest;

// Created: 04/07/2019, Bing Li
public class GroupMembersRequest extends IntercastRequest
{
	private static final long serialVersionUID = -5244109965611334756L;
	
	private String userKey;
	private String groupKey;

	private String senderName;

	public GroupMembersRequest(String userKey, String groupKey, String senderName)
	{
		super(userKey, groupKey, GroupChatApplicationID.GROUP_MEMBERS_REQUEST);
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
