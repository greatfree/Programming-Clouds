package edu.greatfree.container.cs.multinode.message;

import org.greatfree.message.container.Notification;

// Created: 12/31/2018, Bing Li
public class AddPartnerNotification extends Notification
{
	private static final long serialVersionUID = -4326740940955455208L;

	private String localUserKey;
	private String partnerKey;
	private String invitation;

	public AddPartnerNotification(String localUserKey, String partnerKey, String invitation)
	{
		super(ChatApplicationID.ADD_PARTNER_NOTIFICATION);
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
