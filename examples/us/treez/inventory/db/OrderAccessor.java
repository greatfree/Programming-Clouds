package us.treez.inventory.db;

import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;

// Created: 02/05/2020, Bing Li
class OrderAccessor
{
	private PrimaryIndex<String, OrderEntity> primaryIndex;

	public OrderAccessor(EntityStore store)
	{
		this.primaryIndex = store.getPrimaryIndex(String.class, OrderEntity.class);
	}

	public void dispose()
	{
		this.primaryIndex = null;
	}

	public PrimaryIndex<String, OrderEntity> getPrimaryIndex()
	{
		return this.primaryIndex;
	}
}
