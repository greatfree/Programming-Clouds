package edu.chainnet.s3.message;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.Notification;

// Created: 07/20/2020, Bing Li
public class ShutdownStorageChildrenNotification extends Notification
{
	private static final long serialVersionUID = 3906811021617876343L;

	public ShutdownStorageChildrenNotification()
	{
		super(MulticastMessageType.LOCAL_NOTIFICATION, S3AppID.SHUTDOWN_STORAGE_CHILDREN_NOTIFICATION);
	}

}
