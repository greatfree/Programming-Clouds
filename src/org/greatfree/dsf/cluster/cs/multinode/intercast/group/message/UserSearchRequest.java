package org.greatfree.dsf.cluster.cs.multinode.intercast.group.message;

import org.greatfree.message.multicast.container.IntercastRequest;

// Created: 04/07/2019, Bing Li
public class UserSearchRequest extends IntercastRequest
{
	private static final long serialVersionUID = -5706308380434857522L;
	
	private String groupKey;
	private String userKey;

	private String senderName;
	
	public UserSearchRequest(String groupKey, String userKey, String senderName)
	{
		super(groupKey, userKey, GroupChatApplicationID.USER_SEARCH_REQUEST);
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
