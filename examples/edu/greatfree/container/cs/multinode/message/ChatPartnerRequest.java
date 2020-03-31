package edu.greatfree.container.cs.multinode.message;

import org.greatfree.message.container.Request;

// Created: 12/31/2018, Bing Li
public class ChatPartnerRequest extends Request
{
	private static final long serialVersionUID = -287138471403368695L;

	private String userKey;

	public ChatPartnerRequest(String userKey)
	{
		super(ChatApplicationID.CHAT_PARTNER_REQUEST);
		this.userKey = userKey;
	}

	public String getUserKey()
	{
		return this.userKey;
	}
}
