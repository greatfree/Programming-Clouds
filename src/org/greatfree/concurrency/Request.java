package org.greatfree.concurrency;

import org.greatfree.util.Tools;

/**
 * 
 * This is the request to send locally between threads. 08/01/2022, Bing Li
 * 
 * @author libing
 * 
 * 08/01/2022
 *
 */
public abstract class Request
{
	private final String collaboratorKey;
	
	public Request()
	{
		this.collaboratorKey = Tools.generateUniqueKey();
	}

	public Request(String collaboratorKey)
	{
		this.collaboratorKey = collaboratorKey;
	}

	public String getCollaboratorKey()
	{
		return this.collaboratorKey;
	}
}
