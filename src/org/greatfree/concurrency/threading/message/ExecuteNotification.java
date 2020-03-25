package org.greatfree.concurrency.threading.message;

// Created: 09/12/2019, Bing Li
public class ExecuteNotification extends InstructNotification
{
	private static final long serialVersionUID = -152948645428150485L;

	/*
	public ExecuteNotification(String threadKey, String taskKey)
	{
		super(threadKey, taskKey, ThreadingMessageType.EXECUTE_NOTIFICATION);
	}
	*/

	/*
	 * When no taskKey is assigned, it indicates that all of the threads in the Actor work on the same tasks. 09/13/2019, Bing Li
	 */
	public ExecuteNotification(String threadKey)
	{
		super(threadKey, ThreadingMessageType.EXECUTE_NOTIFICATION);
	}
}

