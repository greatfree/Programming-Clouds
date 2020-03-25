package org.greatfree.cache.factory;

import java.io.Serializable;

// Created: 06/07/2017, Bing Li
class StackIndexes implements Serializable
{
	private static final long serialVersionUID = 1973527665635029377L;

	private int headIndex;
	private int tailIndex;

	public StackIndexes()
	{
		this.headIndex = 0;
		this.tailIndex = -1;
	}

	public int getHeadIndex()
	{
		return this.headIndex;
	}
	
	public void setHeadIndex(int headIndex)
	{
		this.headIndex = headIndex;
	}
	
	public int getTailIndex()
	{
		return this.tailIndex;
	}
	
	public void setTailIndex(int tailIndex)
	{
		this.tailIndex = tailIndex;
	}
}
