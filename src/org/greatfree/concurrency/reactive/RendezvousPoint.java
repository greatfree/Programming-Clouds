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
//	private final static Logger log = Logger.getLogger("org.greatfree.concurrency.reactive");

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
	
	/*
	 * The method is critical for the correction rendezvous point. In the past version, the response point is added inside saveResponse() and waitForResponse(), respectively. However, it results in inconsistency even though the points is ConcurrentHashMap. It is an important lesson to learn the technique of synchronized. Do NOT believe the synchronized collections when multiple operations of theirs need to be synchronized. 08/08/2022, Bing Li
	 */
	private synchronized void createPoint(String collaboratorKey)
	{
		if (!this.points.containsKey(collaboratorKey))
		{
			this.points.put(collaboratorKey, new ResponsePoint(collaboratorKey));
		}
	}

	public void saveResponse(Response response)
	{
		/*
		if (!this.points.containsKey(response.getCollaboratorKey()))
		{
			this.points.put(response.getCollaboratorKey(), new ResponsePoint(response.getCollaboratorKey()));
		}
		*/
		this.createPoint(response.getCollaboratorKey());
		ResponsePoint point = this.points.get(response.getCollaboratorKey());
		if (point != null)
		{
			point.setResponse(response);
		}
		this.notify(response.getCollaboratorKey());
//		log.info("response is saved: " + response.getCollaboratorKey());
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
		/*
		if (!this.points.containsKey(collaboratorKey))
		{
			this.points.put(collaboratorKey, new ResponsePoint(collaboratorKey));
		}
		*/
//		if (!this.points.get(collaboratorKey).isResponded())
		this.createPoint(collaboratorKey);
		while (!this.points.get(collaboratorKey).isResponded())
		{
//			log.info("polling: " + collaboratorKey);
//			log.info("polling: isResponded = " + this.points.get(collaboratorKey).isResponded());
			this.points.get(collaboratorKey).holdOn(waitTime);
		}
		
//		log.info("response is responded ...");

		Response response = this.points.get(collaboratorKey).getResponse();
		this.points.remove(collaboratorKey);
		return response;
	}
}
