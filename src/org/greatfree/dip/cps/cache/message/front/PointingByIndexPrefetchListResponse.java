package org.greatfree.dip.cps.cache.message.front;

import org.greatfree.dip.cps.cache.data.MyCachePointing;
import org.greatfree.dip.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 08/03/2018, Bing Li
public class PointingByIndexPrefetchListResponse extends ServerMessage
{
	private static final long serialVersionUID = -3439344879442876877L;

	private MyCachePointing pointing;

	public PointingByIndexPrefetchListResponse(MyCachePointing pointing)
	{
		super(TestCacheMessageType.POINTING_BY_INDEX_PREFETCH_LIST_RESPONSE);
		this.pointing = pointing;
	}

	public MyCachePointing getPointing()
	{
		return this.pointing;
	}
}
