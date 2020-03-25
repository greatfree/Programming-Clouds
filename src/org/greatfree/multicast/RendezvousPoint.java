package org.greatfree.multicast;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.greatfree.exceptions.Prompts;
import org.greatfree.message.multicast.MulticastResponse;
import org.greatfree.message.multicast.RPMulticastResponse;

// Created: 12/17/2018, Bing Li
public class RendezvousPoint
{
	private Map<String, MulticastPoint> points;
	private long waitTime;

	public RendezvousPoint(long waitTime)
	{
		this.points = new ConcurrentHashMap<String, MulticastPoint>();
		this.waitTime = waitTime;
	}
	
	public void dispose()
	{
		for (MulticastPoint entry : this.points.values())
		{
			entry.signalAll();
		}
		this.points.clear();
		this.points = null;
	}
	
	public void saveResponse(MulticastResponse response)
	{
		if (!this.points.containsKey(response.getCollaboratorKey()))
		{
			this.points.put(response.getCollaboratorKey(), new MulticastPoint(response.getCollaboratorKey()));
		}
		this.points.get(response.getCollaboratorKey()).addResponse(response);
		this.notify(response.getCollaboratorKey());
	}
	
	public void saveResponse(RPMulticastResponse response)
	{
		if (!this.points.containsKey(response.getCollaboratorKey()))
		{
			this.points.put(response.getCollaboratorKey(), new MulticastPoint(response.getCollaboratorKey()));
		}
		this.points.get(response.getCollaboratorKey()).addResponses(response.getResponses());
		this.notify(response.getCollaboratorKey());
	}
	
	private void notify(String collaboratorKey)
	{
		try
		{
			if (this.points.get(collaboratorKey).isFull())
			{
				/*
				 * Sometimes it gets NullPointerException. It needs to be resolved. 12/01/2018, Bing Li	
				 */
				this.points.get(collaboratorKey).signal();
			}
			else
			{
//				System.out.println("3) RootRendezvousPoint-notify(): ");
			}
		}
		catch (NullPointerException e)
		{
			// The exception is caught during the procedure of anycasting. Any response must already signal the waiter. Thus, the multicast point is removed. 12/04/2018, Bing Li
			System.out.println(Prompts.ANYCAST_ALREADY_DONE);
		}
	}

	/*
	 * The failed node should be removed from the waiting counts. 08/26/2018, Bing Li
	 */
	public void decrementReceiverSize(String collaboratorKey)
	{
		/*
		int count = this.receiverCounts.get(collaboratorKey);
		this.receiverCounts.put(collaboratorKey, --count);
		*/
		this.points.get(collaboratorKey).decrementReceiverSize();
//		System.out.println("RootRendezvousPoint-decrementReceiverSize(): size = " + this.points.get(collaboratorKey).getReceiverSize());
	}
	
	public void setReceiverSize(String collaboratorKey, int size)
	{
//		System.out.println("RootRendezvousPoint-setReceiverSize(): size = " + size);
//		System.out.println("RootRendezvousPoint-setReceiverSize(): collaboratorKey = " + collaboratorKey);
//		this.receiverCounts.put(collaboratorKey, count);
		if (!this.points.containsKey(collaboratorKey))
		{
			this.points.put(collaboratorKey, new MulticastPoint(collaboratorKey));
		}
		this.points.get(collaboratorKey).setReceiverSize(size);
	}
	
	public int getReceiverSize(String collaboratorKey)
	{
		return this.points.get(collaboratorKey).getReceiverSize();
	}
	
	/*
	 * Waiting for responses from the distributed nodes. 08/26/2018, Bing Li
	 */
	public RPMulticastResponse waitForResponses(String collaboratorKey)
	{
		// Wait for responses from distributed nodes
		if (!this.points.containsKey(collaboratorKey))
		{
			this.points.put(collaboratorKey, new MulticastPoint(collaboratorKey));
		}
		/*
		while (!this.points.get(collaboratorKey).isFull())
		{
			this.points.get(collaboratorKey).holdOn(this.waitTime);
		}
		*/
		if (!this.points.get(collaboratorKey).isFull())
		{
			this.points.get(collaboratorKey).holdOn(this.waitTime);
		}
		List<MulticastResponse> results = this.points.get(collaboratorKey).getResponses();
		// Remove the list from the map
		this.points.remove(collaboratorKey);
		// Return the list of responses
		return new RPMulticastResponse(collaboratorKey, results);
	}
}
