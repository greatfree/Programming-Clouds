package org.greatfree.dip.cps.enterprise.bean;

// Created: 04/24/2020, Bing Li
class VendorEntity
{
	private String vendorName;
	private String merchandise;
	private int inStock;
	private float price;
	private float income;

	public VendorEntity(String vName, String mcd, int inStock, float price)
	{
		this.vendorName = vName;
		this.merchandise = mcd;
		this.inStock = inStock;
		this.price = price;
		this.income = 0;
	}
	
	public void setVendorName(String vn)
	{
		this.vendorName = vn;
	}

	public String getVendorName()
	{
		return this.vendorName;
	}
	
	public void setMerchandise(String merchandise)
	{
		this.merchandise = merchandise;
	}
	
	public String getMerchandise()
	{
		return this.merchandise;
	}
	
	public void setInStock(int inStock)
	{
		this.inStock = inStock;
	}
	
	public int getInStock()
	{
		return this.inStock;
	}
	
	public void setPrice(float price)
	{
		this.price = price;
	}
	
	public float getPrice()
	{
		return this.price;
	}
	
	public void setIncome(float income)
	{
		this.income = income;
	}
	
	public float getIncome()
	{
		return this.income;
	}
}
