package edu.chainnet.center.message;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.Notification;

// Created: 04/25/2021, Bing Li
public class StopDataCenterChildrenNotification extends Notification
{
	private static final long serialVersionUID = 7639282179235406794L;

	public StopDataCenterChildrenNotification()
	{
		super(MulticastMessageType.LOCAL_NOTIFICATION, CenterApplicationID.STOP_CENTER_CHILDREN_NOTIFICATION);
	}
}

