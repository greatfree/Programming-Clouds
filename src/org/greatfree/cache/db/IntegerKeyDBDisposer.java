package org.greatfree.cache.db;

import java.io.IOException;

import org.greatfree.reuse.Disposable;

// Created: 05/22/2017, Bing Li
public class IntegerKeyDBDisposer implements Disposable<IntegerKeyDB>
{

	@Override
	public void dispose(IntegerKeyDB rsc) throws IOException
	{
		rsc.dispose();
	}

}
