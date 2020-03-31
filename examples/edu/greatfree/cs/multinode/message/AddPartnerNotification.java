package edu.greatfree.cs.multinode.message;

import org.greatfree.message.container.Notification;

/*
 * The message encloses the chatting partner to be added. 04/15/2017, Bing Li
 */

// Created: 04/23/2017, Bing Li
public class AddPartnerNotification extends Notification
{
	private static final long serialVersionUID = 2290167983632240615L;
	
	private String localUserKey;
	private String partnerKey;
	private String invitation;

	public AddPartnerNotification(String localUserKey, String partnerKey, String invitation)
	{
		super(ChatMessageType.CS_ADD_PARTNER_NOTIFICATION);
		this.localUserKey = localUserKey;
		this.partnerKey = partnerKey;
		this.invitation = invitation;
	}

	public String getLocalUserKey()
	{
		return this.localUserKey;
	}
	
	public String getPartnerKey()
	{
		return this.partnerKey;
	}
	
	public String getInvitation()
	{
		return this.invitation;
	}
}
