package org.greatfree.multicast.root;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.greatfree.exceptions.Prompts;
import org.greatfree.message.multicast.MulticastResponse;
import org.greatfree.multicast.MulticastPoint;

/*
 * Now it is done. 12/17/2018, Bing Li
 * 
 * I am still working on updating the rendezvous point. It is NOT finished yet. 11/07/2018, Bing Li
 */

// Created: 09/02/2018, Bing Li
public final class RootRendezvousPoint
{
//	private final static Logger log = Logger.getLogger("org.greatfree.multicast.root");	
	
	private Map<String, MulticastPoint> points;
	// All of the received responses from each of the node which is retrieved. 11/28/2014, Bing Li
//	private Map<String, List<MulticastResponse>> responses;
	// The collaborator that synchronizes to collect the results and determine whether the request is responded sufficiently. 11/28/2014, Bing Li
//	private Sync collaborator;
//	private Map<String, Sync> syncs;
	// The time to wait for responses. If it lasts too long, it might get problems for the request processing. 11/28/2014, Bing Li
	private long waitTime;
	// The count of the total nodes in the broadcast. 11/28/2014, Bing Li
//	private AtomicInteger nodeCount;
//	private Map<String, Integer> receiverCounts;
//	private ReentrantLock lock;
	
//	private ThreadPool pool;
//	private ActorPool<SignalNotification> signalActor;

//	public RootRendezvousPoint(ThreadPool pool, long waitTime)
	public RootRendezvousPoint(long waitTime)
	{
		this.points = new ConcurrentHashMap<String, MulticastPoint>();
//		this.responses = new ConcurrentHashMap<String, List<MulticastResponse>>();
//		this.collaborator = new Sync();
//		this.syncs = new ConcurrentHashMap<String, Sync>();
		this.waitTime = waitTime;
//		this.nodeCount = new AtomicInteger(0);
//		this.receiverCounts = new ConcurrentHashMap<String, Integer>();
//		this.lock = new ReentrantLock();

		/*
		this.pool = pool;
		this.signalActor = new ActorPool.ActorPoolBuilder<SignalNotification>()
				.messageQueueSize(ClientConfig.ASYNC_EVENT_QUEUE_SIZE)
				.actorSize(ClientConfig.ASYNC_EVENTER_SIZE)
				.poolingWaitTime(ClientConfig.ASYNC_EVENTING_WAIT_TIME)
				.actorWaitTime(ClientConfig.ASYNC_EVENTER_WAIT_TIME)
				.waitRound(ClientConfig.ASYNC_EVENTER_WAIT_ROUND)
				.idleCheckDelay(ClientConfig.ASYNC_EVENT_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ClientConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD)
				.schedulerPoolSize(ClientConfig.SCHEDULER_POOL_SIZE)
				.schedulerKeepAliveTime(ClientConfig.SCHEDULER_KEEP_ALIVE_TIME)
				.actor(new SignalActor())
				.build();
				*/
	}
	
	public void dispose() throws InterruptedException
	{
		// Signal all of the current threads waiting for responses
		/*
		for (Sync entry : this.syncs.values())
		{
			entry.signalAll();
		}
		// Clear the collections
		this.syncs.clear();
		this.responses.clear();
		this.receiverCounts.clear();
		*/
		for (MulticastPoint entry : this.points.values())
		{
			entry.signalAll();
		}
		this.points.clear();
		this.points = null;
//		this.lock = null;
//		this.signalActor.dispose();
	}
	
