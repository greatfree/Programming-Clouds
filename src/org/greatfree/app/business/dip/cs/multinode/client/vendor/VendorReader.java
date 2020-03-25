package org.greatfree.app.business.dip.cs.multinode.client.vendor;

import java.io.IOException;

import org.greatfree.app.business.dip.cs.multinode.client.BusinessMessageConfig;
import org.greatfree.chat.ChatConfig;
import org.greatfree.chat.message.cs.business.CheckMerchandiseRequest;
import org.greatfree.chat.message.cs.business.CheckMerchandiseResponse;
import org.greatfree.chat.message.cs.business.CheckPendingOrderRequest;
import org.greatfree.chat.message.cs.business.CheckPendingOrderResponse;
import org.greatfree.chat.message.cs.business.CheckVendorTransactionRequest;
import org.greatfree.chat.message.cs.business.CheckVendorTransactionResponse;
import org.greatfree.client.RemoteReader;
import org.greatfree.data.ClientConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.NodeID;
import org.greatfree.util.ServerStatus;

/*
 * This is the reader that is employed by the vendor to carry out the sales. It is implemented over the mature DIP of the C/S Chatting. 12/20/2017, Bing Li
 */

// Created: 12/20/2017, Bing Li
public class VendorReader
{
	private VendorReader()
	{
	}
	
	/*
	 * Initialize a singleton. 11/27/2014, Bing Li
	 */
	private static VendorReader instance = new VendorReader();
	
	public static VendorReader VR()
	{
		if (instance == null)
		{
			instance = new VendorReader();
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
	 * Check pending orders for one particular vendor. 12/20/2017, Bing Li
	 */
	public CheckPendingOrderResponse checkPendingOrder(String vendorKey)
	{
		try
		{
			return (CheckPendingOrderResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ChatConfig.CHAT_SERVER_ADDRESS, ChatConfig.CHAT_SERVER_PORT, new CheckPendingOrderRequest(vendorKey)));
		}
		catch (ClassNotFoundException | RemoteReadException | IOException e)
		{
			e.printStackTrace();
		}
		return BusinessMessageConfig.NO_CHECK_PENDING_ORDER_RESPONSE;
	}

	/*
	 * Check transactions for one particular vendor. 12/20/2017, Bing Li
	 */
	public CheckVendorTransactionResponse checkVendorTransaction(String vendorKey)
	{
		try
		{
			return (CheckVendorTransactionResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ChatConfig.CHAT_SERVER_ADDRESS, ChatConfig.CHAT_SERVER_PORT, new CheckVendorTransactionRequest(vendorKey)));
		}
		catch (ClassNotFoundException | RemoteReadException | IOException e)
		{
			e.printStackTrace();
		}
		return BusinessMessageConfig.NO_CHECK_VENDOR_TRANSACTION_RESPONSE;
	}
}
