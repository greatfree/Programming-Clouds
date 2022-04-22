package org.greatfree.app.business.cs.multinode.client.vendor;

import java.util.Map;
import java.util.Scanner;

import org.greatfree.app.business.cs.multinode.client.UserID;
import org.greatfree.app.business.cs.multinode.server.Merchandise;
import org.greatfree.app.business.cs.multinode.server.MyTransaction;
import org.greatfree.chat.ClientMenu;
import org.greatfree.chat.MenuOptions;
import org.greatfree.chat.client.business.dip.ChatReader;
import org.greatfree.chat.message.cs.business.CheckMerchandiseResponse;
import org.greatfree.chat.message.cs.business.CheckPendingOrderResponse;
import org.greatfree.chat.message.cs.business.CheckVendorTransactionResponse;
import org.greatfree.framework.cs.multinode.message.ChatRegistryResponse;
import org.greatfree.util.Tools;

/*
 * This is the UI for the client of a vendor. 12/21/2017, Bing Li
 */

// Created: 12/21/2017, Bing Li
public class VendorUI
{
	private Scanner in = new Scanner(System.in);

	/*
	 * Initialize. 04/23/2017, Bing Li
	 */
	private VendorUI()
	{
	}

	/*
	 * Initialize a singleton. 04/23/2017, Bing Li
	 */
	private static VendorUI instance = new VendorUI();
	
	public static VendorUI CS()
	{
		if (instance == null)
		{
			instance = new VendorUI();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	public void dispose()
	{
		this.in.close();
	}

	/*
	 * Print the menu list on the screen. 04/23/2017, Bing Li
	 */
	public void printMenu()
	{
		System.out.println(ClientMenu.MENU_HEAD);
		System.out.println(ClientMenu.REGISTER_CHATTING + UserID.CID().getUserName());

		System.out.println(BusinessVendorMenu.POST_MERCHANDISE);
		System.out.println(BusinessVendorMenu.CHECK_MERCHANDISE);
		System.out.println(BusinessVendorMenu.CHECK_PENGING_ORDER);
		System.out.println(BusinessVendorMenu.CHECK_VENDOR_TRANSACTION);
		
		System.out.println(ClientMenu.QUIT);
		System.out.println(ClientMenu.MENU_TAIL);
		System.out.println(ClientMenu.INPUT_PROMPT);
	}

	/*
	 * Send the vendor's option to the server. 12/21/2017, Bing Li
	 */
	public void send(int option)
	{
		ChatRegistryResponse chatRegistryResponse;
		CheckMerchandiseResponse checkMCResponse;
		CheckPendingOrderResponse checkPOResponse;
		CheckVendorTransactionResponse checkVTResponse;
		Map<String, Merchandise> mcs;
		// Check the option to interact with the chatting server. 04/23/2017, Bing Li
		switch (option)
		{
			case MenuOptions.REGISTER_CHATTING:
				chatRegistryResponse = ChatReader.RR().registerChat(UserID.CID().getUserKey(), UserID.CID().getUserName(), UserID.CID().getUserName() + " is a great & free guy!");
				System.out.println("Chatting registry status: " + chatRegistryResponse.isSucceeded());
				break;

			case BusinessVendorOptions.POST_MERCHANDISE:
				VendorEventer.VE().notifyNewMerchandises(UserID.CID().getUserKey(), UserID.CID().getUserName(), new Merchandise(Tools.generateUniqueKey(), "Java Programming", 30.00f, 10));
				break;
				
			case BusinessVendorOptions.CHECK_MERCHANDISE:
				checkMCResponse = VendorReader.VR().checkMerchandises(UserID.CID().getUserKey());
				if (checkMCResponse.getMerchandises() != null)
				{
					for (Map.Entry<String, Merchandise> entry : checkMCResponse.getMerchandises().entrySet())
					{
						System.out.println(entry.getValue().getName() + ", " + entry.getValue().getCount() + ", " + entry.getValue().getPrice());
					}
				}
				else
				{
					System.out.println("No merchandises ...");
				}
				break;
				
			case BusinessVendorOptions.CHECK_PENGING_ORDER:
				checkPOResponse = VendorReader.VR().checkPendingOrder(UserID.CID().getUserKey());
				if (checkPOResponse.getCustomers() != null)
				{
					for (Map.Entry<String, String> cEntry : checkPOResponse.getCustomers().entrySet())
					{
						System.out.println(cEntry.getValue());
						mcs = checkPOResponse.getMerchandises().get(cEntry.getKey());
						for (Map.Entry<String, Merchandise> mEntry : mcs.entrySet())
						{
							System.out.println(mEntry.getValue().getName() + ", " + mEntry.getValue().getCount() + ", " + mEntry.getValue().getPrice());
						}
					}
				}
				else
				{
					System.out.println("No pending orders ...");
				}
				break;
				
			case BusinessVendorOptions.CHECK_VENDOR_TRANSACTION:
				checkVTResponse = VendorReader.VR().checkVendorTransaction(UserID.CID().getUserKey());
				if (checkVTResponse.getTransactions() != null)
				{
					for (Map.Entry<String, MyTransaction> entry : checkVTResponse.getTransactions().entrySet())
					{
						System.out.println(entry.getValue().getVendorName());
						System.out.println(entry.getValue().getCustomerName());
						for (Map.Entry<String, Merchandise> mEntry : entry.getValue().getMerchandises().entrySet())
						{
							System.out.println(mEntry.getValue().getName() + ", " + mEntry.getValue().getCount() + ", " + mEntry.getValue().getPrice());
						}
						System.out.println(entry.getValue().getPayment());
						System.out.println(entry.getValue().getTime());
					}
				}
				else
				{
					System.out.println("No transactions ...");
				}
				break;
		}		
	}
}
