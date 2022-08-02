package org.greatfree.concurrency.reactive;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.greatfree.concurrency.Response;

/**
 * 
 * @author libing
 * 
 * 08/01/2022
 *
 */
public final class RendezvousPoint
{
	private Map<String, ResponsePoint> points;

	private RendezvousPoint()
	{
		this.points = new ConcurrentHashMap<String, ResponsePoint>();
	}
	
	private static RendezvousPoint instance = new RendezvousPoint();
	
	public static RendezvousPoint REDUCE()
	{
		if (instance == null)
		{
			instance = new RendezvousPoint();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void dispose()
	{
		for (ResponsePoint entry : this.points.values())
		{
			entry.signalAll();
		}
		this.points.clear();
		this.points = null;
	}

	public void saveResponse(Response response)
	{
		if (!this.points.containsKey(response.getCollaboratorKey()))
		{
			this.points.put(response.getCollaboratorKey(), new ResponsePoint(response.getCollaboratorKey()));
		}
		ResponsePoint point = this.points.get(response.getCollaboratorKey());
		if (point != null)
		{
			point.setResponse(response);
		}
		this.notify(response.getCollaboratorKey());
	}
	
	private void notify(String collaboratorKey)
	{
		if (this.points.get(collaboratorKey).isResponded())
		{
			this.points.get(collaboratorKey).signal();
		}
	}
	
	public Response waitForResponse(String collaboratorKey, long waitTime)
	{
		if (!this.points.containsKey(collaboratorKey))
		{
			this.points.put(collaboratorKey, new ResponsePoint(collaboratorKey));
		}
		if (!this.points.get(collaboratorKey).isResponded())
		{
			this.points.get(collaboratorKey).holdOn(waitTime);
		}
		Response response = this.points.get(collaboratorKey).getResponse();
		this.points.remove(collaboratorKey);
		return response;
	}
}
