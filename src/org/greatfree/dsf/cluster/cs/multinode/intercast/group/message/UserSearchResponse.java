package org.greatfree.dsf.cluster.cs.multinode.intercast.group.message;

import org.greatfree.message.multicast.MulticastResponse;

// Created: 04/07/2019, Bing Li
public class UserSearchResponse extends MulticastResponse
{
	private static final long serialVersionUID = 6451625099896911371L;
	
	private String userKey;
	private String userName;
	private String description;

	public UserSearchResponse(String userKey, String userName, String description, String collaboratorKey)
	{
		super(GroupChatApplicationID.USER_SEARCH_RESPONSE, collaboratorKey);
		this.userKey = userKey;
		this.userName = userName;
		this.description = description;
	}

	public String getUserKey()
	{
		return this.userKey;
	}
	
	public String getUserName()
	{
		return this.userName;
	}
	
	public String getDescription()
	{
		return this.description;
	}
}
