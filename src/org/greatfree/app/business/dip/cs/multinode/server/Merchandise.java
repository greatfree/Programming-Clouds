package org.greatfree.app.business.dip.cs.multinode.server;

import java.io.Serializable;

// Created: 12/05/2017, Bing Li
public class Merchandise implements Serializable
{
	private static final long serialVersionUID = 1802064631587482791L;
	
	private String key;
	private String name;
	private float price;
	private int count;
	
	public Merchandise(String key, String name, float price, int count)
	{
		this.key = key;
		this.name = name;
		this.price = price;
		this.count = count;
	}

	public String getKey()
	{
		return this.key;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public float getPrice()
	{
		return this.price;
	}
	
	public int getCount()
	{
		return this.count;
	}
	
	public void setCount(int count)
	{
		this.count = count;
	}
}
