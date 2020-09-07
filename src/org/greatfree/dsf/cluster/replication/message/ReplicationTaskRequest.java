package org.greatfree.dsf.cluster.replication.message;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.Request;

// Created: 09/07/2020, Bing Li
public class ReplicationTaskRequest extends Request
{
	private static final long serialVersionUID = -5059343893634360277L;
	
	private String msgKey;
	private String query;

//	public ReplicationTaskRequest(int partitionIndex)
	public ReplicationTaskRequest(String msgKey, String query, int partitionIndex)
	{
		super(MulticastMessageType.BROADCAST_REQUEST, ReplicationApplicationID.REPLICATION_TASK_REQUEST, partitionIndex);
		this.msgKey = msgKey;
		this.query = query;
	}
	
	public String getMsgKey()
	{
		return this.msgKey;
	}

	public String getQuery()
	{
		return this.query;
	}
}
