package edu.greatfree.p2p.message;

import org.greatfree.message.ServerMessage;

import edu.greatfree.cs.multinode.message.ChatMessageType;

/*
 * The message encloses the chatting partner to be added. 04/15/2017, Bing Li
 */

// Created: 05/02/2017, Bing Li
public class AddPartnerNotification extends ServerMessage
{
	private static final long serialVersionUID = -4853146777469532800L;

	private String localUserName;
	private String partnerName;
	private String invitation;
	
	public AddPartnerNotification(String localUserName, String partnerName, String invitation)
	{
		super(ChatMessageType.PEER_ADD_PARTNER_NOTIFICATION);
		this.localUserName = localUserName;
		this.partnerName = partnerName;
		this.invitation = invitation;
	}

	public String getLocalUserName()
	{
		return this.localUserName;
	}
	
	public String getPartnerName()
	{
		return this.partnerName;
	}
	
	public String getInvitation()
	{
		return this.invitation;
	}
}