	/*
	 * Save one particular response from the remote node. 11/28/2014, Bing Li
	 */
	public void saveResponse(MulticastResponse response) throws InterruptedException
	{
		// Check whether the response corresponds to the requestor. 11/29/2014, Bing Li
		/*
		if (this.collaborator.getKey().equals(response.getCollaboratorKey()))
		{
			// Put it into the collection. 11/28/2014, Bing Li
			this.responseMap.put(response.getKey(), response);
			// Check whether the count of the collected response exceeds the total node count. 11/28/2014, Bing Li
			if (this.responseMap.size() >= this.nodeCount.get())
			{
				// If it does, it denotes that the request has been answered by all of the nodes. Notify the waiting collaborator to end the request broadcast. 11/28/2014, Bing Li
				this.collaborator.signal();
			}
		}
		*/
//		System.out.println("RootRendezvousPoint-saveResponse(): response is received!");
		/*
		if (!this.responses.containsKey(response.getCollaboratorKey()))
		{
			this.responses.put(response.getCollaboratorKey(), new ArrayList<MulticastResponse>());
		}
		this.responses.get(response.getCollaboratorKey()).add(response);
		*/
		if (!this.points.containsKey(response.getCollaboratorKey()))
		{
//			System.out.println("RootRendezvousPoint-saveResponse(): create a new point");
			this.points.put(response.getCollaboratorKey(), new MulticastPoint(response.getCollaboratorKey()));
		}
		
		// The below line might cause NullPointerException. 09/09/2020, Bing Li
//		this.points.get(response.getCollaboratorKey()).addResponse(response);
		MulticastPoint point = this.points.get(response.getCollaboratorKey());
		if (point != null)
		{
			point.addResponse(response);
		}
		/*
		if (this.responses.get(response.getCollaboratorKey()).size() >= this.receiverCounts.get(response.getCollaboratorKey()))
		{
			if (!this.syncs.containsKey(response.getCollaboratorKey()))
			{
//				Thread.sleep(MulticastConfig.RP_SIGNAL_PERIOD);
				this.syncs.put(response.getCollaboratorKey(), new Sync(false));
			}
			while (this.responses.containsKey(response.getCollaboratorKey()))
			{
				this.syncs.get(response.getCollaboratorKey()).signal();
				System.out.println("RootRendezvousPoint-saveResponse(): signal is done!");
				Thread.sleep(MulticastConfig.RP_SIGNAL_PERIOD);
			}
		}
		*/
		/*
		if (!this.signalActor.isReady())
		{
			this.pool.execute(this.signalActor);
		}
		this.signalActor.perform(new SignalNotification(this, response.getCollaboratorKey()));
		*/
		this.notify(response.getCollaboratorKey());
	}
	
//	private void notify(String collaboratorKey) throws InterruptedException
	private void notify(String collaboratorKey)
	{
//		if (this.responses.get(collaboratorKey).size() >= this.receiverCounts.get(collaboratorKey))
//		System.out.println("0.5) RootRendezvousPoint-notify(): collaboratorKey = " + collaboratorKey);
		
		try
		{
//			System.out.println("0) RootRendezvousPoint-notify(): response size = " + this.points.get(collaboratorKey).getResponses().size());
			if (this.points.get(collaboratorKey).isFull())
			{
				/*
					if (!this.syncs.containsKey(collaboratorKey))
					{
//						Thread.sleep(MulticastConfig.RP_SIGNAL_PERIOD);
						this.syncs.put(collaboratorKey, new Sync(false));
					}
					*/
				
//				System.out.println("1) RootRendezvousPoint-notify(): ");
				/*
//					while (this.responses.containsKey(collaboratorKey))
					while (this.points.containsKey(collaboratorKey))
					{
//						this.syncs.get(collaboratorKey).signal();
//						System.out.println("2) RootRendezvousPoint-notify(): ");
						this.points.get(collaboratorKey).signal();
//						System.out.println("RootRendezvousPoint-saveResponse(): signal is done!");
						Thread.sleep(MulticastConfig.RP_SIGNAL_TIMEOUT);
					}
					*/
				
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
//		return this.receiverCounts.get(collaboratorKey);
		return this.points.get(collaboratorKey).getReceiverSize();
	}

	/*
	 * Waiting for responses from the distributed nodes. 08/26/2018, Bing Li
	 */
	public List<MulticastResponse> waitForResponses(String collaboratorKey)
	{
		// It is possible that the responses are received before the waiting is set up. So it is required to ensure no responses are received or the count of the received responses is lower than the required one. 11/03/2018, Bing Li
		/*
		if (!this.responses.containsKey(collaboratorKey))
		{
			// Create an instance of Sync, a wait/notify mechanism, for one multicasting request
			this.syncs.put(collaboratorKey, new Sync(false));
			// Wait for responses from distributed nodes
//			this.lock.lock();
			this.syncs.get(collaboratorKey).holdOn(this.waitTime);
//			this.lock.unlock();
			// Remove the instance of Sync for the multicasting request
			this.syncs.remove(collaboratorKey);
		}
		else if (this.responses.get(collaboratorKey).size() < this.receiverCounts.get(collaboratorKey))
		{
			// Create an instance of Sync, a wait/notify mechanism, for one multicasting request
			this.syncs.put(collaboratorKey, new Sync(false));
			// Wait for responses from distributed nodes
//			this.lock.lock();
			this.syncs.get(collaboratorKey).holdOn(this.waitTime);
//			this.lock.unlock();
			// Remove the instance of Sync for the multicasting request
			this.syncs.remove(collaboratorKey);
		}
		*/
		/*
		if (this.syncs.containsKey(collaboratorKey))
		{
			// Remove the instance of Sync for the multicasting request
			this.syncs.remove(collaboratorKey);
			// Get the list that keeps the responses from distributed nodes
			List<MulticastResponse> results = this.responses.get(collaboratorKey);
			// Remove the list from the map
			this.responses.remove(collaboratorKey);
			// Return the list of responses
			return results;
		}
		*/
		// Create an instance of Sync, a wait/notify mechanism, for one multicasting request
		/*
		if (!this.syncs.containsKey(collaboratorKey))
		{
			this.syncs.put(collaboratorKey, new Sync(false));
		}
		*/
		// Wait for responses from distributed nodes
//		this.syncs.put(collaboratorKey, new Sync(false));
		if (!this.points.containsKey(collaboratorKey))
		{
			this.points.put(collaboratorKey, new MulticastPoint(collaboratorKey));
		}
//		this.lock.lock();
//		System.out.println("RootRendezvousPoint-waitForResponses(): Starting to wait for collaboratorKey = " + collaboratorKey);
//		this.syncs.get(collaboratorKey).holdOn(this.waitTime);
		/*
		while (!this.points.get(collaboratorKey).isFull())
		{
			this.points.get(collaboratorKey).holdOn(this.waitTime);
		}
		*/
		if (!this.points.get(collaboratorKey).isFull())
		{
//			log.info("RootRendezvousPoint is waiting for signal ...");
			this.points.get(collaboratorKey).holdOn(this.waitTime);
//			log.info("RootRendezvousPoint's waiting is finished ...");
		}
//		System.out.println("RootRendezvousPoint-waitForResponses(): Waiting done for collaboratorKey = " + collaboratorKey);
//		this.lock.unlock();
		// Get the list that keeps the responses from distributed nodes
//		List<MulticastResponse> results = this.responses.get(collaboratorKey);
		List<MulticastResponse> results = this.points.get(collaboratorKey).getResponses();
		// Remove the list from the map
//		this.responses.remove(collaboratorKey);
		this.points.remove(collaboratorKey);
		// Return the list of responses
		return results;
	}

	/*
	 * Waiting for responses from the distributed nodes. But in the case, a single response from one partition is enough since data within partition is replicated and identical such that it is not necessary to wait additional ones. 08/26/2018, Bing Li
	 */
	public MulticastResponse waitForResponseUponPartition(String collaboratorKey)
	{
		if (!this.points.containsKey(collaboratorKey))
		{
			this.points.put(collaboratorKey, new MulticastPoint(collaboratorKey));
		}
//		if (!this.points.get(collaboratorKey).isFull())
		// Once if a replica is available, it is not necessary to wait. 09/09/2020, Bing Li
		if (!this.points.get(collaboratorKey).isAvailable())
		{
			this.points.get(collaboratorKey).holdOn(this.waitTime);
		}
		List<MulticastResponse> results = this.points.get(collaboratorKey).getResponses();
		this.points.remove(collaboratorKey);
		return results.get(0);
	}
}
