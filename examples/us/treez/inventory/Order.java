package us.treez.inventory;

import java.io.Serializable;
import java.util.Date;

import org.greatfree.util.Tools;

// Created: 02/05/2020, Bing Li
public class Order implements Serializable
{
	private static final long serialVersionUID = -3509400010944227053L;

	private String key;
	
	private String merchandiseKey;
	private String customerEmail;
	private Date orderPlacedDate;
	private String status;
	private int orderedCount;
	private float payment;

	public Order(String merchandiseKey, String customerEmail, Date orderPlacedDate, String status, int orderedCount, float payment)
	{
		this.key = Tools.generateUniqueKey();
		this.merchandiseKey = merchandiseKey;
		this.customerEmail = customerEmail;
		this.orderPlacedDate = orderPlacedDate;
		this.status = status;
		this.orderedCount = orderedCount;
		this.payment = payment;
	}

	public Order(String key, String merchandiseKey, String customerEmail, Date orderPlacedDate, String status, int orderedCount, float payment)
	{
		this.key = key;
		this.merchandiseKey = merchandiseKey;
		this.customerEmail = customerEmail;
		this.orderPlacedDate = orderPlacedDate;
		this.status = status;
		this.orderedCount = orderedCount;
		this.payment = payment;
	}

	public String getKey()
	{
		return this.key;
	}
	
	public String getMerchandiseKey()
	{
		return this.merchandiseKey;
	}
	
	public String getCustomerEmail()
	{
		return this.customerEmail;
	}
	
	public Date getOrderPlacedDate()
	{
		return this.orderPlacedDate;
	}
	
	public String getStatus()
	{
		return this.status;
	}
	
	public int getOrderedCount()
	{
		return this.orderedCount;
	}
	
	public float getPayment()
	{
		return this.payment;
	}
}
