package edu.chainnet.s3.message;

import org.greatfree.message.container.Notification;

/*
 * When the scale is changed, the notification updates the state of the Meta server. Thus, the client is notified to go ahead to upload or download. 09/11/2020, Bing Li
 */

// Created: 09/11/2020, Bing Li
public class ScaleChangedNotification extends Notification
{
	private static final long serialVersionUID = 6228270345435078372L;

	public ScaleChangedNotification()
	{
		super(S3AppID.SCALE_CHANGED_NOTIFICATION);
	}

}
