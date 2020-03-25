package org.greatfree.cache.distributed;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;

// Created: 08/01/2018, Bing Li
public class IsEvicted implements Serializable
{
	private static final long serialVersionUID = -9008591867657079402L;
	
	private AtomicBoolean isHappened;
	
	public IsEvicted()
	{
		this.isHappened = new AtomicBoolean(false);
	}

	public void setHappened(boolean isHappened)
	{
		this.isHappened.set(isHappened);
	}
	
	public boolean isHappened()
	{
		return this.isHappened.get();
	}
}
