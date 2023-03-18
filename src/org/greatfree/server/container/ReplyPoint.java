package org.greatfree.server.container;

import org.greatfree.concurrency.Sync;
import org.greatfree.message.container.Response;

/**
 * 
 * @author libing
 * 
 * 03/08/2023
 *
 */
public final class ReplyPoint
{
	private final String requestKey;
	private Response response;
	private Sync sync;

	public ReplyPoint(String requestKey)
	{
		this.requestKey = requestKey;
		this.response = null;
		this.sync = new Sync(false);
	}
	
	public String getRequestKey()
	{
		return this.requestKey;
	}
	
	public void signalAll()
	{
		this.sync.signalAll();
	}
	
	public void holdOn(long waitTime)
	{
		this.sync.holdOn(waitTime);
	}
	
	public void setResponse(Response response)
	{
		this.response = response;
		this.sync.signal();
	}
	
	public Response getResponse()
	{
		return this.response;
	}
}
