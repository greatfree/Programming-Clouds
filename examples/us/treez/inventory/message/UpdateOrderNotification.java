package us.treez.inventory.message;

import org.greatfree.message.container.Notification;

import us.treez.inventory.Order;

// Created: 02/05/2020, Bing Li
public class UpdateOrderNotification extends Notification
{
	private static final long serialVersionUID = -6598559715647956744L;
	
	private Order order;

	public UpdateOrderNotification(Order order)
	{
		super(BusinessAppID.UPDATE_ORDER_NOTIFICATION);
		this.order = order;
	}

	public Order getOrder()
	{
		return this.order;
	}
}
