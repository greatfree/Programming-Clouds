package org.greatfree.framework.cluster.cs.multinode.wurb.message;

import org.greatfree.message.multicast.container.ClusterRequest;

// Created: 01/28/2019, Bing Li
public class ChatRegistryRequest extends ClusterRequest
{
	private static final long serialVersionUID = -7754259473082789038L;

	// The user key of the account. 04/15/2017, Bing Li
	private String userKey;
	// The user name of the account. 04/15/2017, Bing Li
	private String userName;
	// The description about the user. 04/16/2017, Bing Li
	private String description;

	public ChatRegistryRequest(String userKey, String userName, String description)
	{
//		super(userKey, MulticastMessageType.UNICAST_REQUEST, ChatApplicationID.CHAT_REGISTRY_REQUEST);
		super(userKey, ChatApplicationID.CHAT_REGISTRY_REQUEST);
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
