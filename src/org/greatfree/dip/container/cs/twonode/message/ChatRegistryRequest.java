package org.greatfree.dip.container.cs.twonode.message;

import org.greatfree.message.container.Request;

// Created: 12/18/2018, Bing Li
public class ChatRegistryRequest extends Request
{
	private static final long serialVersionUID = -4617209546278872440L;

	// The user key of the account. 04/15/2017, Bing Li
	private String userKey;
	// The user name of the account. 04/15/2017, Bing Li
	private String userName;
	// The description about the user. 04/16/2017, Bing Li
	private String description;

	public ChatRegistryRequest(String userKey, String userName, String description)
	{
//		super(CSMessageType.REQUEST, ApplicationID.CHAT_REGISTRY_REQUEST);
		super(ApplicationID.CHAT_REGISTRY_REQUEST);
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


