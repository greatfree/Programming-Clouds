package com.greatfree.testing.db;

import java.io.IOException;

import com.greatfree.reuse.Creatable;

/*
 * A creator that initializes instances of URLDB. It works with the URLDBPool to manage the instances of URLDB. 11/25/2014, Bing Li
 */

// Created: 11/25/2014, Bing Li
public class URLDBCreator implements Creatable<String, URLDB>
{
	@Override
	public URLDB createResourceInstance(String source) throws IOException
	{
		return new URLDB(source);
	}
}
