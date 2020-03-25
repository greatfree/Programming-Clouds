package org.greatfree.dip.cps.cache.message.front;

import org.greatfree.dip.cps.cache.data.MyCachePointing;
import org.greatfree.dip.cps.cache.data.MyCacheTiming;
import org.greatfree.dip.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 07/24/2018, Bing Li
public class SaveCachePointingNotification extends ServerMessage
{
	private static final long serialVersionUID = 5793023307809580698L;
	
	private String mapKey;
	private MyCachePointing pointing;
	private MyCacheTiming timing;

	public SaveCachePointingNotification(String mapKey, MyCachePointing pointing)
	{
		super(TestCacheMessageType.SAVE_MY_CACHE_POINTING_MAP_NOTIFICATION);
		this.mapKey = mapKey;
		this.pointing = pointing;
	}

	public SaveCachePointingNotification(String mapKey, MyCacheTiming timing)
	{
		super(TestCacheMessageType.SAVE_MY_CACHE_POINTING_MAP_NOTIFICATION);
		this.mapKey = mapKey;
		this.timing = timing;
	}
	
	public String getMapKey()
	{
		return this.mapKey;
	}

	public MyCachePointing getPointing()
	{
		return this.pointing;
	}
	
	public MyCacheTiming getTiming()
	{
		return this.timing;
	}
}
