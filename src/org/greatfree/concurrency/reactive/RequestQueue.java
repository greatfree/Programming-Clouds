package org.greatfree.concurrency.reactive;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.greatfree.client.OutMessageStream;
import org.greatfree.concurrency.RunnerTask;
import org.greatfree.concurrency.Sync;
import org.greatfree.message.ServerMessage;
import org.greatfree.util.Tools;

/*
 * This is a thread that receive requests from a client, put those messages into a queue and prepare for further processing. It must be derived by sub classes to provide the real responses for the requests. 09/22/2014, Bing Li
 */

// Created: 09/22/2014, Bing Li
// public abstract class RequestQueue<Request extends ServerMessage, Stream extends OutMessageStream<Request>, Response extends ServerMessage> implements Runnable, Comparable<RequestQueue<Request, Stream, Response>>
public abstract class RequestQueue<Request extends ServerMessage, Stream extends OutMessageStream<Request>, Response extends ServerMessage> extends RunnerTask
{
	// The unique key of the thread. It is convenient for managing it by a table-like mechanism. 09/22/2014, Bing Li
	private final String key;
	// The queue that saves the request stream, which extends the OutMessageStream, including the associated output stream,  the lock and the request. 09/22/2014, Bing Li
	private Queue<Stream> queue;
	// The maximum size of the queue. 09/22/2014, Bing Li
	private final int notificationQueueSize;
	// It is necessary to keep the thread waiting when no requests are available. The collaborator is used to notify the thread to keep working when requests are received. When the thread is idle enough, it can be collected. The collaborator is also used to control the life cycle of the thread. 09/22/2014, Bing Li
	private Sync collaborator;
	// The flag that represents whether the thread is busy or idle. 09/22/2014, Bing Li
	private boolean isIdle;
	// The lock is critical to keep synchronous to manage the idle/busy state of the thread. 02/07/2016, Bing Li
	private ReentrantLock idleLock;

	// The thread is possibly interrupted by the thread pool/the system when the thread is hung by a task permanently. If so, the exception is not required to be displayed according to the flag. 11/05/2019, Bing Li
//	private AtomicBoolean isSysInterrupted;
	private AtomicBoolean isHung;
	/*
	 * The server key is added as a new field. 03/30/2020, Bing Li
	 * 
	 * 	The key is used to identify server tasks if multiple servers instances exist within a single process. In the previous versions, only one server tasks are allowed. It is a defect if multiple instances of servers exist in a process since they are overwritten one another. 03/30/2020, Bing Li
	 */
	private String serverKey;

	/*
	 * Initialize an instance. 09/22/2014, Bing Li
	 */
	public RequestQueue(int taskSize)
	{
		// Generate a unique key for the instance of the class. 09/22/2014, Bing Li
		this.key = Tools.generateUniqueKey();
		// Initialize the queue to keep received the request streams, which consist of requests and their relevant streams and locks. It is critical to set up the queue without the limit of the length since the message is forced to be put into the queue when the count of threads reach the maximum and each one's queue is full. 09/22/2014, Bing Li
		this.queue = new LinkedBlockingQueue<Stream>();
		this.notificationQueueSize = taskSize;
		this.collaborator = new Sync();
		// Setting the idle is false means that the thread is busy when being initialized. 09/22/2014, Bing Li
		this.isIdle = false;
		// Initialize the lock that keeps synchronous of the threa's state, busy or idle. 02/07/2016, Bing Li
		this.idleLock = new ReentrantLock();
//		this.isSysInterrupted = new AtomicBoolean(false);
		this.isHung = new AtomicBoolean(false);
	}

	/*
	 * Dispose the instance of the class. 09/22/2014, Bing Li
	 */
	@Override
	public synchronized void dispose() throws InterruptedException
	{
		// Set the flag to be the state of being shutdown. 09/22/2014, Bing Li
		/*
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
		*/

		// The above shutdown lines are combined and executed atomically. 02/26/2016, Bing Li
		this.collaborator.shutdown(this);
		
		// Clear the queue to release resources. 09/22/2014, Bing Li
		if (this.queue != null)
		{
			this.queue.clear();
		}
		/*
		if (this.isHung.get())
		{
			this.interrupt();
		}
		*/
	}

	@Override
	public synchronized void dispose(long timeout) throws InterruptedException
	{
		// The above shutdown lines are combined and executed atomically. 02/26/2016, Bing Li
		this.collaborator.shutdown(this);
		
		// Clear the queue to release resources. 09/22/2014, Bing Li
		if (this.queue != null)
		{
			this.queue.clear();
		}
		/*
		if (this.isHung.get())
		{
			this.interrupt();
		}
		*/
	}

	/*
	 * Expose the key for the convenient management. 09/22/2014, Bing Li
	 */
	public String getKey()
	{
		return this.key;
	}

	/*
	 * The server key is added as a new field. 03/30/2020, Bing Li
	 * 
	 * 	The key is used to identify server tasks if multiple servers instances exist within a single process. In the previous versions, only one server tasks are allowed. It is a defect if multiple instances of servers exist in a process since they are overwritten one another. 03/30/2020, Bing Li
	 */
	public void setServerKey(String key)
	{
		this.serverKey = key;
	}
	
	public String getServerKey()
	{
		return this.serverKey;
	}

