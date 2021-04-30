package edu.chainnet.center.message;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.Notification;

// Created: 04/29/2021, Bing Li
public class PauseIndexingNotification extends Notification
{
	private static final long serialVersionUID = -3592298444381828218L;
	
	public PauseIndexingNotification()
	{
		super(MulticastMessageType.BROADCAST_NOTIFICATION, CenterApplicationID.PAUSE_INDEXING_NOTIFICATION);
	}
}

