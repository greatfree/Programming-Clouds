package us.treez.inventory.db;

import java.io.File;

import org.greatfree.cache.db.DBEnv;
import org.greatfree.util.FileManager;

import us.treez.inventory.Inventory;
import us.treez.inventory.TreezConfig;

// Created: 02/05/2020, Bing Li
class InventoryDB
{
	private File envPath;
	private DBEnv env;
	private InventoryAccessor accessor;
	
	public InventoryDB(String path)
	{
		if (!FileManager.isDirExisted(path))
		{
			FileManager.makeDir(path);
		}
		this.envPath = new File(path);
		this.env = new DBEnv(this.envPath, false, TreezConfig.DB_CACHE_SIZE, TreezConfig.LOCK_TIME_OUT, TreezConfig.INVENTORY_STORE);
		this.accessor = new InventoryAccessor(this.env.getEntityStore());
	}

	public void dispose()
	{
		this.accessor.dispose();
		this.env.close();
	}
	
	public void putInventory(Inventory inventory)
	{
		this.accessor.getPrimaryIndex().put(new InventoryEntity(inventory.getKey(), inventory.getMerchandiseName(), inventory.getDescription(), inventory.getPrice(), inventory.getQuantity()));
	}
	
	public Inventory getInventory(String key)
	{
		InventoryEntity entry = this.accessor.getPrimaryIndex().get(key);
		if (entry != null)
		{
			return new Inventory(entry.getKey(), entry.getName(), entry.getDescription(), entry.getPrice(), entry.getQuantity());
		}
		return null;
	}
	
	public void update(Inventory inventory)
	{
		this.accessor.getPrimaryIndex().put(new InventoryEntity(inventory.getKey(), inventory.getMerchandiseName(), inventory.getDescription(), inventory.getPrice(), inventory.getQuantity()));
	}
	
	public void remove(String key)
	{
		this.env.getEntityStore().getPrimaryIndex(String.class, InventoryEntity.class).delete(key);
	}
}
