package us.treez.inventory.message;

import org.greatfree.message.container.Notification;

// Created: 02/06/2020, Bing Li
public class RemoveInventoryNotification extends Notification
{
	private static final long serialVersionUID = 7296918584963514170L;
	
	private String merchandiseKey;

	public RemoveInventoryNotification(String merchandiseKey)
	{
		super(BusinessAppID.REMOVE_INVENTORY_NOTIFICATION);
		this.merchandiseKey = merchandiseKey;
	}

	public String getMerchandiseKey()
	{
		return this.merchandiseKey;
	}
}
