package ca.dp.tncs.client;

import java.io.IOException;

import org.greatfree.client.RemoteReader;
import org.greatfree.data.ClientConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.NodeID;

import ca.dp.tncs.message.Book;
import ca.dp.tncs.message.MerchandiseRequest;
import ca.dp.tncs.message.MerchandiseResponse;
import ca.dp.tncs.message.TNCSConfig;

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
		return ((MerchandiseResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), TNCSConfig.SERVER_IP, TNCSConfig.SERVER_PORT, new MerchandiseRequest(title)))).getBook();
	}
	
}
