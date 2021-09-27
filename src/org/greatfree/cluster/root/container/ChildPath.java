package org.greatfree.cluster.root.container;

import java.io.Serializable;

// Created: 09/23/2021, Bing Li
class ChildPath implements Serializable
{
	private static final long serialVersionUID = -8395307466311968083L;
	
	private String childID;
	private String path;
	
	public ChildPath(String childID, String path)
	{
		this.childID = childID;
		this.path = path;
	}

	public String getChildID()
	{
		return this.childID;
	}
	
	public String getPath()
	{
		return this.path;
	}
}
