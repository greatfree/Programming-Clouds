package org.greatfree.framework.cs.multinode.message;

import org.greatfree.message.ServerMessage;
import org.greatfree.message.SystemMessageType;

/*
 * The message encloses the data to register one account in the chatting system. 04/15/2017, Bing Li
 */

// Created: 04/15/2017, Bing Li
public class ChatRegistryRequest extends ServerMessage
{
	private static final long serialVersionUID = -4329938193364238991L;

	// The user key of the account. 04/15/2017, Bing Li
	private String userKey;
	// The user name of the account. 04/15/2017, Bing Li
	private String userName;
	// The description about the user. 04/16/2017, Bing Li
	private String description;

	public ChatRegistryRequest(String userKey, String userName, String description)
	{
		super(SystemMessageType.CS_CHAT_REGISTRY_REQUEST);
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
