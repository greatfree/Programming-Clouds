package edu.greatfree.container.pe.message;

import org.greatfree.message.container.Request;

// Created: 06/05/2020, Bing Li
public class PollChatRequest extends Request
{
	private static final long serialVersionUID = -3458202932825941376L;
	
	private String partnerKey;

	public PollChatRequest(String userKey)
	{
		super(PEChatApplicationID.POLL_CHAT_REQUEST);
		this.partnerKey = userKey;
	}

	public String getPartnerKey()
	{
		return this.partnerKey;
	}
}
