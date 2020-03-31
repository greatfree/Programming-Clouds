package edu.greatfree.cs.multinode.message;

import org.greatfree.message.container.Request;

/*
 * The request asks the chatting registry server to obtain the potential chatting partners. 04/16/2017, Bing Li
 */

// Created: 04/16/2017, Bing Li
public class ChatPartnerRequest extends Request
{
	private static final long serialVersionUID = -1587573982719535239L;
	
	private String userKey;

	public ChatPartnerRequest(String userKey)
	{
		super(ChatMessageType.CS_CHAT_PARTNER_REQUEST);
		this.userKey = userKey;
	}

	public String getUserKey()
	{
		return this.userKey;
	}
}
