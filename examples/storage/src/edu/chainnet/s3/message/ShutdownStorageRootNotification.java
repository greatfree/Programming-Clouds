package edu.chainnet.s3.message;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.Notification;

// Created: 07/20/2020, Bing Li
public class ShutdownStorageRootNotification extends Notification
{
	private static final long serialVersionUID = -5341988809461885626L;

	public ShutdownStorageRootNotification()
	{
		super(MulticastMessageType.LOCAL_NOTIFICATION, S3AppID.SHUTDOWN_STORAGE_ROOT_NOTIFICATION);
	}

}
