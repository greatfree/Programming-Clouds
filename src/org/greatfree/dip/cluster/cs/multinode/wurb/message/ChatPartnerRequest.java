package org.greatfree.dip.cluster.cs.multinode.wurb.message;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.Request;

// Created: 01/27/2019, Bing Li
public class ChatPartnerRequest extends Request
{
	private static final long serialVersionUID = -2761177539514173286L;
	
	private String userKey;

	public ChatPartnerRequest(String userKey)
	{
		super(MulticastMessageType.BROADCAST_REQUEST, ChatApplicationID.CHAT_PARTNER_REQUEST);
		this.userKey = userKey;
	}

	public String getUserKey()
	{
		return this.userKey;
	}
}
