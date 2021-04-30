package edu.chainnet.s3.message;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.Notification;

/*
 * The code intends to stop one child of the storage cluster and verify whether the fault-tolerant strategy works or not. 09/09/2020, Bing Li
 */

// Created: 09/09/2020, Bing Li
public class StopOneStorageChildNotification extends Notification
{
	private static final long serialVersionUID = 3747043637433665403L;

	public StopOneStorageChildNotification()
	{
		super(MulticastMessageType.UNICAST_NOTIFICATION, S3AppID.STOP_ONE_STORAGE_CHILD_NOTIFICATION);
	}

}
