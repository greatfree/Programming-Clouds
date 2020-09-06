package org.greatfree.dsf.cps.cache.message.front;

import java.util.List;

import org.greatfree.dsf.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;
import org.greatfree.testing.cache.local.MyUKValue;

// Created: 02/28/2019, Bing Li
public class SaveMuchUKsNotification extends ServerMessage
{
	private static final long serialVersionUID = 2244968878954067997L;
	
	private List<MyUKValue> uks;

	public SaveMuchUKsNotification(List<MyUKValue> uks)
	{
		super(TestCacheMessageType.SAVE_MUCH_UKS_NOTIFICATION);
		this.uks = uks;
	}

	public List<MyUKValue> getUKs()
	{
		return this.uks;
	}
}
