package org.greatfree.framework.cps.enterprise.bean;

// Created: 04/23/2020, Bing Li
class CustomerSession
{
	private CustomerEntity c;
	
	public CustomerSession(CustomerEntity customerEntity)
	{
		this.c = customerEntity;
	}
	
	public CustomerEntity close()
	{
		return this.c;
	}
	
	public float buy(float price, int quantity)
	{
		this.c.setBalance(this.c.getBalance() + price * quantity);
		return this.c.getBalance();
	}
}
