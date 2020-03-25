package org.greatfree.app.cs.twonode.message;

import org.greatfree.message.ServerMessage;

// Created: 07/31/2018, Bing Li
public class OrderNotification extends ServerMessage
{
	private static final long serialVersionUID = -617027902810944454L;
	
	private String merchandise;
	private int quantity;

	public OrderNotification(String merchandise, int quantity)
	{
		super(BusinessMessageType.ORDER_NOTIFICATION);
		this.merchandise = merchandise;
		this.quantity = quantity;
	}

	public String getMerchandise()
	{
		return this.merchandise;
	}
	
	public int getQuantity()
	{
		return this.quantity;
	}
}
