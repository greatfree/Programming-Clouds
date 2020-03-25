package org.greatfree.reuse;

import java.io.IOException;
import java.util.TimerTask;

import org.greatfree.util.FreeObject;

/*
 * The class runs periodically to check whether a resource being managed in a resource pool, such as RetrievablePool, is idle enough time. If so, it collects the resource. 08/26/2014, Bing Li
 */

// Created: 08/26/2014, Bing Li
public class RetrievableIdleChecker<Source extends FreeObject, Resource extends FreeObject, ResourceCreator extends Creatable<Source, Resource>, ResourceDisposer extends Disposable<Resource>> extends TimerTask
{
	// The resource pool contains all of the idle resources to be checked. 08/26/2014, Bing Li
	private RetrievablePool<Source, Resource, ResourceCreator, ResourceDisposer> pool;

	/*
	 * Initialize the checker. 08/26/2014, Bing Li
	 */
	public RetrievableIdleChecker(RetrievablePool<Source, Resource, ResourceCreator, ResourceDisposer> pool)
	{
		this.pool = pool;
	}

	/*
	 * The timer must run asynchronously to check whether the resources are idle enough time. 08/26/2014, Bing Li
	 */
	@Override
	public void run()
	{
		try
		{
			this.pool.checkIdle();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
