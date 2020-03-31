package us.treez.inventory.message;

import org.greatfree.message.container.Notification;

// Created: 02/05/2020, Bing Li
public class CancelOrderNotification extends Notification
{
	private static final long serialVersionUID = 3520342853706745892L;
	
	private String orderKey;

	public CancelOrderNotification(String orderKey)
	{
		super(BusinessAppID.CANCEL_ORDER_NOTIFICATION);
		this.orderKey = orderKey;
	}

	public String getOrderKey()
	{
		return this.orderKey;
	}
}
