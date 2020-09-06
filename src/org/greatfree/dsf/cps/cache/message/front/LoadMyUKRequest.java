package org.greatfree.dsf.cps.cache.message.front;

import org.greatfree.dsf.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 02/28/2019, Bing Li
public class LoadMyUKRequest extends ServerMessage
{
	private static final long serialVersionUID = -8069521610710116241L;
	
	private int index;

	public LoadMyUKRequest(int index)
	{
		super(TestCacheMessageType.LOAD_MY_UK_REQUEST);
		this.index = index;
	}

	public int getIndex()
	{
		return this.index;
	}
}
