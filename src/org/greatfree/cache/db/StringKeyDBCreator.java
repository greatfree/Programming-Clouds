package org.greatfree.cache.db;

import java.io.IOException;

import org.greatfree.reuse.Creatable;

// Created: 05/22/2017, Bing Li
public class StringKeyDBCreator implements Creatable<String, StringKeyDB>
{

	@Override
	public StringKeyDB createClientInstance(String source) throws IOException
	{
		return new StringKeyDB(source);
	}

}
