package org.greatfree.reuse;

/*
 * This is an interface to dispose a thread. 09/20/2014, Bing Li
 */

// Created: 08/04/2014, Bing Li
public interface ThreadDisposable<Resource extends Runnable>
{
	public void dispose(Resource r) throws InterruptedException;
	public void dispose(Resource r, long time) throws InterruptedException;
}
