package org.greatfree.testing.concurrency.pingpong;

import org.greatfree.concurrency.Request;

/**
 * 
 * @author libing
 * 
 * 08/08/2022
 *
 */
class PongRequest extends Request
{
	private String query;
	
	public PongRequest(String query)
	{
		this.query = query;
	}

	public PongRequest(String query, String collaboratorKey)
	{
		super(collaboratorKey);
		this.query = query;
	}

	public String getQuery()
	{
		return this.query;
	}
}
