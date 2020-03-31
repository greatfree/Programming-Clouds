package us.treez.inventory.message;

import org.greatfree.message.container.Notification;

import us.treez.inventory.Inventory;

// Created: 02/05/2020, Bing Li
public class AddInventoryNotification extends Notification
{
	private static final long serialVersionUID = -6063112051639579232L;
	
	private Inventory iv;

	public AddInventoryNotification(Inventory iv)
	{
		super(BusinessAppID.ADD_INVENTORY_NOTIFICATION);
		this.iv = iv;
	}

	public Inventory getInventory()
	{
		return this.iv;
	}
}
