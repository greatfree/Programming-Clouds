package us.treez.inventory.db;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

// Created: 02/05/2020, Bing Li
@Entity
class InventoryEntity
{
	@PrimaryKey
	private String key;

	private String name;
	private String description;
	private float price;
	private int quantity;
	
	public InventoryEntity()
	{
	}
	
	public InventoryEntity(String key, String name, String description, float price, int quantity)
	{
		this.key = key;
		this.name = name;
		this.description = description;
		this.price = price;
		this.quantity = quantity;
	}

	public void setKey(String key)
	{
		this.key = key;
	}
	
	public String getKey()
	{
		return this.key;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public void setDescription(String description)
	{
		this.description = description;
	}
	
	public String getDescription()
	{
		return this.description;
	}
	
	public void setPrice(float price)
	{
		this.price = price;
	}

	public float getPrice()
	{
		return this.price;
	}
	
	public void setQuantity(int quantity)
	{
		this.quantity = quantity;
	}
	
	public int getQuantity()
	{
		return this.quantity;
	}
}
