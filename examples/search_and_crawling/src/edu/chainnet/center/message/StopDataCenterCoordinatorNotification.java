package edu.chainnet.center.message;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.Notification;

// Created: 04/25/2021, Bing Li
public class StopDataCenterCoordinatorNotification extends Notification
{
	private static final long serialVersionUID = 5166604550557954658L;

	public StopDataCenterCoordinatorNotification()
	{
		super(MulticastMessageType.LOCAL_NOTIFICATION, CenterApplicationID.STOP_CENTER_COORDINATOR_NOTIFICATION);
	}
}
