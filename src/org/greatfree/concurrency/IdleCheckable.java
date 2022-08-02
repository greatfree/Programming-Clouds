package org.greatfree.concurrency;

/*
 * The interface defines the signatures of two methods for a thread's idle checking. 11/04/2014, Bing Li
 */

// Created: 11/04/2014, Bing Li
public interface IdleCheckable
{
	// The method signature to check the idle state. 11/04/2014, Bing Li
	public void checkIdle() throws InterruptedException;
	// The method signature to set the configurations of idle checking. 11/04/2014, Bing Li
	public void setIdleChecker(long idleCheckDelay, long idleCheckPeriod);
}
