package com.greatfree.concurrency;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;

import com.greatfree.multicast.ServerMessage;
import com.greatfree.remote.OutMessageStream;
import com.greatfree.util.Tools;

/*
 * This is a thread that receive requests from a client, put those messages into a queue and prepare for further processing. It must be derived by sub classes to provide the real responses for the requests. 09/22/2014, Bing Li
 */

// Created: 09/22/2014, Bing Li
public class RequestQueue<Request extends ServerMessage, Stream extends OutMessageStream<Request>, Response extends ServerMessage> extends Thread
{
	// The unique key of the thread. It is convenient for managing it by a table-like mechanism. 09/22/2014, Bing Li
	private String key;
	// The queue that saves the request stream, which extends the OutMessageStream, including the associated output stream,  the lock and the request. 09/22/2014, Bing Li
	private Queue<Stream> queue;
	// The maximum size of the queue. 09/22/2014, Bing Li
	private int maxTaskSize;
	// It is necessary to keep the thread waiting when no requests are available. The collaborator is used to notify the thread to keep working when requests are received. When the thread is idle enough, it can be collected. The collaborator is also used to control the life cycle of the thread. 09/22/2014, Bing Li
	private Collaborator collaborator;
	// The flag that represents whether the thread is busy or idle. 09/22/2014, Bing Li
	private boolean isIdle;

	/*
	 * Initialize an instance. 09/22/2014, Bing Li
	 */
	public RequestQueue(int maxTaskSize)
	{
		// Generate a unique key for the instance of the class. 09/22/2014, Bing Li
		this.key = Tools.generateUniqueKey();
		// Initialize the queue to keep received the request streams, which consist of requests and their relevant streams and locks. It is critical to set up the queue without the limit of the length since the message is forced to be put into the queue when the count of threads reach the maximum and each one's queue is full. 09/22/2014, Bing Li
		this.queue = new LinkedBlockingQueue<Stream>();
		this.maxTaskSize = maxTaskSize;
		this.collaborator = new Collaborator();
		// Setting the idle is false means that the thread is busy when being initialized. 09/22/2014, Bing Li
		this.isIdle = false;
	}

	/*
	 * Dispose the instance of the class. 09/22/2014, Bing Li
	 */
	public synchronized void dispose()
	{
		// Set the flag to be the state of being shutdown. 09/22/2014, Bing Li
		this.collaborator.setShutdown();
		// Notify the thread being waiting to go forward. Since the shutdown flag is set, the thread must die for the notification. 09/22/2014, Bing Li
		this.collaborator.signal();
		try
		{
			// Wait for the thread to die. 09/22/2014, Bing Li
			this.join();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		// Clear the queue to release resources. 09/22/2014, Bing Li
		if (this.queue != null)
		{
			this.queue.clear();
		}
	}

	/*
	 * Expose the key for the convenient management. 09/22/2014, Bing Li
	 */
	public String getKey()
	{
		return this.key;
	}

	/*
	 * Enqueue the wrapper, the request stream, for the request, the output stream and the lock. 09/22/2014, Bing Li
	 */
	public void enqueue(Stream request)
	{
		// Set the state of the thread to be busy. 09/22/2014, Bing Li
		this.setBusy();
		// Enqueue the request and its relevant output stream and lock. 09/22/2014, Bing Li
		this.queue.add(request);
		// Notify the waiting thread to keep on working since new requests are received. 09/22/2014, Bing Li
		this.collaborator.signal();
	}

	/*
	 * Set the state to be busy. 09/22/2014, Bing Li
	 */
	private synchronized void setBusy()
	{
		this.isIdle = false;
	}

	/*
	 * Set the state to be idle. 09/22/2014, Bing Li
	 */
	private synchronized void setIdle()
	{
		this.isIdle = true;
	}

	/*
	 * The method intends to stop the thread temporarily when no requests are available. A thread is identified as being idle immediately after the temporary waiting is finished. 09/22/2014, Bing Li
	 */
	public void holdOn(long waitTime) throws InterruptedException
	{
		// Wait for some time, which is determined by the value of waitTime. 09/22/2014, Bing Li
		this.collaborator.holdOn(waitTime);
		// Set the state of the thread to be idle after waiting for some time. 09/22/2014, Bing Li
		this.setIdle();
	}

	/*
	 * Check whether the shutdown flag of the thread is set or not. It might take some time for the thread to be shutdown practically even though the flag is set. 09/22/2014, Bing Li 
	 */
	public boolean isShutdown()
	{
		return this.collaborator.isShutdown();
	}

	/*
	 * Check whether the queue is empty. 09/22/2014, Bing Li
	 */
	public boolean isEmpty()
	{
		return this.queue.size() <= 0;
	}

	/*
	 * Check whether the current size of the queue reaches the upper limit. 09/22/2014, Bing Li
	 */
	public boolean isFull()
	{
		return this.queue.size() >= this.maxTaskSize;
	}

	/*
	 * Check whether the thread is idle or not. 09/22/2014, Bing Li
	 */
	public synchronized boolean isIdle()
	{
		return this.isIdle;
	}

	/*
	 * Get the current size of the queue. 09/22/2014, Bing Li
	 */
	public int getQueueSize()
	{
		return this.queue.size();
	}

	/*
	 * Dequeue the request stream from the queue. The stream includes the request, the output stream and the lock. They are dequeued for processing and responding. 09/22/2014, Bing Li
	 */
	public Stream getRequest()
	{
		return this.queue.poll();
	}

	/*
	 * After the response is created, the method is responsible for sending it back to the client. 09/22/2014, Bing Li
	 */
	public synchronized void respond(ObjectOutputStream out, Lock outLock, Response response) throws IOException
	{
		// The lock is shared by all of threads that use the output stream. It makes sure that the responding operations are performed in an atomic way.
		outLock.lock();
		try
		{
			// Send the response to the remote end. 09/17/2014, Bing Li
			out.writeObject(response);
			// It is required to invoke the below methods to avoid the memory leak. 09/22/2014, Bing Li
			out.flush();
			out.reset();
		}
		finally
		{
			outLock.unlock();
		}
	}

	/*
	 * Dispose the request stream and the response. 09/22/2014, Bing Li
	 */
	public synchronized void disposeMessage(Stream request, Response response)
	{
		request.disposeMessage();
		response = null;
	}

	/*
	 * Dispose the request stream. Sometimes the response needs to be forwarded. So it should be disposed. 09/22/2014, Bing Li
	 */
	public synchronized void disposeMessage(Stream request)
	{
		request.disposeMessage();
	}
}
