package org.greatfree.app.cs.twonode.message;

import java.io.Serializable;

// Created: 07/31/2018, Bing Li
public class Merchandise implements Serializable
{
	private static final long serialVersionUID = -3017919476230672377L;
	
	private String merchandise;
	private int inStockQuantity;
	
	public Merchandise(String merchandise, int inStockQuantity)
	{
		this.merchandise = merchandise;
		this.inStockQuantity = inStockQuantity;
	}

	public String getMerchandise()
	{
		return this.merchandise;
	}
	
	public int getInStockQuantity()
	{
		return this.inStockQuantity;
	}
}
