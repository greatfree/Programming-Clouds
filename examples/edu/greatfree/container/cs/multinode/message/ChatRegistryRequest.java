package edu.greatfree.container.cs.multinode.message;

import org.greatfree.message.container.Request;

// Created: 12/31/2018, Bing Li
public class ChatRegistryRequest extends Request
{
	private static final long serialVersionUID = 5723169493426479478L;

	// The user key of the account. 04/15/2017, Bing Li
	private String userKey;
	// The user name of the account. 04/15/2017, Bing Li
	private String userName;
	// The description about the user. 04/16/2017, Bing Li
	private String description;

	public ChatRegistryRequest(String userKey, String userName, String description)
	{
		super(ChatApplicationID.CHAT_REGISTRY_REQUEST);
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
