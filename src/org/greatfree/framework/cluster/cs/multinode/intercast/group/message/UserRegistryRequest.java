package org.greatfree.framework.cluster.cs.multinode.intercast.group.message;

import org.greatfree.message.multicast.container.ClusterRequest;

// Created: 04/02/2019, Bing Li
public class UserRegistryRequest extends ClusterRequest
{
	private static final long serialVersionUID = 7199185898495150877L;

	// The user key of the account. 04/15/2017, Bing Li
	private String userKey;
	// The user name of the account. 04/15/2017, Bing Li
	private String userName;
	// The description about the user. 04/16/2017, Bing Li
	private String description;
	
	private String senderName;

	public UserRegistryRequest(String userKey, String userName, String description, String senderName)
	{
//		super(userKey, MulticastMessageType.UNICAST_REQUEST, GroupChatApplicationID.USER_REGISTRY_REQUEST);
		super(userKey, GroupChatApplicationID.USER_REGISTRY_REQUEST);
		this.userKey = userKey;
		this.userName = userName;
		this.description = description;
		
		this.senderName = senderName;
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
	
	public String getSenderName()
	{
		return this.senderName;
	}
}
