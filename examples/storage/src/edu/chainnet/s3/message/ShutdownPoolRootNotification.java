package edu.chainnet.s3.message;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.Notification;

// Created: 09/10/2020, Bing Li
public class ShutdownPoolRootNotification extends Notification
{
	private static final long serialVersionUID = 9157236654255285459L;

	public ShutdownPoolRootNotification()
	{
		super(MulticastMessageType.LOCAL_NOTIFICATION, S3AppID.SHUTDOWN_POOL_ROOT_NOTIFICATION);
	}

}
