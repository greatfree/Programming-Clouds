package com.greatfree.reuse;

/*
 * The class is an interface to define a thread's disposer. 07/30/2014, Bing Li
 */

// Created: 07/17/2014, Bing Li
public interface RunDisposable<Resource extends Runnable>
{
	public void dispose(Resource r);
	public void dispose(Resource r, long time);
}
