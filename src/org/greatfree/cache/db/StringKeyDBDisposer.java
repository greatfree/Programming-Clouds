package org.greatfree.cache.db;

import java.io.IOException;

import org.greatfree.reuse.Disposable;

// Created: 05/22/2017, Bing Li
public class StringKeyDBDisposer implements Disposable<StringKeyDB>
{

	@Override
	public void dispose(StringKeyDB rsc) throws IOException
	{
		rsc.dispose();
	}

}
