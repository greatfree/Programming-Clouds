package org.greatfree.reuse;

import java.io.IOException;
import java.util.TimerTask;

import org.greatfree.util.FreeObject;

/*
 * The class aims to check periodically whether an idle resource is idle long enough to be disposed. 11/03/2014, Bing Li
 */

// Created: 11/03/2014, Bing Li
public class QueuedIdleChecker<Resource extends FreeObject, ResourceCreator extends Creatable<String, Resource>, ResourceDisposer extends Disposable<Resource>> extends TimerTask
{
	// Declare the pool. 11/03/2014, Bing Li
	private QueuedPool<Resource, ResourceCreator, ResourceDisposer> pool;

	/*
	 * Initialize the pool, which is assigned from the outside. The method of idle checking is called asynchronously by the class. 11/03/2014, Bing Li
	 */
	public QueuedIdleChecker(QueuedPool<Resource, ResourceCreator, ResourceDisposer> pool)
	{
		this.pool = pool;
	}

	@Override
	public void run()
	{
		try
		{
			// Call the idle checking method. 11/03/2014, Bing Li
			this.pool.checkIdle();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
