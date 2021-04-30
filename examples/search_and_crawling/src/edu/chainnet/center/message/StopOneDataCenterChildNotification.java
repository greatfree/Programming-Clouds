package edu.chainnet.center.message;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.Notification;

// Created: 04/25/2021, Bing Li
public class StopOneDataCenterChildNotification extends Notification
{
	private static final long serialVersionUID = 5223523637993744274L;

	public StopOneDataCenterChildNotification()
	{
		super(MulticastMessageType.UNICAST_NOTIFICATION, CenterApplicationID.STOP_ONE_CENTER_CHILD_NOTIFICATION);
	}

}
