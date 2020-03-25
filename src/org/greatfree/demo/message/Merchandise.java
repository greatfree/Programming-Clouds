package org.greatfree.demo.message;

import java.io.Serializable;

// Created: 01/24/2019, Bing Li
public class Merchandise implements Serializable
{
	private static final long serialVersionUID = 3560756645005092579L;
	
	private String name;
	private int inStock;
	private float price;
	
	public Merchandise(String name, int inStock, float price)
	{
		this.name = name;
		this.inStock = inStock;
		this.price = price;
	}

	public String getName()
	{
		return this.name;
	}
	
	public int getInStock()
	{
		return this.inStock;
	}
	
	public float getPrice()
	{
		return this.price;
	}
}
