package org.greatfree.client;

import java.io.IOException;

import org.greatfree.reuse.Disposable;

/*
 * The class implements the interface of Disposable and aims to invoke the dispose method of an instance of FreeClient to collect the resource. It is used a resource disposer in RetrievablePool. 09/17/2014, Bing Li
 */

// Created: 09/17/2014, Bing Li
//public class FreeClientDisposer implements Disposable<FreeClient>
class FreeClientDisposer implements Disposable<FreeClient>
{
	// Dispose a FreeClient by invoking its dispose method. 09/17/2014, Bing Li
	@Override
	public void dispose(FreeClient t) throws IOException
	{
		t.dispose();
	}
}
