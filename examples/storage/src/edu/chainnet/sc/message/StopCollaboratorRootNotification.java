package edu.chainnet.sc.message;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.Notification;

// Created: 10/17/2020, Bing Li
public class StopCollaboratorRootNotification extends Notification
{
	private static final long serialVersionUID = 4637273592286987099L;

	public StopCollaboratorRootNotification()
	{
		super(MulticastMessageType.LOCAL_NOTIFICATION, SCAppID.STOP_COLLABORATOR_ROOT_NOTIFICATION);
	}

}
