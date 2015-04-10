package com.greatfree.concurrency;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

import com.greatfree.multicast.BroadcastRequest;
import com.greatfree.multicast.BroadcastResponse;
import com.greatfree.remote.FreeClientPool;
import com.greatfree.remote.IPPort;
import com.greatfree.util.Tools;
import com.greatfree.util.UtilConfig;

/*
 * The thread is the base one to support implementing broadcast requests in a concurrent way. 11/29/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
public class BroadcastRequestQueue<Request extends BroadcastRequest, Response extends BroadcastResponse> extends Thread
{
	// The unique key of the thread. It is convenient for managing it by a table-like mechanism. 11/29/2014, Bing Li
	private String key;
	// The queue to take the received broadcast requests. 11/29/2014, Bing Li
	private LinkedBlockingQueue<Request> queue;
	// The IP/port of the broadcast original initiator. The response must be sent back to it. 11/29/2014, Bing Li
	private IPPort ipPort;
	// The maximum size of the queue. 11/29/2014, Bing Li
	private int taskSize;
	// It is necessary to keep the thread waiting when no requests are available. The collaborator is used to notify the thread to keep working when requests are received. When the thread is idle enough, it can be collected. The collaborator is also used to control the life cycle of the thread. 11/29/2014, Bing Li
	private Collaborator collaborator;
	// The flag that represents whether the thread is busy or idle. 11/29/2014, Bing Li
	private boolean isIdle;
	// The TCP client pool. With it, it is able to get the instance of the client to connect the initiator of the broadcast. 11/29/2014, Bing Li
	private FreeClientPool pool;

	/*
	 * Initialize an instance. 11/29/2014, Bing Li
	 */
	public BroadcastRequestQueue(IPPort ipPort, FreeClientPool pool)
	{
		this.key = Tools.generateUniqueKey();
		this.queue = new LinkedBlockingQueue<Request>();
		this.ipPort = ipPort;
		this.taskSize = UtilConfig.NO_QUEUE_SIZE;
		this.collaborator = new Collaborator();
		this.isIdle = false;
		this.pool = pool;
	}
	
	/*
	 * Initialize an instance. 11/29/2014, Bing Li
	 */
	public BroadcastRequestQueue(IPPort ipPort, FreeClientPool pool, int taskSize)
	{
		this.key = Tools.generateUniqueKey();
		this.queue = new LinkedBlockingQueue<Request>();
		this.ipPort = ipPort;
		this.taskSize = taskSize;
		this.collaborator = new Collaborator();
		this.isIdle = false;
		this.pool = pool;
	}
	
	/*
	 * Dispose the instance of the class. 11/29/2014, Bing Li
	 */
	public synchronized void dispose()
	{
		// Set the flag to be the state of being shutdown. 11/29/2014, Bing Li
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
	 * Enqueue the broadcast request. 11/29/2014, Bing Li
	 */
	public void enqueue(Request request)
	{
		// Set the state of the thread to be busy. 11/29/2014, Bing Li
		this.setBusy();
		// Enqueue the request and its relevant output stream and lock. 11/29/2014, Bing Li
		this.queue.add(request);
		// Notify the waiting thread to keep on working since new requests are received. 11/29/2014, Bing Li
		this.collaborator.signal();
	}
	
	/*
	 * Set the state to be busy. 11/29/2014, Bing Li
	 */
	private synchronized void setBusy()
	{
		this.isIdle = false;
	}
	
	/*
	 * Set the state to be idle. 11/29/2014, Bing Li
	 */
	private synchronized void setIdle()
	{
		this.isIdle = true;
	}
	
	/*
	 * The method intends to stop the thread temporarily when no requests are available. A thread is identified as being idle immediately after the temporary waiting is finished. 11/29/2014, Bing Li
	 */
	public void holdOn(long waitTime) throws InterruptedException
	{
		// Wait for some time, which is determined by the value of waitTime. 11/29/2014, Bing Li
		this.collaborator.holdOn(waitTime);
		// Set the state of the thread to be idle after waiting for some time. 11/29/2014, Bing Li
		this.setIdle();
	}
	
	/*
	 * Check whether the shutdown flag of the thread is set or not. It might take some time for the thread to be shutdown practically even though the flag is set. 11/29/2014, Bing Li 
	 */
	public boolean isShutdown()
	{
		return this.collaborator.isShutdown();
	}
	
	/*
	 * Check whether the queue is empty. 11/29/2014, Bing Li
	 */
	public boolean isEmpty()
	{
		return this.queue.size() <= 0;
	}
	
	/*
	 * Check whether the current size of the queue reaches the upper limit. 11/29/2014, Bing Li
	 */
	public boolean isFull()
	{
		return this.queue.size() >= this.taskSize;
	}
	
	/*
	 * Check whether the thread is idle or not. 11/29/2014, Bing Li
	 */
	public synchronized boolean isIdle()
	{
		return this.isIdle;
	}
	
	/*
	 * Get the current size of the queue. 11/29/2014, Bing Li
	 */
	public int getQueueSize()
	{
		return this.queue.size();
	}
	
	/*
	 * Dequeue the request from the queue. 11/29/2014, Bing Li
	 */
	public Request getRequest() throws InterruptedException
	{
		return this.queue.take();
	}

	/*
	 * After the response is created, the method is responsible for sending it back to the anycast initiator. 11/29/2014, Bing Li
	 */
	public synchronized void respond(Response response) throws IOException
	{
		this.pool.send(this.ipPort, response);
	}
	
	/*
	 * Dispose the request and the response. 11/29/2014, Bing Li
	 */
	public synchronized void DisposeMessage(Request request, Response response)
	{
		request = null;
		response = null;
	}
	
	/*
	 * Dispose the request. 11/29/2014, Bing Li
	 */
	public synchronized void DisposeMessage(Request request)
	{
		request = null;
	}
	
	/*
	 * Dispose the response. 11/29/2014, Bing Li
	 */
	public synchronized void DisposeMessage(Response response)
	{
		response = null;
	}
}
