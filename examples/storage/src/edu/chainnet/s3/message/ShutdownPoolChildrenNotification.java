package edu.chainnet.s3.message;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.Notification;

// Created: 09/10/2020, Bing Li
public class ShutdownPoolChildrenNotification extends Notification
{
	private static final long serialVersionUID = -4467148311375675284L;

	public ShutdownPoolChildrenNotification()
	{
		super(MulticastMessageType.LOCAL_NOTIFICATION, S3AppID.SHUTDOWN_POOL_CHILDREN_NOTIFICATION);
	}

}
