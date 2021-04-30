package edu.chainnet.sc.message;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.Notification;

/*
 * The message to shutdown all of the children of a cluster. 10/17/2020, Bing Li
 */

// Created: 10/17/2020, Bing Li
public class StopCollaboratorClusterNotification extends Notification
{
	private static final long serialVersionUID = 1195376149456815209L;

	public StopCollaboratorClusterNotification()
	{
		super(MulticastMessageType.BROADCAST_NOTIFICATION, SCAppID.STOP_COLLABORATOR_CLUSTER_NOTIFICATION);
	}
}
