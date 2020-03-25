package org.greatfree.concurrency;

// Created: 05/19/2018, Bing Li
public abstract class ThreaderTask extends Thread
{
	public abstract void dispose() throws InterruptedException;
	public abstract void dispose(long timeout) throws InterruptedException;
}
