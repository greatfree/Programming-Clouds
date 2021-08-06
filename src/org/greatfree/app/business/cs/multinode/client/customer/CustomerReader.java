package org.greatfree.app.business.cs.multinode.client.customer;

import java.io.IOException;

import org.greatfree.app.business.cs.multinode.client.BusinessMessageConfig;
import org.greatfree.chat.ChatConfig;
import org.greatfree.chat.message.cs.business.CheckCartRequest;
import org.greatfree.chat.message.cs.business.CheckCartResponse;
import org.greatfree.chat.message.cs.business.CheckCustomerTransactionRequest;
import org.greatfree.chat.message.cs.business.CheckCustomerTransactionResponse;
import org.greatfree.chat.message.cs.business.CheckMerchandiseRequest;
import org.greatfree.chat.message.cs.business.CheckMerchandiseResponse;
import org.greatfree.chat.message.cs.business.CheckSalesRequest;
import org.greatfree.chat.message.cs.business.CheckSalesResponse;
import org.greatfree.client.RemoteReader;
import org.greatfree.data.ClientConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.NodeID;
import org.greatfree.util.ServerStatus;

/*
 * This is the reader that is employed by the customer to carry out the sales. It is implemented over the mature DIP of the C/S Chatting. 12/20/2017, Bing Li
 */

// Created: 12/21/2017, Bing Li
public class CustomerReader
{
	private CustomerReader()
	{
	}
	
	/*
	 * Initialize a singleton. 11/27/2014, Bing Li
	 */
	private static CustomerReader instance = new CustomerReader();
	
	public static CustomerReader CR()
	{
		if (instance == null)
		{
			instance = new CustomerReader();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	/*
	 * Initialize the remote reader to send requests and receive responses from the remote server. 11/23/2014, Bing Li
	 */
	public void init()
	{
		RemoteReader.REMOTE().init(ClientConfig.CLIENT_READER_POOL_SIZE);
	}

	/*
	 * Shutdown the remote reader. 11/23/2014, Bing Li
	 */
	public void shutdown()
	{
		try
		{
			RemoteReader.REMOTE().shutdown();
		}
		catch (IOException e)
		{
			ServerStatus.FREE().printException(e);
		}
	}

	/*
	 * Check merchandises of one particular vendor. 12/20/2017, Bing Li
	 */
	public CheckMerchandiseResponse checkMerchandises(String vendorKey)
	{
		try
		{
			return (CheckMerchandiseResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ChatConfig.CHAT_SERVER_ADDRESS, ChatConfig.CHAT_SERVER_PORT, new CheckMerchandiseRequest(vendorKey)));
		}
		catch (ClassNotFoundException | RemoteReadException | IOException e)
		{
			e.printStackTrace();
		}
		return BusinessMessageConfig.NO_CHECK_MERCHANDISE_RESPONSE;
	}
	
	/*
	 * Check current sales for customers. 12/22/2017, Bing Li
	 */
	public CheckSalesResponse checkSales()
	{
		try
		{
			return (CheckSalesResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ChatConfig.CHAT_SERVER_ADDRESS, ChatConfig.CHAT_SERVER_PORT, new CheckSalesRequest()));
		}
		catch (ClassNotFoundException | RemoteReadException | IOException e)
		{
			e.printStackTrace();
		}
		return BusinessMessageConfig.NO_CHECK_SALES_RESPONSE;
	}
	
	/*
	 * Check merchandises in the cart of a customer. 12/21/2017, Bing Li
	 */
	public CheckCartResponse checkCart(String customerKey, String vendorKey)
	{
		try
		{
			return (CheckCartResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ChatConfig.CHAT_SERVER_ADDRESS, ChatConfig.CHAT_SERVER_PORT, new CheckCartRequest(customerKey, vendorKey)));
		}
		catch (ClassNotFoundException | RemoteReadException | IOException e)
		{
			e.printStackTrace();
		}
		return BusinessMessageConfig.NO_CHECK_CART_RESPONSE;
	}
	
	/*
	 * Check transactions of a customer. 12/21/2017, Bing Li
	 */
	public CheckCustomerTransactionResponse checkCustomerTransaction(String customerKey)
	{
		try
		{
			return (CheckCustomerTransactionResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ChatConfig.CHAT_SERVER_ADDRESS, ChatConfig.CHAT_SERVER_PORT, new CheckCustomerTransactionRequest(customerKey)));
		}
		catch (ClassNotFoundException | RemoteReadException | IOException e)
		{
			e.printStackTrace();
		}
		return BusinessMessageConfig.NO_CHECK_CUSTOMER_TRANSACTION_RESPONSE;
	}
}
