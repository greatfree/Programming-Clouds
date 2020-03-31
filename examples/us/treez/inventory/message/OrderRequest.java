package us.treez.inventory.message;

import org.greatfree.message.container.Request;

import us.treez.inventory.Order;

// Created: 02/05/2020, Bing Li
public class OrderRequest extends Request
{
	private static final long serialVersionUID = -6064537203693282876L;
	
	private Order order;

	public OrderRequest(Order order)
	{
		super(BusinessAppID.ORDER_REQUEST);
		this.order = order;
	}

	public Order getOrder()
	{
		return this.order;
	}
}
