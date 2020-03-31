package edu.greatfree.container.p2p.message;

import org.greatfree.message.container.Request;

// Created: 01/12/2019, Bing Li
public class ChatPartnerRequest extends Request
{
	private static final long serialVersionUID = -8821788488520438666L;

	private String partnerKey;

	public ChatPartnerRequest(String partnerKey)
	{
		super(P2PChatApplicationID.PEER_CHAT_PARTNER_REQUEST);
		this.partnerKey = partnerKey;
	}

	public String getPartnerKey()
	{
		return this.partnerKey;
	}
}
