package us.treez.inventory;

import java.io.Serializable;

import org.greatfree.util.Tools;

// Created: 02/05/2020, Bing Li
public class Inventory implements Serializable
{
	private static final long serialVersionUID = -4931473359211271480L;

	private String key;
	private String merchandiseName;
	private String description;
	private float price;
	private int quantity;

	public Inventory(String name, String description, float price, int quantity)
	{
		this.key = Tools.getHash(name);
		this.merchandiseName = name;
		this.description = description;
		this.price = price;
		this.quantity = quantity;
	}

	public Inventory(String key, String name, String description, float price, int quantity)
	{
		this.key = key;
		this.merchandiseName = name;
		this.description = description;
		this.price = price;
		this.quantity = quantity;
	}

	public String getKey()
	{
		return this.key;
	}
	
	public String getMerchandiseName()
	{
		return this.merchandiseName;
	}
	
	public String getDescription()
	{
		return this.description;
	}
	
	public float getPrice()
	{
		return this.price;
	}
	
	public int getQuantity()
	{
		return this.quantity;
	}
	
	public String toString()
	{
		return this.key + ", " + this.merchandiseName + ", " + this.description + ", " + this.price + ", " + this.quantity;
	}
}
