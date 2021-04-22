package org.greatfree.app.business.dip.cs.multinode.client.customer;

import java.util.Map;
import java.util.Scanner;

import org.greatfree.app.business.dip.cs.multinode.client.CustomerCookies;
import org.greatfree.app.business.dip.cs.multinode.client.UserID;
import org.greatfree.app.business.dip.cs.multinode.server.Merchandise;
import org.greatfree.app.business.dip.cs.multinode.server.Transaction;
import org.greatfree.chat.ClientMenu;
import org.greatfree.chat.MenuOptions;
import org.greatfree.chat.client.business.dip.ChatReader;
import org.greatfree.chat.message.cs.business.CheckCartResponse;
import org.greatfree.chat.message.cs.business.CheckCustomerTransactionResponse;
import org.greatfree.chat.message.cs.business.CheckSalesResponse;
import org.greatfree.framework.cs.multinode.message.ChatRegistryResponse;
import org.greatfree.util.Tools;
import org.greatfree.util.UtilConfig;

// Created: 12/22/2017, Bing Li
public class CustomerUI
{
	private Scanner in = new Scanner(System.in);

	/*
	 * Initialize. 04/23/2017, Bing Li
	 */
	private CustomerUI()
	{
	}

	/*
	 * Initialize a singleton. 04/23/2017, Bing Li
	 */
	private static CustomerUI instance = new CustomerUI();
	
	public static CustomerUI CS()
	{
		if (instance == null)
		{
			instance = new CustomerUI();
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

		System.out.println(BusinessCustomerMenu.CHECK_SALES);
		System.out.println(BusinessCustomerMenu.PUT_INTO_CART);
		System.out.println(BusinessCustomerMenu.CHECK_CART);
		System.out.println(BusinessCustomerMenu.REMOVE_FROM_CART);
		System.out.println(BusinessCustomerMenu.PLACE_ORDER);
		System.out.println(BusinessCustomerMenu.CHCEK_CUSTOMER_TRANSACTION);
		
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
		CheckSalesResponse checkSalesResponse;
		CheckCartResponse checkCTResponse;
		CheckCustomerTransactionResponse checkCTRResponse;
		Map<String, Merchandise> mcs;
		// Check the option to interact with the chatting server. 04/23/2017, Bing Li
		switch (option)
		{
			case MenuOptions.REGISTER_CHATTING:
				chatRegistryResponse = ChatReader.RR().registerChat(UserID.CID().getUserKey(), UserID.CID().getUserName(), UserID.CID().getUserName() + " is a great & free guy!");
				System.out.println("Chatting registry status: " + chatRegistryResponse.isSucceeded());
				break;
				
			case BusinessCustomerOptions.CHECK_SALES:
				checkSalesResponse = CustomerReader.CR().checkSales();
				if (checkSalesResponse.getVendors() != null)
				{
					for (Map.Entry<String, String> vEntry : checkSalesResponse.getVendors().entrySet())
					{
						System.out.println(vEntry.getValue());
						mcs = checkSalesResponse.getSales().get(vEntry.getKey());
						for (Map.Entry<String, Merchandise> mEntry : mcs.entrySet())
						{
							System.out.println(mEntry.getValue().getName() + ", " + mEntry.getValue().getCount() + ", " + mEntry.getValue().getPrice());
						}
					}
					CustomerCookies.STATE().setVendorKey(Tools.getRandomSetElement(checkSalesResponse.getVendors().keySet()));
					mcs = checkSalesResponse.getSales().get(CustomerCookies.STATE().getVendorKey());
					CustomerCookies.STATE().setMerchandiseKey(Tools.getRandomSetElement(mcs.keySet()));
					System.out.println("merchandiseKey = " + CustomerCookies.STATE().getMerchandiseKey());
				}
				else
				{
					System.out.println("No sales ...");
				}
				break;

			case BusinessCustomerOptions.PUT_INTO_CART:
				if (!CustomerCookies.STATE().getMerchandiseKey().equals(UtilConfig.EMPTY_STRING))
				{
					CustomerEventer.CE().notifyPutIntoCart(UserID.CID().getUserKey(), CustomerCookies.STATE().getVendorKey(), CustomerCookies.STATE().getMerchandiseKey(), 3);
				}
				else
				{
					System.out.println("No merchandise for shopping ..");
				}
				break;
				
			case BusinessCustomerOptions.CHECK_CART:
				checkCTResponse = CustomerReader.CR().checkCart(UserID.CID().getUserKey()	, CustomerCookies.STATE().getVendorKey());
				if (checkCTResponse.getMerchandises() != null)
				{
					for (Map.Entry<String, Merchandise> entry : checkCTResponse.getMerchandises().entrySet())
					{
						System.out.println(entry.getValue().getName() + ", " + entry.getValue().getCount() + ", " + entry.getValue().getPrice());
					}
				}
				else
				{
					System.out.println("No merchandises in cart ...");
				}
				break;
				
			case BusinessCustomerOptions.REMOVE_FROM_CART:
				CustomerEventer.CE().notifyRemoveFromCart(UserID.CID().getUserKey(), CustomerCookies.STATE().getVendorKey(), CustomerCookies.STATE().getMerchandiseKey(), 2);
				break;
				
			case BusinessCustomerOptions.PLACE_ORDER:
				CustomerEventer.CE().notifyPlaceOrder(CustomerCookies.STATE().getVendorKey(), UserID.CID().getUserKey());
				break;

			case BusinessCustomerOptions.CHCEK_CUSTOMER_TRANSACTION:
				checkCTRResponse = CustomerReader.CR().checkCustomerTransaction(UserID.CID().getUserKey());
				if (checkCTRResponse.getTransactions() != null)
				{
					for (Map.Entry<String, Transaction> entry : checkCTRResponse.getTransactions().entrySet())
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
