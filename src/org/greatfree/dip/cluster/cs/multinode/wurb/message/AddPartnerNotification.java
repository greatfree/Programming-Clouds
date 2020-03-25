package org.greatfree.dip.cluster.cs.multinode.wurb.message;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.Notification;

// Created: 01/28/2019, Bing Li
public class AddPartnerNotification extends Notification
{
	private static final long serialVersionUID = -8152361807736586628L;
	
	private String localUserKey;
	private String partnerKey;
	private String invitation;

	public AddPartnerNotification(String localUserKey, String partnerKey, String invitation)
	{
		super(localUserKey, MulticastMessageType.UNICAST_NOTIFICATION, ChatApplicationID.ADD_PARTNER_NOTIFICATION);
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
