package org.greatfree.server.container;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.greatfree.message.ServerMessage;
import org.greatfree.message.container.Response;

/**
 * 
 * @author libing
 * 
 * 03/08/2023
 *
 */
public final class RendezvousPoint
{
	private Map<String, ReplyPoint> points;
	private long waitTime;
	
	public RendezvousPoint(long waitTime)
	{
		this.points = new ConcurrentHashMap<String, ReplyPoint>();
		this.waitTime = waitTime;
	}

	public void dispose()
	{
		for (ReplyPoint entry : this.points.values())
		{
			entry.signalAll();
		}
		this.points.clear();
		this.points = null;
	}

	public void saveResponse(Response res)
	{
		if (this.points.containsKey(res.getKey()))
		{
			this.points.get(res.getKey()).setResponse(res);
		}
	}

//	public Response waitResponse(String requestKey)
	public ServerMessage waitResponse(String requestKey)
	{
		if (!this.points.containsKey(requestKey))
		{
			this.points.put(requestKey, new ReplyPoint(requestKey));
		}
		this.points.get(requestKey).holdOn(this.waitTime);
		Response res = this.points.get(requestKey).getResponse();
		this.points.remove(requestKey);
		return res.getMessage();
	}
}
