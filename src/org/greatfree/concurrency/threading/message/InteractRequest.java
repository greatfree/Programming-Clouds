package org.greatfree.concurrency.threading.message;

import java.util.Set;

import org.greatfree.util.Tools;

// Created: 10/03/2019, Bing Li
public abstract class InteractRequest extends ATMNotification
{
	private static final long serialVersionUID = -7125354711233302309L;
	
	private String srcSlaveKey;
	private String dstSlaveKey;
	private String taskKey;
	private String collaboratorKey;

	public InteractRequest(String srcSlaveKey, String dstSlaveKey, String threadKey, String taskKey)
	{
		super(threadKey, ATMMessageType.INTERACT_REQUEST);
		this.srcSlaveKey = srcSlaveKey;
		this.dstSlaveKey = dstSlaveKey;
		this.taskKey = taskKey;
		this.collaboratorKey = Tools.generateUniqueKey();
	}

	public InteractRequest(String srcSlaveKey, String dstSlaveKey, Set<String> threadKeys, String taskKey)
	{
		super(threadKeys, ATMMessageType.INTERACT_REQUEST);
		this.srcSlaveKey = srcSlaveKey;
		this.dstSlaveKey = dstSlaveKey;
		this.taskKey = taskKey;
		this.collaboratorKey = Tools.generateUniqueKey();
	}
	
	public String getSourceSlaveKey()
	{
		return this.srcSlaveKey;
	}

	public String getDestinationSlaveKey()
	{
		return this.dstSlaveKey;
	}
	
	public String getTaskKey()
	{
		return this.taskKey;
	}
	
	public String getCollaboratorKey()
	{
		return this.collaboratorKey;
	}
}
