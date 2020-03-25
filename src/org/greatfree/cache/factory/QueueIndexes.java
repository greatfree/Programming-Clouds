package org.greatfree.cache.factory;

import java.io.Serializable;

// Created: 06/07/2017, Bing Li
class QueueIndexes implements Serializable
{
	private static final long serialVersionUID = 539380230811362357L;
	
//	private Set<String> keys;

	private int headIndex;
	private int tailIndex;

//	public QueueIndexes(Set<String> keys)
	public QueueIndexes()
	{
//		this.keys = keys;
		this.headIndex = 0;
		this.tailIndex = 0;
	}

	/*
	public Set<String> getKeys()
	{
		return this.keys;
	}
	*/
	
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
