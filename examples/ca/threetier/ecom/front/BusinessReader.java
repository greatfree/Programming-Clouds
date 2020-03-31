package ca.threetier.ecom.front;

import java.io.IOException;

import org.greatfree.client.RemoteReader;
import org.greatfree.data.ClientConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.NodeID;

import ca.dp.tncs.message.Book;
import ca.dp.tncs.message.MerchandiseRequest;
import ca.dp.tncs.message.MerchandiseResponse;
import ca.threetier.ecom.message.ThreeTierConfig;

// Created: 02/22/2020, Bing Li
class BusinessReader
{
	public static void init()
	{
		RemoteReader.REMOTE().init(ClientConfig.CLIENT_READER_POOL_SIZE);
	}

	public static void shutdown() throws IOException
	{
		RemoteReader.REMOTE().shutdown();
	}
	
	public static Book getBook(String title) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return ((MerchandiseResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ThreeTierConfig.MIDDLE_TIER_IP, ThreeTierConfig.MIDDLE_TIER_PORT, new MerchandiseRequest(title)))).getBook();
	}
	
}
