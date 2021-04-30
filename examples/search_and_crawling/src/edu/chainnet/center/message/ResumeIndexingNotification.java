package edu.chainnet.center.message;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.Notification;

// Created: 04/29/2021, Bing Li
public class ResumeIndexingNotification extends Notification
{
	private static final long serialVersionUID = 5008660602372963681L;

	public ResumeIndexingNotification()
	{
		super(MulticastMessageType.BROADCAST_NOTIFICATION, CenterApplicationID.RESUME_INDEXING_NOTIFICATION);
	}
}

