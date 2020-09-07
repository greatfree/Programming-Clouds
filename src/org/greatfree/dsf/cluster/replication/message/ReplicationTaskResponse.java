package org.greatfree.dsf.cluster.replication.message;

import org.greatfree.message.multicast.MulticastResponse;

// Created: 09/07/2020, Bing Li
public class ReplicationTaskResponse extends MulticastResponse
{
	private static final long serialVersionUID = -5480230633420638068L;
	
	private String childrenKey;
	private String message;

	public ReplicationTaskResponse(String childrenKey, String message, String collaboratorKey)
	{
		super(ReplicationApplicationID.REPLICATION_TASK_RESPONSE, collaboratorKey);
		this.childrenKey = childrenKey;
		this.message = message;
	}

	public String getChildrenKey()
	{
		return this.childrenKey;
	}
	
	public String getMessage()
	{
		return this.message;
	}
}
