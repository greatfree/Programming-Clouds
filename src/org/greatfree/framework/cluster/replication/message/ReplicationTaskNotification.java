package org.greatfree.framework.cluster.replication.message;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.Notification;
import org.greatfree.util.Tools;

// Created: 09/07/2020, Bing Li
public class ReplicationTaskNotification extends Notification
{
	private static final long serialVersionUID = 3760281691610807996L;
	
	private String key;
	private String message;

	public ReplicationTaskNotification(String message, int partitionIndex)
	{
		super(MulticastMessageType.BROADCAST_NOTIFICATION, ReplicationApplicationID.REPLICATION_TASK_NOTIFICATION, partitionIndex);
		this.key = Tools.getHash(message);
		this.message = message;
	}
	
	public String getMsgKey()
	{
		return this.key;
	}

	public String getMessage()
	{
		return this.message;
	}
}