	/*
	 * 
	 * It is useless and incorrect to interrupt a thread this way. It should be done in the runner, which has a method, interrupt(). 11/07/2019, Bing Li
	 * 
	 * The method aims to kill one thread that is hung permanently by a task. 11/05/2019, Bing Li
	 */
	/*
	public void interrupt()
	{
		this.isSysInterrupted.set(true);
		Thread.currentThread().interrupt();
	}
	*/

	/*
	public boolean isSysInterrupted()
	{
		return this.isSysInterrupted.get();
	}
	*/

	public boolean isHung()
	{
		return this.isHung.get();
	}

	/*
	 * Enqueue the wrapper, the request stream, for the request, the output stream and the lock. 09/22/2014, Bing Li
	 */
//	public boolean enqueue(Stream request)
	public void enqueue(Stream request)
	{
		// Set the state of the thread to be busy. 09/22/2014, Bing Li
//		this.setBusy();
		this.idleLock.lock();
		try
		{
			// Set the state of busy for the thread. 02/07/2016, Bing Li
			this.isIdle = false;
			// Enqueue the request and its relevant output stream and lock. 09/22/2014, Bing Li
			this.queue.add(request);
			/*
			if (!this.collaborator.isShutdown())
			{
				// Notify the waiting thread to keep on working since new requests are received. 09/22/2014, Bing Li
				this.collaborator.signal();
				return true;
			}
			else
			{
				return false;
			}
			*/
		}
		finally
		{
			this.idleLock.unlock();
		}
		// Notify the waiting thread to keep on working since new requests are received. 09/22/2014, Bing Li
		this.collaborator.signal();
	}

	/*
	 * Set the state to be busy. 09/22/2014, Bing Li
	 */
	/*
	private synchronized void setBusy()
	{
		this.isIdle = false;
	}
	*/

	/*
	 * Set the state to be idle. 09/22/2014, Bing Li
	 */
	/*
	private synchronized void setIdle()
	{
		this.isIdle = true;
	}
	*/

	/*
	 * The method intends to stop the thread temporarily when no requests are available. A thread is identified as being idle immediately after the temporary waiting is finished. 09/22/2014, Bing Li
	 */
	public void holdOn(long waitTime) throws InterruptedException
	{
		/*
		// The lock intends to avoid the problem to shutdown the thread when the thread is holding on. 02/06/2016, Bing Li
		this.idleLock.lock();
		// Set a local variable to keep the isIdle state before holding on. 02/22/2016, Bing Li
		boolean isIdleBeforeHoldOn = this.isIdle;
		// Set the state of busy for the thread. 02/07/2016, Bing Li
		this.isIdle = false;
		this.idleLock.unlock();
		*/
		// Wait for some time, which is determined by the value of waitTime. 09/22/2014, Bing Li
		if (this.collaborator.holdOn(waitTime))
		{
			// Set the state of the thread to be idle after waiting for some time. 09/22/2014, Bing Li
//			this.setIdle();
			this.idleLock.lock();
			// Only when the queue is empty, the thread is set to be busy. 02/07/2016, Bing Li
			if (this.queue.size() <= 0)
			{
				// Set the state of the thread to be idle after waiting for some time. 11/04/2014, Bing Li
				this.isIdle = true;
				// If the thread is idle before holding on and the queue is empty after the waiting, it really indicates the thread is idle. So, it can dispose itself at this moment. 02/22/2016, Bing Li
				/*
				if (isIdleBeforeHoldOn)
				{
					// Dispose the thread itself. 02/22/2016, Bing Li
					this.dispose();
				}
				*/
			}
			this.idleLock.unlock();
		}
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
		return this.queue.size() >= this.notificationQueueSize;
	}

	/*
	 * Check whether the thread is idle or not. 09/22/2014, Bing Li
	 */
//	public synchronized boolean isIdle()
	public boolean isIdle()
	{
//		return this.isIdle;
		this.idleLock.lock();
		try
		{
//			System.out.println("RequestQueue: this.queue.size() = " + this.queue.size());
//			System.out.println("RequestQueue: this.isIdle = " + this.isIdle);
			// The thread is believed to be idle only when the notification queue is empty and the idle is set to be true. The lock mechanism prevents one possibility that the queue gets new messages and the idle is set to be true. The situation occurs when the size of the queue and the idle value are checked asynchronously. Both of them being detected are a better solution. The idle guarantees the sufficient time has been waited and the queue size indicates that the thread is really not busy. 02/07/2016, Bing Li
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
	 * Get the current size of the queue. 09/22/2014, Bing Li
	 */
	@Override
//	public int getQueueSize()
	public int getWorkload()
	{
		return this.queue.size();
	}

	/*
	 * Dequeue the request stream from the queue. The stream includes the request, the output stream and the lock. They are dequeued for processing and responding. 09/22/2014, Bing Li
	 */
	public Stream getRequest()
	{
		this.isHung.set(true);
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
		this.isHung.set(false);
		request.disposeMessage();
		response = null;
	}

	/*
	 * Dispose the request stream. Sometimes the response needs to be forwarded. So it should be disposed. 09/22/2014, Bing Li
	 */
	public synchronized void disposeMessage(Stream request)
	{
		this.isHung.set(false);
		request.disposeMessage();
	}

	/*
	 * Compare threads according to their task loads in the request queue. 02/01/2016, Bing Li
	 */
	/*
	@Override
	public int compareTo(RequestQueue<Request, Stream, Response> obj)
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
