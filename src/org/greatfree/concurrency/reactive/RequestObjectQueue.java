package org.greatfree.concurrency.reactive;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import org.greatfree.concurrency.Request;
import org.greatfree.concurrency.Response;
import org.greatfree.concurrency.RunnerTask;
import org.greatfree.concurrency.Sync;
import org.greatfree.util.Tools;

/**
 * 
 * @author libing
 * 
 * 08/01/2022
 *
 */
public abstract class RequestObjectQueue<Query extends Request, Answer extends Response> extends RunnerTask
{
	private final String key;
	private Queue<Query> queue;
	private final int requestQueueSize;
	private Sync collaborator;
	private boolean isIdle;
	private ReentrantLock idleLock;
	private AtomicBoolean isSysInterrupted;
	private AtomicBoolean isHung;

	public RequestObjectQueue(int queueSize)
	{
		this.key = Tools.generateUniqueKey();
		this.queue = new LinkedBlockingQueue<Query>();
		this.requestQueueSize = queueSize;
		this.collaborator = new Sync();
		this.isIdle = false;
		this.idleLock = new ReentrantLock();
		this.isSysInterrupted = new AtomicBoolean(false);
		this.isHung = new AtomicBoolean(false);
	}

	public synchronized void dispose() throws InterruptedException
	{
		this.collaborator.shutdown(this);
		if (this.queue != null)
		{
			this.queue.clear();
		}
		if (this.isHung.get())
		{
			this.interrupt();
		}
	}
	
	public synchronized void dispose(long timeout) throws InterruptedException
	{
		this.collaborator.shutdown(this);
		if (this.queue != null)
		{
			this.queue.clear();
		}
		if (this.isHung.get())
		{
			this.interrupt();
		}
	}

	public String getKey()
	{
		return this.key;
	}

	public void interrupt()
	{
		this.isSysInterrupted.set(true);
		Thread.currentThread().interrupt();
	}
	
	public boolean isSysInterrupted()
	{
		return this.isSysInterrupted.get();
	}

	public boolean isHung()
	{
		return this.isHung.get();
	}
	
	public synchronized void disposeObject(Query query, Answer answer)
	{
		this.isHung.set(false);
		query = null;
		answer = null;
	}

	public void enqueue(Query request)
	{
		this.idleLock.lock();
		try
		{
			this.isIdle = false;
			this.queue.add(request);
		}
		finally
		{
			this.idleLock.unlock();
		}
		this.collaborator.signal();
	}

	public void holdOn(long waitTime) throws InterruptedException
	{
		if (this.collaborator.holdOn(waitTime))
		{
			this.idleLock.lock();
			if (this.queue.size() <= 0)
			{
				this.isIdle = true;
			}
			this.idleLock.unlock();
		}
	}

	public boolean isShutdown()
	{
		return this.collaborator.isShutdown();
	}

	public boolean isEmpty()
	{
		return this.queue.size() <= 0;
	}

	public boolean isFull()
	{
		return this.queue.size() >= this.requestQueueSize;
	}

	public boolean isIdle()
	{
		this.idleLock.lock();
		try
		{
			if (this.queue.size() <= 0)
			{
				return this.isIdle;
			}
			else
			{
				return false;
			}
		}
		finally
		{
			this.idleLock.unlock();
		}
	}

	public int getWorkload()
	{
		return this.queue.size();
	}

	public Query dequeue()
	{
		this.isHung.set(true);
		return this.queue.poll();
	}
	
	public synchronized void saveResponse(Response response)
	{
		RendezvousPoint.REDUCE().saveResponse(response);
	}
}
