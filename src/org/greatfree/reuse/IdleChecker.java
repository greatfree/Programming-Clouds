package org.greatfree.reuse;

import java.util.TimerTask;

import org.greatfree.util.HashFreeObject;

/*
 * The idle checker works with the ResourcePool to check the idle states of resources. The checking method is invoked by calling back of the ResourcePool. 11/26/2014, Bing Li
 */

// Created: 11/26/2014, Bing Li
public class IdleChecker<Source, Resource extends HashFreeObject, ResourceCreator extends HashCreatable<Source, Resource>, ResourceDisposer extends HashDisposable<Resource>> extends TimerTask
{
	// The resource pool which contains resources to be checked. 11/26/2014, Bing Li
	private ResourcePool<Source, Resource, ResourceCreator, ResourceDisposer> pool;

	/*
	 * Initialize the idle checker. 11/26/2014, Bing Li
	 */
	public IdleChecker(ResourcePool<Source, Resource, ResourceCreator, ResourceDisposer> pool)
	{
		this.pool = pool;
	}

	/*
	 * Check the idle states of the resources concurrently.
	 */
	@Override
	public void run()
	{
		try
		{
			this.pool.checkIdle();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
}
