package org.greatfree.concurrency.reactive;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

import org.greatfree.client.FreeClientPool;
import org.greatfree.client.IPResource;
import org.greatfree.concurrency.RunnerTask;
import org.greatfree.concurrency.MessageBindable;
import org.greatfree.concurrency.Sync;
import org.greatfree.message.ServerMessage;
import org.greatfree.util.Tools;
import org.greatfree.util.UtilConfig;

/*
 * When processing broadcast requests, no matter whether the local memory node contains the matched data, it is required to forward the request to its children. That is the difference between the anycast and the broadcast. However, it is suggested that the retrieval and data forwarding can be done concurrently and they do not affect with one another. The thread is designed for the goal since they are synchronized once after both of them finish their critical tasks. Therefore, they do not affect each other. 11/29/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
//public abstract class BoundBroadcastRequestQueue<Request extends BroadcastRequest, Response extends BroadcastResponse, RequestBinder extends MessageBindable<Request>> implements Runnable, Comparable<BoundBroadcastRequestQueue<Request, Response, RequestBinder>>
// public abstract class BoundRequestQueue<Request extends ServerMessage, Response extends ServerMessage, RequestBinder extends MessageBindable<Request>> implements Runnable, Comparable<BoundRequestQueue<Request, Response, RequestBinder>>
public abstract class BoundRequestQueue<Request extends ServerMessage, Response extends ServerMessage, RequestBinder extends MessageBindable<Request>> extends RunnerTask
{
	// The unique key of the thread. It is convenient for managing it by a table-like mechanism. 11/29/2014, Bing Li
	private final String key;
	// The dispatcher key that controls the thread. It represents one of threads that share the broadcast request. 11/29/2014, Bing Li
	private final String dispatcherKey;
	// The queue to take the received broadcast requests. 11/29/2014, Bing Li
	private Queue<Request> queue;
	// The IP/port of the broadcast original initiator. The response must be sent back to it. 11/29/2014, Bing Li
	private IPResource ipPort;
	// The maximum size of the queue. 11/29/2014, Bing Li
	private final int taskSize;
	// It is necessary to keep the thread waiting when no requests are available. The collaborator is used to notify the thread to keep working when requests are received. When the thread is idle enough, it can be collected. The collaborator is also used to control the life cycle of the thread. 11/29/2014, Bing Li
	private Sync collaborator;
	// The flag that represents whether the thread is busy or idle. 11/29/2014, Bing Li
	private boolean isIdle;
	// The TCP client pool. With it, it is able to get the instance of the client to connect the initiator of the broadcast. 11/29/2014, Bing Li
	private FreeClientPool pool;
	// The binder that receives notifications from all of threads that share the broadcast. After all of the threads have completed their concurrent tasks, the binder can do something that must be done in a synchronous way. 11/29/2014, Bing Li
	private RequestBinder reqBinder;
	// The lock is critical to keep synchronous to manage the idle/busy state of the thread. 02/07/2016, Bing Li
	private ReentrantLock idleLock;

	/*
	 * Initialize an instance. 11/29/2014, Bing Li
	 */
	public BoundRequestQueue(IPResource ipPort, FreeClientPool pool, String dispatcherKey, RequestBinder reqBinder)
	{
		this.key = Tools.generateUniqueKey();
		this.queue = new LinkedBlockingQueue<Request>();
		this.ipPort = ipPort;
		this.taskSize = UtilConfig.NO_QUEUE_SIZE;
		this.collaborator = new Sync();
		this.isIdle = false;
		this.pool = pool;
		this.dispatcherKey = dispatcherKey;
		this.reqBinder = reqBinder;
		this.idleLock = new ReentrantLock();
	}
	
	/*
	 * Initialize an instance. 11/29/2014, Bing Li
	 */
	public BoundRequestQueue(IPResource ipPort, FreeClientPool pool, int taskSize, String dispatcherKey, RequestBinder reqBinder)
	{
		this.key = Tools.generateUniqueKey();
		this.queue = new LinkedBlockingQueue<Request>();
		this.ipPort = ipPort;
		this.taskSize = taskSize;
		this.collaborator = new Sync();
		this.isIdle = false;
		this.pool = pool;
		this.dispatcherKey = dispatcherKey;
		this.reqBinder = reqBinder;
		this.idleLock = new ReentrantLock();
	}

	/*
	 * Initialize an instance. This is the latest version. 11/29/2014, Bing Li
	 */
	public BoundRequestQueue(int taskSize, String dispatcherKey, RequestBinder reqBinder)
	{
		this.key = Tools.generateUniqueKey();
		this.queue = new LinkedBlockingQueue<Request>();
//		this.ipPort = ipPort;
		this.taskSize = taskSize;
		this.collaborator = new Sync();
		this.isIdle = false;
//		this.pool = pool;
		this.dispatcherKey = dispatcherKey;
		this.reqBinder = reqBinder;
		this.idleLock = new ReentrantLock();
	}

	/*
	 * Dispose the instance of the class. 11/29/2014, Bing Li
	 */
	@Override
	public synchronized void dispose() throws InterruptedException
	{
		// Set the flag to be the state of being shutdown. 11/29/2014, Bing Li
		/*
		this.collaborator.setShutdown();
		// Notify the thread being waiting to go forward. Since the shutdown flag is set, the thread must die for the notification. 11/29/2014, Bing Li
		this.collaborator.signalAll();
		try
		{
			// Wait for the thread to die. 11/29/2014, Bing Li
			this.join();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		*/

		// The above shutdown lines are combined and executed atomically. 02/26/2016, Bing Li
		this.collaborator.shutdown(this);
		
		// Clear the queue to release resources. 11/29/2014, Bing Li
		if (this.queue != null)
		{
			this.queue.clear();
		}
	}

	@Override
	public synchronized void dispose(long timeout) throws InterruptedException
	{
		// The above shutdown lines are combined and executed atomically. 02/26/2016, Bing Li
		this.collaborator.shutdown(this);
		
		// Clear the queue to release resources. 11/29/2014, Bing Li
		if (this.queue != null)
		{
			this.queue.clear();
		}
	}

	/*
	 * Expose the key for the convenient management. 11/29/2014, Bing Li
	 */
	public String getKey()
	{
		return this.key;
	}
	
	/*
	 * Expose the dispatcher key. 11/29/2014, Bing Li
	 */
	public String getDispatcherKey()
	{
		return this.dispatcherKey;
	}
	
	/*
	 * Enqueue the broadcast request. 11/29/2014, Bing Li
	 */
	public void enqueue(Request request) throws IllegalStateException
	{
		// Set the state of the thread to be busy. 11/29/2014, Bing Li
//		this.setBusy();
		this.idleLock.lock();
		// Set the state of busy for the thread. 02/07/2016, Bing Li
		this.isIdle = false;
		// Enqueue the request and its relevant output stream and lock. 11/29/2014, Bing Li
		this.queue.add(request);
		this.idleLock.unlock();
		// Notify the waiting thread to keep on working since new requests are received. 11/29/2014, Bing Li
		this.collaborator.signal();
	}
	
	/*
	 * Set the state to be busy. 11/29/2014, Bing Li
	 */
	/*
	private synchronized void setBusy()
	{
		this.isIdle = false;
	}
	*/
	
	/*
	 * Set the state to be idle. 11/29/2014, Bing Li
	 */
	/*
	private synchronized void setIdle()
	{
		this.isIdle = true;
	}
	*/
	
	/*
	 * Get the current size of the queue. 11/29/2014, Bing Li
	 */
	@Override
