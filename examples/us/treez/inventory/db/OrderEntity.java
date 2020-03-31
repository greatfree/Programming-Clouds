package us.treez.inventory.db;

import static com.sleepycat.persist.model.Relationship.MANY_TO_ONE;

import java.util.Date;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.SecondaryKey;

// Created: 02/05/2020, Bing Li
@Entity
class OrderEntity
{
	@PrimaryKey
	private String key;

	@SecondaryKey(relate=MANY_TO_ONE)
	private String mechandiseKey;

	private String customerEmail;
	private Date orderPlacedDate;
	private String status;
	private int orderedCount;
	private float payment;
	
	public OrderEntity()
	{
	}
	
	public OrderEntity(String key, String merchandiseKey, String customerEmail, Date orderPlacedDate, String status, int orderedCount, float payment)
	{
		this.key = key;
		this.mechandiseKey = merchandiseKey;
		this.customerEmail = customerEmail;
		this.orderPlacedDate = orderPlacedDate;
		this.status = status;
		this.orderedCount = orderedCount;
		this.payment = payment;
	}
	
	public void setKey(String key)
	{
		this.key = key;
	}
	
	public String getKey()
	{
		return this.key;
	}
	
	public void setMerchandiseKey(String merchandiseKey)
	{
		this.mechandiseKey = merchandiseKey;
	}
	
	public String getMerchandiseKey()
	{
		return this.mechandiseKey;
	}
	
	public void setCustomerEmail(String customerEmail)
	{
		this.customerEmail = customerEmail;
	}
	
	public String getCustomerEmail()
	{
		return this.customerEmail;
	}
	
	public void setOrderPlacedDate(Date orderPlacedDate)
	{
		this.orderPlacedDate = orderPlacedDate;
	}
	
	public Date getOrderPlacedDate()
	{
		return this.orderPlacedDate;
	}
	
	public void setStatus(String status)
	{
		this.status = status;
	}
	
	public String getStatus()
	{
		return this.status;
	}
	
	public void setOrderCount(int orderedCount)
	{
		this.orderedCount = orderedCount;
	}
	
	public int getOrderedCount()
	{
		return this.orderedCount;
	}

	public void setPayment(float payment)
	{
		this.payment = payment;
	}
	
	public float getPayment()
	{
		return this.payment;
	}
}
