package org.greatfree.exceptions;

// Created: 08/22/2018, Bing Li
public class DistributedNodeFailedException extends Exception
{
	private static final long serialVersionUID = 8076590972662511439L;
	
	private String nodeKey;
	
	public DistributedNodeFailedException(String nodeKey)
	{
		this.nodeKey = nodeKey;
	}

	public String getNodeKey()
	{
		return this.nodeKey;
	}
}
