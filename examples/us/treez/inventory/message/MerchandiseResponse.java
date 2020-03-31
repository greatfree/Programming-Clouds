package us.treez.inventory.message;

import org.greatfree.message.ServerMessage;

import us.treez.inventory.Inventory;

// Created: 02/05/2020, Bing Li
public class MerchandiseResponse extends ServerMessage
{
	private static final long serialVersionUID = -3267339914110988141L;
	
	private Inventory iv;

	public MerchandiseResponse(Inventory iv)
	{
		super(BusinessAppID.MERCHANDISE_RESPONSE);
		this.iv = iv;
	}

	public Inventory getInventory()
	{
		return this.iv;
	}
}
