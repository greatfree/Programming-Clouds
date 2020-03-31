package us.treez.inventory.server;

import java.io.IOException;
import java.util.Calendar;

import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.container.Notification;
import org.greatfree.message.container.Request;
import org.greatfree.server.container.ServerTask;

import us.treez.inventory.TreezConfig;
import us.treez.inventory.db.BusinessDB;
import us.treez.inventory.message.AddInventoryNotification;
import us.treez.inventory.message.BusinessAppID;
import us.treez.inventory.message.CancelOrderNotification;
import us.treez.inventory.message.MerchandiseRequest;
import us.treez.inventory.message.MerchandiseResponse;
import us.treez.inventory.message.OrderRequest;
import us.treez.inventory.message.OrderResponse;
import us.treez.inventory.message.RemoveInventoryNotification;
import us.treez.inventory.message.UpdateInventoryNotification;
import us.treez.inventory.message.UpdateOrderNotification;

// Created: 02/05/2020, Bing Li
class BusinessTask implements ServerTask
{

	@Override
	public void processNotification(Notification notification)
	{
		switch (notification.getApplicationID())
		{
			case BusinessAppID.UPDATE_ORDER_NOTIFICATION:
				System.out.println("UPDATE_ORDER_NOTIFICATION received @" + Calendar.getInstance().getTime());
				UpdateOrderNotification uon = (UpdateOrderNotification)notification;
				BusinessDB.CS().updateOrder(uon.getOrder());
				break;
				
			case BusinessAppID.CANCEL_ORDER_NOTIFICATION:
				System.out.println("CANCEL_ORDER_NOTIFICATION received @" + Calendar.getInstance().getTime());
				CancelOrderNotification con = (CancelOrderNotification)notification;
				BusinessDB.CS().cancelOrder(con.getOrderKey());
				break;
				
			case BusinessAppID.ADD_INVENTORY_NOTIFICATION:
				System.out.println("ADD_INVENTORY_NOTIFICATION received @" + Calendar.getInstance().getTime());
				AddInventoryNotification ain = (AddInventoryNotification)notification;
				BusinessDB.CS().addInventory(ain.getInventory());
				break;
				
			case BusinessAppID.UPDATE_INVENTORY_NOTIFICATION:
				System.out.println("UPDATE_INVENTORY_NOTIFICATION received @" + Calendar.getInstance().getTime());
				UpdateInventoryNotification uin = (UpdateInventoryNotification)notification;
				BusinessDB.CS().updateInventory(uin.getInventory());
				break;
				
			case BusinessAppID.REMOVE_INVENTORY_NOTIFICATION:
				System.out.println("REMOVE_INVENTORY_NOTIFICATION received @" + Calendar.getInstance().getTime());
				RemoveInventoryNotification rin = (RemoveInventoryNotification)notification;
				BusinessDB.CS().removeInventory(rin.getMerchandiseKey());
				break;
				
			case BusinessAppID.STOP_SERVER_NOTIFICATION:
				System.out.println("STOP_SERVER_NOTIFICATION received @" + Calendar.getInstance().getTime());
				try
				{
					BusinessServer.CS().stop(TreezConfig.TERMINATE_SLEEP);
				}
				catch (ClassNotFoundException | IOException | InterruptedException | RemoteReadException e)
				{
					e.printStackTrace();
				}
				break;
		}
		
	}

	@Override
	public ServerMessage processRequest(Request request)
	{
		switch (request.getApplicationID())
		{
			case BusinessAppID.MERCHANDISE_REQUEST:
				MerchandiseRequest mr = (MerchandiseRequest)request;
				return new MerchandiseResponse(BusinessDB.CS().getInventory(mr.getMerchandiseKey()));
				
			case BusinessAppID.ORDER_REQUEST:
				OrderRequest or = (OrderRequest)request;
				return new OrderResponse(BusinessDB.CS().order(or.getOrder()));
		}
		
		return null;
	}

}
