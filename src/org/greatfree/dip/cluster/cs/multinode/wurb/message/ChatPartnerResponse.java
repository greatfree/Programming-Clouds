package org.greatfree.dip.cluster.cs.multinode.wurb.message;

import org.greatfree.message.multicast.MulticastResponse;

// Created: 01/27/2019, Bing Li
public class ChatPartnerResponse extends MulticastResponse
{
	private static final long serialVersionUID = 6009051240028624484L;
	
	private String userKey;
	private String userName;
	private String description;

	public ChatPartnerResponse(String userKey, String userName, String description, String collaboratorKey)
	{
		super(ChatApplicationID.CHAT_PARTNER_RESPONSE, collaboratorKey);
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
