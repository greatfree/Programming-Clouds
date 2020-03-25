package org.greatfree.cache.db;

import java.io.IOException;

import org.greatfree.reuse.Creatable;

// Created: 05/22/2017, Bing Li
public class IntegerKeyDBCreator implements Creatable<String, IntegerKeyDB>
{

	@Override
	public IntegerKeyDB createResourceInstance(String source) throws IOException
	{
		return new IntegerKeyDB(source);
	}

}
