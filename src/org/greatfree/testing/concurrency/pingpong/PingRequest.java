package org.greatfree.testing.concurrency.pingpong;

import org.greatfree.concurrency.Request;

/**
 * 
 * @author libing
 * 
 * 08/08/2022
 *
 */
class PingRequest extends Request
{
	private String query;
	
	public PingRequest(String query)
	{
		this.query = query;
	}

	public PingRequest(String query, String collaboratorKey)
	{
		super(collaboratorKey);
		this.query = query;
	}

	public String getQuery()
	{
		return this.query;
	}
}