//	public int getQueueSize()
	public int getWorkload()
	{
		return this.queue.size();
	}

	/*
	 * The method intends to stop the thread temporarily when no requests are available. A thread is identified as being idle immediately after the temporary waiting is finished. 11/29/2014, Bing Li
	 */
	public void holdOn(long waitTime) throws InterruptedException
	{
		// The lock intends to avoid the problem to shutdown the thread when the thread is holding on. 02/06/2016, Bing Li
		this.idleLock.lock();
		// Set the state of busy for the thread. 02/07/2016, Bing Li
		this.isIdle = false;
		this.idleLock.unlock();
		this.collaborator.holdOn(waitTime);
//		this.setIdle();
		this.idleLock.lock();
		// Only when the queue is empty, the thread is set to be busy. 02/07/2016, Bing Li
		if (this.queue.size() <= 0)
		{
			// Set the state of the thread to be idle after waiting for some time. 11/04/2014, Bing Li
			this.isIdle = true;
		}
		this.idleLock.unlock();
	}
	
	/*
	 * Check whether the shutdown flag of the thread is set or not. It might take some time for the thread to be shutdown practically even though the flag is set. 11/29/2014, Bing Li 
	 */
	public boolean isShutdown()
	{
		return this.collaborator.isShutdown();
	}
	
	/*
	 * Check whether the current size of the queue reaches the upper limit. 11/29/2014, Bing Li
	 */
	public boolean isFull()
	{
		return this.queue.size() >= this.taskSize;
	}
	
	/*
	 * Check whether the queue is empty. 11/29/2014, Bing Li
	 */
	public boolean isEmpty()
	{
		return this.queue.size() <= 0;
	}
	
	/*
	 * Check whether the thread is idle or not. 11/29/2014, Bing Li
	 */
//	public synchronized boolean isIdle()
	public boolean isIdle()
	{
		this.idleLock.lock();
		try
		{
			// The thread is believed to be idle only when the request queue is empty and the idle is set to be true. The lock mechanism prevents one possibility that the queue gets new messages and the idle is set to be true. The situation occurs when the size of the queue and the idle value are checked asynchronously. Both of them being detected are a better solution. The idle guarantees the sufficient time has been waited and the queue size indicates that the thread is really not busy. 02/07/2016, Bing Li
			if (this.queue.size() <= 0)
			{
				return this.isIdle;
			}
			else
			{
				// If the queue size is not empty, the thread is believed to be busy even though the idle is set to be true. 02/07/2016, Bing Li
				return false;
			}
		}
		finally
		{
			this.idleLock.unlock();
		}
	}

	/*
	 * Dequeue the request from the queue. 11/29/2014, Bing Li
	 */
	public Request getRequest() throws InterruptedException
	{
		return this.queue.poll();
	}
	
	/*
	 * After the response is created, the method is responsible for sending it back to the anycast initiator. 11/29/2014, Bing Li
	 */
	public synchronized void respond(Response response) throws IOException
	{
		this.pool.send(this.ipPort, response);
	}

	/*
	 * Notify the binder that one thread completes its task. 11/29/2014, Bing Li
	 */
	public synchronized void bind(String threadKey, Request request)
	{
		this.reqBinder.bind(threadKey, request);
	}
	
	/*
	 * Dispose the request and the response. 11/29/2014, Bing Li
	 */
	public synchronized void disposeResponse(Response response)
	{
		response = null;
	}

	/*
	@Override
	public int compareTo(BoundRequestQueue<Request, Response, RequestBinder> obj)
	{
		if (obj != null)
		{
			if (this.queue.size() > obj.getQueueSize())
			{
				return 1;
			}
			else if (this.queue.size() == obj.getQueueSize())
			{
				return 0;
			}
			else
			{
				return -1;
			}
		}
		else
		{
			return 1;
		}
	}
	*/
}
