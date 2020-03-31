package edu.greatfree.container.p2p.message;

import org.greatfree.message.container.Notification;

// Created: 01/12/2019, Bing Li
public class AddPartnerNotification extends Notification
{
	private static final long serialVersionUID = -1176669555552318575L;

	private String localUserName;
	private String partnerName;
	private String invitation;

	public AddPartnerNotification(String localUserName, String partnerName, String invitation)
	{
		super(P2PChatApplicationID.ADD_PARTNER_NOTIFICATION);
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
