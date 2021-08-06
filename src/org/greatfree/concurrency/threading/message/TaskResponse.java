package org.greatfree.concurrency.threading.message;

import org.greatfree.message.container.Notification;

// Created: 09/28/2019, Bing Li
public abstract class TaskResponse extends Notification
{
	private static final long serialVersionUID = 8781312752106605522L;
	
	private String collaboratorKey;

	public TaskResponse(String collaboratorKey)
	{
		super(ATMMessageType.TASK_RESPONSE);
		this.collaboratorKey = collaboratorKey;
	}

	public String getCollaboratorKey()
	{
		return this.collaboratorKey;
	}
}
