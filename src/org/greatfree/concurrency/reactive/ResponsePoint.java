package org.greatfree.concurrency.reactive;

import org.greatfree.concurrency.Response;
import org.greatfree.concurrency.Sync;

/**
 * 
 * @author libing
 * 
 * 08/01/2022
 *
 */
final class ResponsePoint
{
	private final String collaboratorKey;
	private Sync sync;
	private Response response;

	public ResponsePoint(String ck)
	{
		this.collaboratorKey = ck;
		this.sync = new Sync(false);
		this.response = null;
	}
	
	public String getCollabratorKey()
	{
		return this.collaboratorKey;
	}

	public boolean isResponded()
	{
		return this.response != null;
	}

	public void setResponse(Response response)
	{
		this.response = response;
	}
	
	public void signal()
	{
		this.sync.signal();
	}
	
	public void signalAll()
	{
		this.sync.signalAll();
	}
	
	public void holdOn(long waitTime)
	{
		this.sync.holdOn(waitTime);
	}
	
	public Response getResponse()
	{
		return this.response;
	}
}
