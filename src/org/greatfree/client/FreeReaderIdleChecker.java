package org.greatfree.client;

import java.io.IOException;
import java.util.TimerTask;

/*
 * The class is used to call back the method of checkIdle of the instance of FreeReaderPool. 11/05/2014, Bing Li
 */

// Created: 11/05/2014, Bing Li
//public class FreeReaderIdleChecker extends TimerTask
class FreeReaderIdleChecker extends TimerTask
{
	// Declare an instance of FreeReaderPool. 11/05/2014, Bing Li
	private FreeReaderPool pool;

	/*
	 * The instance of FreeReaderPool is initialized outside and assigned to the object. 11/05/2014, Bing Li
	 */
	public FreeReaderIdleChecker(FreeReaderPool pool)
	{
		this.pool = pool;
	}

	/*
	 * Invoke the method, checkIdle(), periodically. 11/05/2014, Bing Li
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
