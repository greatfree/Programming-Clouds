package org.greatfree.concurrency.batch;

// Created: 04/23/2018, Bing Li
public interface ThreadNotifiable<Task>
{
	public void pause() throws InterruptedException;
	public void keepOn();
	public void restoreFast(String key) throws InterruptedException;
	public void done(Task task);
}
