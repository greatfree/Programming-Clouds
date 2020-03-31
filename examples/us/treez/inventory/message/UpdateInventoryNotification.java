package us.treez.inventory.message;

import org.greatfree.message.container.Notification;

import us.treez.inventory.Inventory;

// Created: 02/05/2020, Bing Li
public class UpdateInventoryNotification extends Notification
{
	private static final long serialVersionUID = 4746598666330296524L;
	
	private Inventory iv;

	public UpdateInventoryNotification(Inventory iv)
	{
		super(BusinessAppID.UPDATE_INVENTORY_NOTIFICATION);
		this.iv = iv;
	}

	public Inventory getInventory()
	{
		return this.iv;
	}
}
