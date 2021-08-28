package org.greatfree.util;

import java.util.concurrent.atomic.AtomicBoolean;

import org.greatfree.concurrency.Sync;

/*
 * The class is a flag that represents whether the client process is set to be terminated or not. For some long running threads, they can check the flag to stop their tasks immediately. 09/21/2014, Bing Li
 * 
 * Since a process being terminated is its unique state. The class is implemented in the pattern of singleton. 09/21//2014, Bing Li
 */

// Created: 09/21/2014, Bing Li
public class TerminateSignal
{
	// The flag to indicate whether the client process is set to be terminated or not. 09/21/2014, Bing Li
	private AtomicBoolean isTerminated;
	
	private Sync collaborator;

	/*
	 * Initialize. 09/21/2014, Bing Li
	 */
	private TerminateSignal()
	{
		this.isTerminated = new AtomicBoolean(false);
		this.collaborator = new Sync(false);
	}

	// Implement it as a singleton. 09/21/2014, Bing Li
	private static TerminateSignal instance = new TerminateSignal();
	
	public static TerminateSignal SIGNAL()
	{
		if (instance == null)
		{
			instance = new TerminateSignal();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	public void waitTermination(long timeout)
	{
		this.collaborator.holdOn(timeout);
	}
	
	public void waitTermination()
	{
		this.collaborator.holdOn();
	}
	
	public void notifyTermination()
	{
		this.collaborator.signal();
	}
	
	public void notifyAllTermination()
	{
		this.collaborator.signalAll();
	}
	
	public boolean isTerminated()
	{
		return this.isTerminated.get();
	}
	
	public void setTerminated()
	{
		ServerStatus.FREE().setShutdown();
		this.isTerminated.set(true);;
	}
}
