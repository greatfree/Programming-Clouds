package org.greatfree.concurrency;

/*
 * To simplify the Runner constructor, the two method, run() and dispose(), are integrated into the interface. 05/18/2018, Bing Li
 */

// Created: 05/18/2018, Bing Li
public abstract class RunnerTask implements Runnable
{
	// The reason to design the method aims to enable comparable for Runner such that it is possible to evaluate the workload of each thread. 05/19/2018, Bing Li
	public abstract int getWorkload();
	public abstract void dispose() throws InterruptedException;
	public abstract void dispose(long timeout) throws InterruptedException;
}
