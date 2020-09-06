package org.greatfree.dsf.cluster.original.cs.twonode.message;

import org.greatfree.message.multicast.container.Request;

// Created: 10/24/2108, Bing Li
public class ChatRegistryRequest extends Request
{
	private static final long serialVersionUID = -216824394677822287L;
	
	// The user key of the account. 04/15/2017, Bing Li
	private String userKey;
	// The user name of the account. 04/15/2017, Bing Li
	private String userName;
	// The description about the user. 04/16/2017, Bing Li
	private String description;

	public ChatRegistryRequest(String userKey, String userName, String description)
	{
		/*
		 * When registering, it can be done in the way of broadcasting as well as unicasting. If the user is registered in the way of broadcasting, other users can find him through unicasting requests. If the user is registered though unicasting, other users have to find him through broadcasting requests. For a large-scale chatting system, it is recommended to register in the way of unicasting to save storage space since it must have a large number of users. 01/27/2019, Bing Li
		 */
//		super(userKey, MulticastMessageType.UNICAST_REQUEST, ChatApplicationID.CHAT_REGISTRY_REQUEST);
		super(userKey, ChatApplicationID.CHAT_REGISTRY_REQUEST);
//		super(userKey, MulticastMessageType.BROADCAST_REQUEST, ApplicationID.CHAT_REGISTRY_REQUEST);
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
