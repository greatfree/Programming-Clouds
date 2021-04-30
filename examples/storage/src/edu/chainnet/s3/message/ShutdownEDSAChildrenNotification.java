package edu.chainnet.s3.message;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.Notification;

// Created: 07/20/2020, Bing Li
public class ShutdownEDSAChildrenNotification extends Notification
{
	private static final long serialVersionUID = 4974781384838981581L;

	public ShutdownEDSAChildrenNotification()
	{
		super(MulticastMessageType.LOCAL_NOTIFICATION, S3AppID.SHUTDOWN_EDSA_CHILDREN_NOTIFICATION);
	}

}
