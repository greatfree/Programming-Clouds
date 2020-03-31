package edu.greatfree.p2p.message;

import org.greatfree.message.ServerMessage;

import edu.greatfree.cs.multinode.message.ChatMessageType;

/*
 * The request asks the chatting registry server to obtain the potential chatting partners. 04/16/2017, Bing Li
 */

// Created: 04/30/2017, Bing Li
public class ChatPartnerRequest extends ServerMessage
{
	private static final long serialVersionUID = 3239511889796307819L;
	
	private String partnerKey;

	public ChatPartnerRequest(String partnerKey)
	{
		super(ChatMessageType.PEER_CHAT_PARTNER_REQUEST);
		this.partnerKey = partnerKey;
	}

	public String getPartnerKey()
	{
		return this.partnerKey;
	}
}
