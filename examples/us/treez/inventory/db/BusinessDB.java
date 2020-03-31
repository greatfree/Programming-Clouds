package us.treez.inventory.db;

import us.treez.inventory.Inventory;
import us.treez.inventory.Order;

// Created: 02/05/2020, Bing Li
public class BusinessDB
{
	private InventoryDB idb;
	private OrderDB odb;
	
	private BusinessDB()
	{
	}
	
	private static BusinessDB instance = new BusinessDB();
	
	public synchronized static BusinessDB CS()
	{
		if (instance == null)
		{
			instance = new BusinessDB();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void dispose()
	{
		this.idb.dispose();
		this.odb.dispose();
	}
	
	public void init(String idbPath, String odbPath)
	{
		this.idb = new InventoryDB(idbPath);
		this.odb = new OrderDB(odbPath);
	}
	
	public void addInventory(Inventory inventory)
	{
		this.idb.putInventory(inventory);
	}
	
	public void updateInventory(Inventory inventory)
	{
		this.idb.update(inventory);
	}
	
	public void removeInventory(String merchandiseKey)
	{
		this.idb.remove(merchandiseKey);
	}
	
	public synchronized void updateOrder(Order order)
	{
		Inventory iv = this.idb.getInventory(order.getMerchandiseKey());
		if (iv != null)
		{
			idb.update(new Inventory(iv.getKey(), iv.getMerchandiseName(), iv.getDescription(), iv.getPrice(), iv.getQuantity() + order.getOrderedCount()));
			this.odb.putOrder(order);
		}
	}
	
	public synchronized void cancelOrder(String orderKey)
	{
		Order order = this.odb.getOrder(orderKey);
		Inventory iv = this.idb.getInventory(order.getMerchandiseKey());
		if (iv != null)
		{
			idb.update(new Inventory(iv.getKey(), iv.getMerchandiseName(), iv.getDescription(), iv.getPrice(), iv.getQuantity() + order.getOrderedCount()));
		}
		this.odb.remove(orderKey);
	}
	
	public Inventory getInventory(String merchandiseKey)
	{
		return this.idb.getInventory(merchandiseKey);
	}
	
	public synchronized boolean order(Order order)
	{
		Inventory iv = this.idb.getInventory(order.getMerchandiseKey());
		if (iv != null)
		{
			if (iv.getQuantity() >= order.getOrderedCount())
			{
				idb.update(new Inventory(iv.getKey(), iv.getMerchandiseName(), iv.getDescription(), iv.getPrice(), iv.getQuantity() - order.getOrderedCount()));
				this.odb.putOrder(order);
				return true;
			}
		}
		return false;
	}
}
