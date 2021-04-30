package edu.chainnet.s3.message;

import java.util.Set;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.Notification;
import org.greatfree.util.IPAddress;

/*
 * When a child in the pool cluster needs to join the storage cluster, it is required to be initialized. The message is responsible for that. 09/12/2020, Bing Li
 */

// Created: 09/12/2020, Bing Li
public class InitializeChildrenNotification extends Notification
{
	private static final long serialVersionUID = -4126905308579843594L;
	
	private IPAddress collaboratorAddress;

	public InitializeChildrenNotification(IPAddress ca, Set<String> childrenKeys)
	{
		super(MulticastMessageType.BROADCAST_NOTIFICATION, S3AppID.INITIALIZE_CHILDREN_NOTIFICATION, childrenKeys);
		this.collaboratorAddress = ca;
	}

	public IPAddress getCollaboratorAddress()
	{
		return this.collaboratorAddress;
	}
}
