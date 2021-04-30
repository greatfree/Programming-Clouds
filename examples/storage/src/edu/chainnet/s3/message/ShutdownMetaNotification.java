package edu.chainnet.s3.message;

import org.greatfree.message.container.Notification;

// Created: 07/20/2020, Bing Li
public class ShutdownMetaNotification extends Notification
{
	private static final long serialVersionUID = 6756573410067288072L;

	public ShutdownMetaNotification()
	{
		super(S3AppID.SHUTDOWN_META_NOTIFICATION);
	}

}
