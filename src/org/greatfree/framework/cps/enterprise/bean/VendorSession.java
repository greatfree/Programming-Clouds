package org.greatfree.framework.cps.enterprise.bean;

// Created: 04/23/2020, Bing Li
class VendorSession
{
	private VendorEntity v;

	public VendorSession(VendorEntity vendorEntity)
	{
		this.v = vendorEntity;
	}
	
	public VendorEntity close()
	{
		return this.v;
	}
	
	public boolean sell(String vName, String mcd, int quantity)
	{
		if (this.v.getVendorName().equals(vName))
		{
			if (this.v.getMerchandise().equals(mcd))
			{
				if (this.v.getInStock() > quantity)
				{
					this.v.setIncome(this.v.getInStock() - quantity);
					this.v.setIncome(this.v.getIncome() + this.v.getPrice() * quantity);
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean updateMerchandise(String vName, String mcd, int quantity, float price)
	{
		if (this.v.getVendorName().equals(vName))
		{
			if (this.v.getMerchandise().equals(mcd))
			{
				this.v.setInStock(this.v.getInStock() + quantity);
			}
			else
			{
				this.v.setMerchandise(mcd);
				this.v.setInStock(quantity);
			}
			this.v.setPrice(price);
		}
		return true;
	}
	
	public float getIncome()
	{
		return this.v.getIncome();
	}
}
