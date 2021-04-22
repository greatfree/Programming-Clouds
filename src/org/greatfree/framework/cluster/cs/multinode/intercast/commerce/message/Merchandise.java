package org.greatfree.framework.cluster.cs.multinode.intercast.commerce.message;

import java.io.Serializable;

// Created: 05/18/2019, Bing Li
public class Merchandise implements Serializable
{
	private static final long serialVersionUID = -8941866106900559854L;

	private String merchandiseID;
	private String merchandiseName;
	private String model;
	private String description;
	private float price;
	private int inStock;
	private String manufacturer;
	private String shippingManner;
	
	public Merchandise(String merchandiseID, String merchandiseName, String model, String description, float price, int inStock, String manufacturer, String shippingManner)
	{
		this.merchandiseID = merchandiseID;
		this.merchandiseName = merchandiseName;
		this.model = model;
		this.description = description;
		this.price = price;
		this.inStock = inStock;
		this.manufacturer = manufacturer;
		this.shippingManner = shippingManner;
	}
	
	public String getMerchandiseID()
	{
		return this.merchandiseID;
	}
	
	public String getMerchandiseName()
	{
		return this.merchandiseName;
	}
	
	public String getModel()
	{
		return this.model;
	}
	
	public String getDescription()
	{
		return this.description;
	}
	
	public float getPrice()
	{
		return this.price;
	}
	
	public int getInStock()
	{
		return this.inStock;
	}
	
	public String getManufacturer()
	{
		return this.manufacturer;
	}
	
	public String getShippingManner()
	{
		return this.shippingManner;
	}
}
