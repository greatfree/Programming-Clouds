package edu.chainnet.s3.message;

import org.greatfree.message.container.Notification;

/*
 * When the scale of the storage system needs to be updated, the notification is sent to the Meta server. Then, the client is notified indirectly via the uploading or the downloading responses. 09/11/2020, Bing Li
 */

// Created: 09/11/2020, Bing Li
public class ScaleChangingNotification extends Notification
{
	private static final long serialVersionUID = -2935217113815961760L;

	public ScaleChangingNotification()
	{
		super(S3AppID.SCALE_CHANGING_NOTIFICATION);
	}

}
