package com.greatfree.testing.db;

import java.io.IOException;

import com.greatfree.reuse.Creatable;

/*
 * A creator that initializes instances of NodeDB. 11/04/2014, Bing Li
 */

// Created: 11/04/2014, Bing Li
public class NodeDBCreator implements Creatable<String, NodeDB>
{
	@Override
	public NodeDB createResourceInstance(String source) throws IOException
	{
		return new NodeDB(source);
	}
}
