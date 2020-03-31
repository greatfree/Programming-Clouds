package us.treez.inventory.message;

import org.greatfree.message.ServerMessage;

// Created: 02/05/2020, Bing Li
public class OrderResponse extends ServerMessage
{
	private static final long serialVersionUID = 6445486524821868097L;
	
	private boolean isSufficient;

	public OrderResponse(boolean isSufficient)
	{
		super(BusinessAppID.ORDER_RESPONSE);
		this.isSufficient = isSufficient;
	}

	public boolean isSufficient()
	{
		return this.isSufficient;
	}
}
