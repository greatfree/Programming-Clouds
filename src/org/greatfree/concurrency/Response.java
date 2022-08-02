package org.greatfree.concurrency;

/**
 * 
 * This is the response to send locally between threads. 08/01/2022, Bing Li
 * 
 * @author libing
 *
 */
public abstract class Response
{
	private final String collaboratorKey;
	
	public Response(String collaboratorKey)
	{
		this.collaboratorKey = collaboratorKey;
	}

	public String getCollaboratorKey()
	{
		return this.collaboratorKey;
	}
}
