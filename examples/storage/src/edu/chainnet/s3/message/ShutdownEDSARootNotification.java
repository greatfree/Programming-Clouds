package edu.chainnet.s3.message;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.Notification;

// Created: 07/20/2020, Bing Li
public class ShutdownEDSARootNotification extends Notification
{
	private static final long serialVersionUID = 3190093632606895312L;

	public ShutdownEDSARootNotification()
	{
		super(MulticastMessageType.LOCAL_NOTIFICATION, S3AppID.SHUTDOWN_EDSA_ROOT_NOTIFICATION);
	}

}
