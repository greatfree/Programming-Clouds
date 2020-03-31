package edu.greatfree.cs.multinode.message;

import org.greatfree.message.ServerMessage;

/*
 * The response contains the account description. 04/16/2017, Bing Li
 */

// Created: 04/16/2017, Bing Li
public class ChatPartnerResponse extends ServerMessage
{
	private static final long serialVersionUID = 2055070734600953034L;
	
	private String userKey;
	private String userName;
	private String description;

	public ChatPartnerResponse(String userKey, String userName, String description)
	{
		super(ChatMessageType.CS_CHAT_PARTNER_RESPONSE);
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
