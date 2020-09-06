package org.greatfree.dsf.cluster.cs.multinode.unifirst.message;

import org.greatfree.dsf.cluster.cs.multinode.wurb.message.ChatApplicationID;
import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.Notification;

// Created: 02/10/2019, Bing Li
//public class AddPartnerNotification extends IntercastNotification
public class AddPartnerNotification extends Notification
{
	private static final long serialVersionUID = 7404758990642892379L;

	private String localUserKey;
	private String partnerKey;
	private String invitation;

	public AddPartnerNotification(String localUserKey, String partnerKey, String invitation)
	{
//		super(localUserKey, partnerKey, ChatApplicationID.ADD_PARTNER_NOTIFICATION);
		super(partnerKey, MulticastMessageType.UNICAST_NOTIFICATION, ChatApplicationID.ADD_PARTNER_NOTIFICATION);
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
