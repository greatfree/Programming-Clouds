package org.greatfree.framework.cps.enterprise.bean;

// Created: 04/24/2020, Bing Li
class CustomerEntity
{
	private String customerName;
	private float balance;

	public CustomerEntity(String customerName, float balance)
	{
		this.customerName = customerName;
		this.balance = balance;
	}
	
	public void setCustomerName(String customerName)
	{
		this.customerName = customerName;
	}
	
	public String getCustomerName()
	{
		return this.customerName;
	}
	
	public void setBalance(float balance)
	{
		this.balance = balance;
	}
	
	public float getBalance()
	{
		return this.balance;
	}
}
