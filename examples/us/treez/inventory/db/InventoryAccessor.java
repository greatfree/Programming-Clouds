package us.treez.inventory.db;

import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;

// Created: 02/05/2020, Bing Li
class InventoryAccessor
{
	private PrimaryIndex<String, InventoryEntity> primaryIndex;

	public InventoryAccessor(EntityStore store)
	{
		this.primaryIndex = store.getPrimaryIndex(String.class, InventoryEntity.class);
	}

	public void dispose()
	{
		this.primaryIndex = null;
	}

	public PrimaryIndex<String, InventoryEntity> getPrimaryIndex()
	{
		return this.primaryIndex;
	}
}
