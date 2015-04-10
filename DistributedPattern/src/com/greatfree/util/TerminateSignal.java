package com.greatfree.util;

/*
 * The class is a flag that represents whether the client process is set to be terminated or not. For some long running threads, they can check the flag to stop their tasks immediately. 09/21/2014, Bing Li
 * 
 * Since a process being terminated is its unique state. The class is implemented in the pattern of singleton. 09/21//2014, Bing Li
 */

// Created: 09/21/2014, Bing Li
public class TerminateSignal
{
	// The flag to indicate whether the client process is set to be terminated or not. 09/21/2014, Bing Li
	private boolean isTerminated;

	/*
	 * Initialize. 09/21/2014, Bing Li
	 */
	private TerminateSignal()
	{
		this.isTerminated = false;
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
	
	public boolean isTerminated()
	{
		return this.isTerminated;
	}
	
	public void setTerminated()
	{
		this.isTerminated = true;
	}
}
